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
package org.jboss.dependency.plugins;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
class DummyExecutorService implements ExecutorService
{
   public void shutdown()
   {
      throw new UnsupportedOperationException("NYI");
   }

   public List<Runnable> shutdownNow()
   {
      throw new UnsupportedOperationException("NYI");
   }

   public boolean isShutdown()
   {
      throw new UnsupportedOperationException("NYI");
   }

   public boolean isTerminated()
   {
      throw new UnsupportedOperationException("NYI");
   }

   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException
   {
      throw new UnsupportedOperationException("NYI");
   }

   public <T> Future<T> submit(final Callable<T> task)
   {
      return new Future<T>()
      {
         public boolean cancel(boolean mayInterruptIfRunning)
         {
            throw new UnsupportedOperationException("NYI");
         }

         public boolean isCancelled()
         {
            throw new UnsupportedOperationException("NYI");
         }

         public boolean isDone()
         {
            throw new UnsupportedOperationException("NYI");
         }

         public T get() throws InterruptedException, ExecutionException
         {
            try
            {
               return task.call();
            }
            catch (Exception e)
            {
               throw new ExecutionException(e);
            }
         }

         public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
         {
            throw new UnsupportedOperationException("NYI");
         }
      };
   }

   public <T> Future<T> submit(Runnable task, T result)
   {
      throw new UnsupportedOperationException("NYI");
   }

   public Future<?> submit(Runnable task)
   {
      throw new UnsupportedOperationException("NYI");
   }

   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException
   {
      throw new UnsupportedOperationException("NYI");
   }

   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException
   {
      throw new UnsupportedOperationException("NYI");
   }

   public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException
   {
      throw new UnsupportedOperationException("NYI");
   }

   public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
   {
      throw new UnsupportedOperationException("NYI");
   }

   public void execute(Runnable command)
   {
      throw new UnsupportedOperationException("NYI");
   }
}
