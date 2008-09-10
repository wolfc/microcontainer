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
package org.jboss.kernel.plugins.dependency;

import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.LifecycleMetaData;
import org.jboss.kernel.spi.dependency.CreateKernelControllerContextAware;
import org.jboss.kernel.spi.dependency.KernelControllerContextAware;
import org.jboss.dependency.spi.ControllerState;

/**
 * CreateDestroyLifecycleAction.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @author <a href="ales.justin@jboss.com">Ales Justin</a>
 * @version $Revision$
 */
public class CreateDestroyLifecycleAction extends LifecycleAction
{
   protected LifecycleMetaData getInstallLifecycle(BeanMetaData beanMetaData)
   {
      return beanMetaData.getCreate();
   }

   public String getDefaultInstallMethod()
   {
      return "create";
   }

   protected Class<? extends KernelControllerContextAware> getActionAwareInterface()
   {
      return CreateKernelControllerContextAware.class;
   }

   protected LifecycleMetaData getUninstallLifecycle(BeanMetaData beanMetaData)
   {
      return beanMetaData.getDestroy();
   }

   public String getDefaultUninstallMethod()
   {
      return "destroy";
   }

   protected ControllerState getState()
   {
      return ControllerState.CREATE;
   }
}