/*
* JBoss, Home of Professional Open Source
* Copyright 2005, JBoss Inc., and individual contributors as indicated
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
package org.jboss.kernel.spi.config;

import org.jboss.beans.info.spi.BeanAccessMode;
import org.jboss.beans.info.spi.BeanInfo;
import org.jboss.kernel.spi.KernelObject;
import org.jboss.kernel.spi.validation.KernelBeanValidator;
import org.jboss.kernel.spi.bootstrap.KernelInitializer;
import org.jboss.kernel.spi.dependency.DependencyBuilder;
import org.jboss.kernel.spi.dependency.KernelController;
import org.jboss.kernel.spi.event.KernelEventManager;
import org.jboss.kernel.spi.metadata.KernelMetaDataRepository;
import org.jboss.kernel.spi.registry.KernelBus;
import org.jboss.reflect.spi.ClassInfo;
import org.jboss.reflect.spi.TypeInfo;

/**
 * Kernel Configuration.<p>
 * 
 * Provides kernel configuration options. 
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @author <a href="mailto:les.hazlewood@jboss.org">Les A. Hazlewood</a>
 * @version $Revision$
 */
@SuppressWarnings("deprecation")
public interface KernelConfig extends KernelObject
{
   /**
    * Get the bean info
    * 
    * @param className the class name
    * @param cl the classloader
    * @return the bean info
    * @throws Throwable for any error
    */
   BeanInfo getBeanInfo(String className, ClassLoader cl) throws Throwable;

   /**
    * Get the bean info
    * 
    * @param clazz the class
    * @return the bean info
    * @throws Throwable for any error
    */
   BeanInfo getBeanInfo(Class<?> clazz) throws Throwable;

   /**
    * Get the bean info
    * 
    * @param type the type info
    * @return the bean info
    * @throws Throwable for any error
    */
   BeanInfo getBeanInfo(TypeInfo type) throws Throwable;
   
   /**
    * Get the bean info
    *
    * @param className the class name
    * @param cl the classloader
    * @param mode the access mode
    * @return the bean info
    * @throws Throwable for any error
    */
   BeanInfo getBeanInfo(String className, ClassLoader cl, BeanAccessMode mode) throws Throwable;

   /**
    * Get the bean info
    *
    * @param clazz the class
    * @param mode the access mode
    * @return the bean info
    * @throws Throwable for any error
    */
   BeanInfo getBeanInfo(Class<?> clazz, BeanAccessMode mode) throws Throwable;

   /**
    * Get the bean info
    *
    * @param type the type info
    * @param mode the access mode
    * @return the bean info
    * @throws Throwable for any error
    */
   BeanInfo getBeanInfo(TypeInfo type, BeanAccessMode mode) throws Throwable;

   /**
    * Get the type info for a class
    * 
    * @param className the class name
    * @param cl the classloader
    * @return the type info
    * @throws Throwable for any error
    */
   TypeInfo getTypeInfo(String className, ClassLoader cl) throws Throwable;
   
   /**
    * Get the type info for a class
    * 
    * @param clazz the class
    * @return the type info
    * @throws Throwable for any error
    */
   TypeInfo getTypeInfo(Class<?> clazz) throws Throwable;

   /**
    * Get the class info for a class
    * 
    * @param className the class name
    * @param cl the classloader
    * @return the class info
    * @throws Throwable for any error
    */
   ClassInfo getClassInfo(String className, ClassLoader cl) throws Throwable;
   
   /**
    * Get the class info for a class
    * 
    * @param clazz the class
    * @return the class info
    * @throws Throwable for any error
    */
   ClassInfo getClassInfo(Class<?> clazz) throws Throwable;

   /**
    * Create a kernel bus
    * 
    * @return the kernel bus
    * @throws Throwable for any error
    */
   KernelBus createKernelBus() throws Throwable;

   /**
    * Create a kernel configurator
    * 
    * @return the kernel configurator
    * @throws Throwable for any error
    */
   KernelConfigurator createKernelConfigurator() throws Throwable;

   /**
    * Create a kernel controller
    * 
    * @return the kernel controller
    * @throws Throwable for any error
    */
   KernelController createKernelController() throws Throwable;

   /**
    * Create a kernel event manager
    *
    * @return the kernel event manager
    * @throws Throwable for any error
    */
   KernelEventManager createKernelEventManager() throws Throwable;

   /**
    * Create a kernel initializer
    * 
    * @return the kernel initializer
    * @throws Throwable for any error
    */
   KernelInitializer createKernelInitializer() throws Throwable;

   /**
    * Create a kernel registry
    * 
    * @return the kernel registry
    * @throws Throwable for any error
    */
   org.jboss.kernel.spi.registry.KernelRegistry createKernelRegistry() throws Throwable;

   /**
    * Create a meta data repository
    * 
    * @return the meta data repository
    * @throws Throwable for any error
    */
   KernelMetaDataRepository createKernelMetaDataRepository() throws Throwable;

   /**
    * Create kernel bean validator.
    *
    * @return the kernel bean validator
    * @throws Throwable for any error
    */
   KernelBeanValidator createKernelBeanValidator() throws Throwable;

   /**
    * Get the dependency builder
    * 
    * @return the dependency builder
    * @throws Throwable for any error
    */
   DependencyBuilder getDependencyBuilder() throws Throwable;
}
