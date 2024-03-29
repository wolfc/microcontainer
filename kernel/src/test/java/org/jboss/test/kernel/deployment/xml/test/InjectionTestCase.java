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
package org.jboss.test.kernel.deployment.xml.test;

import java.util.Set;

import junit.framework.Test;
import org.jboss.beans.metadata.api.model.FromContext;
import org.jboss.beans.metadata.plugins.AbstractBeanMetaData;
import org.jboss.beans.metadata.plugins.AbstractDependencyValueMetaData;
import org.jboss.beans.metadata.plugins.AbstractInjectionValueMetaData;
import org.jboss.beans.metadata.spi.PropertyMetaData;
import org.jboss.beans.metadata.spi.ValueMetaData;
import org.jboss.dependency.plugins.graph.Search;
import org.jboss.dependency.spi.ControllerState;

/**
 * InjectionTestCase.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision$
 */
public class InjectionTestCase extends AbstractXMLTest
{
   protected AbstractInjectionValueMetaData getInjection(String name) throws Exception
   {
      AbstractBeanMetaData bean = unmarshalBean(name);
      Set<PropertyMetaData> properties = bean.getProperties();
      assertNotNull(properties);
      assertEquals(1, properties.size());
      PropertyMetaData property = properties.iterator().next();
      assertNotNull(property);
      ValueMetaData value = property.getValue();
      assertNotNull(value);
      assertTrue(value instanceof AbstractInjectionValueMetaData);
      return (AbstractInjectionValueMetaData) value;
   }

   public void testInjectionWithBean() throws Exception
   {
      AbstractDependencyValueMetaData dependency = getInjection("InjectionWithBean.xml");
      assertEquals("Bean1", dependency.getValue());
      assertNull(dependency.getProperty());
      assertNull(dependency.getDependentState());
   }

   public void testInjectionWithProperty() throws Exception
   {
      AbstractDependencyValueMetaData dependency = getInjection("InjectionWithProperty.xml");
      assertEquals("Dummy", dependency.getValue());
      assertEquals("Property1", dependency.getProperty());
      assertNull(dependency.getDependentState());
   }

   public void testInjectionWithState() throws Exception
   {
      AbstractDependencyValueMetaData dependency = getInjection("InjectionWithState.xml");
      assertEquals("Dummy", dependency.getValue());
      assertNull(dependency.getProperty());
      assertEquals(ControllerState.CONFIGURED, dependency.getDependentState());
   }

   public void testInjectionBadNoBean() throws Exception
   {
      try
      {
         AbstractDependencyValueMetaData dependency = getInjection("InjectionBadNoBean.xml");
         assertNull(dependency.getValue());                  
      }
      catch (Exception expected)
      {
         checkJBossXBException(IllegalArgumentException.class, expected);
      }
   }

   public void testInjectionWithFromContext() throws Exception
   {
      AbstractInjectionValueMetaData dependency = getInjection("InjectionWithFromContext.xml");
      assertEquals("Dummy", dependency.getValue());
      assertNull(dependency.getProperty());
      assertEquals(FromContext.getInstance("name"), dependency.getFromContext());
   }

   public void testInjectionWithSearch() throws Exception
   {
      AbstractInjectionValueMetaData dependency = getInjection("InjectionWithSearch.xml");
      assertEquals("Dummy", dependency.getValue());
      assertEquals(Search.LEAVES, dependency.getSearch());
   }

   public static Test suite()
   {
      return suite(InjectionTestCase.class);
   }

   public InjectionTestCase(String name)
   {
      super(name);
   }

   protected InjectionTestCase(String name, boolean useClone)
   {
      super(name, useClone);
   }
}
