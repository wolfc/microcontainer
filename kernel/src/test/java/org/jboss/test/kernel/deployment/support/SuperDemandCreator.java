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
package org.jboss.test.kernel.deployment.support;

import org.jboss.beans.metadata.api.annotations.Constructor;
import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.dependency.plugins.AbstractDependencyItem;
import org.jboss.dependency.spi.Controller;
import org.jboss.dependency.spi.ControllerState;
import org.jboss.dependency.spi.DependencyInfo;
import org.jboss.dependency.spi.DependencyItem;
import org.jboss.dependency.spi.ControllerContext;
import org.jboss.kernel.plugins.bootstrap.basic.KernelConstants;

/**
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public class SuperDemandCreator
{
   private Controller controller;

   @Constructor
   public SuperDemandCreator(@Inject(bean = KernelConstants.KERNEL_CONTROLLER_NAME) Controller controller)
   {
      this.controller = controller;
   }

   public void createDependency(String name, String demand, String whenRequired, String dependentState)
   {
      ControllerContext context = controller.getContext(name, null);
      if (context == null)
         throw new IllegalArgumentException("Cannot find context: " + name);
      
      DependencyInfo di = context.getDependencyInfo();
      DependencyItem item = new AbstractDependencyItem(context.getName(), demand, new ControllerState(whenRequired), new ControllerState(dependentState));
      di.addIDependOn(item);
   }
}