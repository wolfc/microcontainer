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
package org.jboss.aop.microcontainer.beans.metadata;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
@XmlType(propOrder= {})
public class MixinData implements Serializable
{
   private static final long serialVersionUID = 1L;
   
   String mixin;
   boolean isTransient = true;
   String construction;
   String interfaces;

   public boolean getTransient()
   {
      return isTransient;
   }

   @XmlAttribute
   public void setTransient(boolean isTransient)
   {
      this.isTransient = isTransient;
   }

   public String getMixin()
   {
      return mixin;
   }

   @XmlElement(name="class")
   public void setMixin(String mixin)
   {
      this.mixin = mixin;
   }

   public String getConstruction()
   {
      return construction;
   }

   public void setConstruction(String construction)
   {
      this.construction = construction;
   }

   public String getInterfaces()
   {
      return interfaces;
   }

   public void setInterfaces(String interfaces)
   {
      this.interfaces = interfaces;
   }   
}
