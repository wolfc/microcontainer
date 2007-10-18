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
package org.jboss.managed.spi.factory;

import java.io.Serializable;

import org.jboss.beans.info.spi.BeanInfo;
import org.jboss.managed.api.ManagedProperty;
import org.jboss.metatype.api.values.MetaValue;

/**
 * A plugin for obtaining the class to scan for management object
 * related annotations.
 *
 * @param <T> actual attachment type
 * @author Scott.Stark@jboss.org
 * @author Ales.Justin@jboss.org
 * @version $Revision$
 */
public interface InstanceClassFactory<T extends Serializable>
{
   /**
    * Return the Class that represents the root ManagedObject to scan
    * for management object related annotations.
    * 
    * @param attachment - the instance a ManagedObject is to be created for.
    * @return the Class that represents the root ManagedObject.
    * @throws ClassNotFoundException if MO class not found
    */
   Class<? extends Serializable> getManagedObjectClass(T attachment) throws ClassNotFoundException;

   /**
    * Get the value from object.
    *
    * @param beanInfo managed object's bean info
    * @param property managed property
    * @param attachment attachment
    * @return meta value
    */
   MetaValue getValue(BeanInfo beanInfo, ManagedProperty property, T attachment);

   /**
    * Set the value to object.
    *
    * @param beanInfo managed object's bean info
    * @param property managed property
    * @param attachment attachment
    * @param value meta value
    */
   void setValue(BeanInfo beanInfo, ManagedProperty property, T attachment, MetaValue value);

   /**
    * Get the runtime component name.
    *
    * @param beanInfo managed object's bean info
    * @param property managed property
    * @param attachment attachment
    * @param value original value
    * @return meta value
    */
   Object getComponentName(BeanInfo beanInfo, ManagedProperty property, T attachment, MetaValue value);
}
