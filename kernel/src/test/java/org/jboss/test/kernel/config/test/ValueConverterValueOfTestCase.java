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
package org.jboss.test.kernel.config.test;

import java.util.HashSet;

import junit.framework.Test;

import org.jboss.beans.metadata.plugins.AbstractBeanMetaData;
import org.jboss.beans.metadata.plugins.AbstractPropertyMetaData;
import org.jboss.beans.metadata.plugins.StringValueMetaData;
import org.jboss.beans.metadata.spi.PropertyMetaData;
import org.jboss.dependency.spi.ControllerState;
import org.jboss.kernel.Kernel;
import org.jboss.kernel.spi.config.KernelConfigurator;
import org.jboss.test.kernel.config.support.ControllerStateBean;
import org.jboss.test.kernel.config.support.CustomValueBean;

/**
 * 
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
public class ValueConverterValueOfTestCase extends AbstractKernelConfigTest
{
   public ValueConverterValueOfTestCase(String name)
   {
      super(name);
   }

   public ValueConverterValueOfTestCase(String name, boolean xmltest)
   {
      super(name, xmltest);
   }

   public static Test suite()
   {
      return suite(ValueConverterValueOfTestCase.class);
   }
   
   public void testValueOfForCustomValue() throws Throwable
   {
      CustomValueBean bean = valueOfForCustomValue();
      assertNotNull(bean);
      assertNotNull(bean.getValue());
      assertEquals("Custom", bean.getValue().getValue());
   }
   
   public void testValueOfForControllerState() throws Throwable
   {
      ControllerStateBean bean = valueOfForControllerState();
      assertNotNull(bean);
      assertNotNull(bean.getState());
      assertEquals(ControllerState.CONFIGURED, bean.getState());
   }
   
   protected CustomValueBean valueOfForCustomValue() throws Throwable
   {
      Kernel kernel = bootstrap();
      KernelConfigurator configurator = kernel.getConfigurator();
      
      AbstractBeanMetaData bmd = new AbstractBeanMetaData(CustomValueBean.class.getName());
      HashSet<PropertyMetaData> properties = new HashSet<PropertyMetaData>();
      bmd.setProperties(properties);

      AbstractPropertyMetaData pmd = new AbstractPropertyMetaData("value", new StringValueMetaData("Custom"));
      properties.add(pmd);
      
      return (CustomValueBean) instantiateAndConfigure(configurator, bmd);
   }
   
   protected ControllerStateBean valueOfForControllerState() throws Throwable
   {
      Kernel kernel = bootstrap();
      KernelConfigurator configurator = kernel.getConfigurator();
      
      AbstractBeanMetaData bmd = new AbstractBeanMetaData(ControllerStateBean.class.getName());
      HashSet<PropertyMetaData> properties = new HashSet<PropertyMetaData>();
      bmd.setProperties(properties);

      AbstractPropertyMetaData pmd = new AbstractPropertyMetaData("state", new StringValueMetaData("Configured"));
      properties.add(pmd);
      
      return (ControllerStateBean) instantiateAndConfigure(configurator, bmd);
   }
}
