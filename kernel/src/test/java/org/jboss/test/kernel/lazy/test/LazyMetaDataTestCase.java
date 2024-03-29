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
package org.jboss.test.kernel.lazy.test;

import junit.framework.Test;
import org.jboss.test.kernel.junit.MicrocontainerTest;
import org.jboss.test.kernel.lazy.support.IRare;
import org.jboss.test.kernel.lazy.support.RareBean;

/**
 * Test lazy metadata.
 * 
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public class LazyMetaDataTestCase extends MicrocontainerTest
{
   public LazyMetaDataTestCase(String name)
   {
      super(name);
   }

   public static Test suite()
   {
      return suite(LazyMetaDataTestCase.class);
   }

   public void testLazyXMLTest() throws Throwable
   {
      Object proxy = getBean("proxy");
      assertNotNull(proxy);
      assertInstanceOf(proxy, IRare.class);
      assertInstanceOf(proxy, RareBean.class);
      RareBean rare = (RareBean)proxy;

      RareBean bean = (RareBean)getBean("bean");
      assertNotNull(bean);

      assertEquals(bean.getHits(), rare.getHits());
      bean.setHits(123);
      assertEquals(bean.getHits(), rare.getHits());
      bean.setHits(321);
      assertEquals(bean.getHits(), rare.getHits());

      Object holder = getBean("holder");
      assertNotNull(holder);
      assertInstanceOf(holder, IRare.class);
   }
}
