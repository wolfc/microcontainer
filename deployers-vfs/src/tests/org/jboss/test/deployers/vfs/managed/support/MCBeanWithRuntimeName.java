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
package org.jboss.test.deployers.vfs.managed.support;

import org.jboss.managed.api.annotation.ManagementObject;
import org.jboss.managed.api.annotation.ManagementObjectID;
import org.jboss.managed.api.annotation.ManagementRuntimeRef;

/**
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
@ManagementObject
public class MCBeanWithRuntimeName
{
   private String id;
   private String componentName;

   @ManagementObjectID(type = "MCBean")
   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   @ManagementRuntimeRef
   public String getComponentName()
   {
      return componentName;
   }

   public void setComponentName(String componentName)
   {
      this.componentName = componentName;
   }
}