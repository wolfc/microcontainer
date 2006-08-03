/*
* JBoss, Home of Professional Open Source
* Copyright 2006, JBoss Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
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
package org.jboss.test.metadata.loader.threadlocal.test;

import java.util.concurrent.CountDownLatch;

import org.jboss.metadata.plugins.loader.thread.ThreadLocalMetaDataLoader;
import org.jboss.test.metadata.AbstractMetaDataTest;

/**
 * ThreadLocalLoaderUnitTestCase.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision$
 */
public class ThreadLocalLoaderUnitTestCase extends AbstractMetaDataTest
{
   ThreadLocalMetaDataLoader loader = ThreadLocalMetaDataLoader.INSTANCE;
   
   CountDownLatch entryLatch;
   CountDownLatch initLatch;
   
   public ThreadLocalLoaderUnitTestCase(String name)
   {
      super(name);
   }
   
   public void testMetaDataConcurrent() throws Exception
   {
      TestMetaDataRunnable[] runnables = new TestMetaDataRunnable[5];
      Thread[] threads = new Thread[runnables.length];
      entryLatch = new CountDownLatch(runnables.length);
      initLatch = new CountDownLatch(runnables.length);
      for (int i = 0; i < 5; ++i)
      {
         runnables[i] = new TestMetaDataRunnable();
         threads[i] = new Thread(runnables[i], "Thread" + i);
         threads[i].start();
      }
      
      for (int i = 0; i< 5; ++i)
      {
         threads[i].join();
         assertNull(runnables[i].error);
      }
   }
   
   public class TestMetaDataRunnable implements Runnable
   {
      Throwable error;
      
      public void run()
      {
         try
         {
            entryLatch.countDown();
            entryLatch.await();
            
            loader.addMetaData(Thread.currentThread(), Thread.class);

            initLatch.countDown();
            initLatch.await();
            
            assertEquals(Thread.currentThread(), loader.removeMetaData(Thread.class));
         }
         catch (Throwable t)
         {
            log.error("Error", t);
            error = t;
         }
      }
   }
}
