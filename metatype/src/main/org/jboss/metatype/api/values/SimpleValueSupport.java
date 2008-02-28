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
package org.jboss.metatype.api.values;

import java.io.Serializable;

import org.jboss.metatype.api.types.SimpleMetaType;

/**
 * SimpleValue.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class SimpleValueSupport extends AbstractMetaValue implements SimpleValue
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 8473043036261557127L;
   
   /** The simple meta type */
   private SimpleMetaType metaType;
   
   /** The value */
   private Serializable value;

   /**
    * Wrap a simple object in simple value
    * 
    * @param object the simple object
    * @return the simple value
    */
   public static SimpleValue wrap(Serializable object)
   {
      if (object == null)
         return null;
      String className = object.getClass().getName();
      SimpleMetaType metaType = SimpleMetaType.resolve(className);
      return new SimpleValueSupport(metaType, object);
   }
   
   /**
    * Create a new SimpleValueSupport.
    * 
    * @param metaType the simple meta type
    * @param value the value
    * @throws IllegalArgumentException for a null simpleMetaType
    */
   public SimpleValueSupport(SimpleMetaType metaType, Serializable value)
   {
      if (metaType == null)
         throw new IllegalArgumentException("Null simple meta type");
      this.metaType = metaType;
      setValue(value);
   }

   public SimpleMetaType getMetaType()
   {
      return metaType;
   }

   /**
    * Get the value.
    * 
    * @return the value.
    */
   public Serializable getValue()
   {
      return value;
   }

   /**
    * Set the value.
    * 
    * @param value the value.
    */
   public void setValue(Serializable value)
   {
      this.value = value;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (obj == this)
         return true;
      
      if (obj == null || obj instanceof SimpleValue == false)
         return false;

      SimpleValue other = (SimpleValue) obj;
      if (metaType.equals(other.getMetaType()) == false)
         return false;

      Object otherValue = other.getValue();
      if (value == null && otherValue == null)
         return true;
      if (value == null && otherValue != null)
         return false;
      return value.equals(otherValue);
   }
   
   @Override
   public int hashCode()
   {
      if (value == null)
         return 0;
      return value.hashCode();
   }

   @Override
   public String toString()
   {
      return metaType + ":" + value;
   }
}
