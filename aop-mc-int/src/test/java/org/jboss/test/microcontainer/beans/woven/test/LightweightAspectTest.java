/*
* JBoss, Home of Professional Open Source.
* Copyright 2006, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.test.microcontainer.beans.woven.test;

import org.jboss.test.aop.junit.AOPMicrocontainerTest;
import org.jboss.test.microcontainer.beans.Interceptions;
import org.jboss.test.microcontainer.beans.LightweightAspectPOJO;

/**
 * 
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
public class LightweightAspectTest extends AOPMicrocontainerTest
{
   public LightweightAspectTest(String name)
   {
      super(name);
   }

   public void testLightweightAspect() throws Throwable
   {
      LightweightAspectPOJO pojo = new LightweightAspectPOJO();
      
      Interceptions.reset();
      pojo.method(false);
      assertEquals(3, Interceptions.interceptions().size());
      assertEquals("BEFORE", Interceptions.interceptions().get(0));
      assertEquals("AFTER", Interceptions.interceptions().get(1));
      assertEquals("FINALLY", Interceptions.interceptions().get(2));
      
      Interceptions.reset();
      try
      {
         pojo.method(true);
      }
      catch (Exception expected)
      {
      }
      assertEquals(3, Interceptions.interceptions().size());
      assertEquals("BEFORE", Interceptions.interceptions().get(0));
      assertEquals("THROWING", Interceptions.interceptions().get(1));
      assertEquals("FINALLY", Interceptions.interceptions().get(2));
      
   }
}
