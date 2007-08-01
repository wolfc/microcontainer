/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.managed.plugins;

import java.io.Serializable;
import java.util.Map;

import org.jboss.managed.api.ComponentType;
import org.jboss.managed.api.ManagedComponent;
import org.jboss.managed.api.ManagedDeployment;
import org.jboss.managed.api.ManagedProperty;

/**
 * 
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class ManagedComponentImpl extends BaseManagedObject
   implements ManagedComponent, Serializable
{
   private static final long serialVersionUID = 1;
   
   private ManagedDeployment owner;
   private ComponentType type;
   
   ManagedComponentImpl(String name, ComponentType type, Map<String, ManagedProperty> properties, ManagedDeployment owner)
   {
      super(name, properties);
      this.type = type;
      this.owner = owner;
   }
   
   public ManagedDeployment getDeployment()
   {
      return owner;
   }
   
   public ComponentType getType()
   {
      return type;
   }
   
   public String toString()
   {
      StringBuilder tmp = new StringBuilder(super.toString());
      tmp.append('{');
      super.toString(tmp);
      tmp.append(", type=");
      tmp.append(type);
      tmp.append(", owner=ManagedDeployment@");
      tmp.append(System.identityHashCode(owner));
      tmp.append('}');
      return tmp.toString();
   }
}