/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.dependency.internal;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class TCCLExecutorService extends AbstractExecutorService
{
   private final ExecutorService delegate;

   private class TCCLFuture<V> extends FutureTask<V>
   {
      private final ClassLoader classLoader;
      private final Object task;

      TCCLFuture(final Callable<V> callable, final ClassLoader classLoader)
      {
         super(callable);
         this.classLoader = classLoader;
         this.task = callable;
      }

      TCCLFuture(final Runnable task, final V value, final ClassLoader classLoader)
      {
         super(task, value);
         this.classLoader = classLoader;
         this.task = task;
      }

      @Override
      public void run()
      {
         final ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
         Thread.currentThread().setContextClassLoader(classLoader);
         try
         {
            super.run();
         }
         finally
         {
            Thread.currentThread().setContextClassLoader(previousClassLoader);
         }
      }

      @Override
      public String toString()
      {
         return "Future of " + task.toString();
      }
   }

   public TCCLExecutorService(final ExecutorService delegate)
   {
      if (delegate == null)
         throw new NullPointerException();
      this.delegate = delegate;
   }

   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException
   {
      return delegate.awaitTermination(timeout, unit);
   }

   @Override
   public void execute(Runnable command)
   {
      delegate.execute(command);
   }

   @Override
   public boolean isShutdown()
   {
      return delegate.isShutdown();
   }

   @Override
   public boolean isTerminated()
   {
      return delegate.isTerminated();
   }

   @Override
   protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value)
   {
      final ClassLoader cl = Thread.currentThread().getContextClassLoader();
      return new TCCLFuture(runnable, value, cl);
   }

   @Override
   protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable)
   {
      final ClassLoader cl = Thread.currentThread().getContextClassLoader();
      return new TCCLFuture(callable, cl);
   }

   @Override
   public void shutdown()
   {
      delegate.shutdown();
   }

   @Override
   public List<Runnable> shutdownNow()
   {
      return delegate.shutdownNow();
   }
}
