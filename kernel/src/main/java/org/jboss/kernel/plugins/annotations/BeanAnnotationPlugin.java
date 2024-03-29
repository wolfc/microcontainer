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
package org.jboss.kernel.plugins.annotations;

import java.util.List;

import org.jboss.beans.metadata.api.annotations.Bean;
import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.MetaDataVisitorNode;
import org.jboss.beans.metadata.plugins.AbstractBeanMetaData;
import org.jboss.reflect.spi.ClassInfo;

/**
 * Bean annotation plugin.
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public class BeanAnnotationPlugin extends ClassAnnotationPlugin<Bean>
{
   public static final BeanAnnotationPlugin INSTANCE = new BeanAnnotationPlugin();

   protected BeanAnnotationPlugin()
   {
      super(Bean.class);
   }

   protected List<? extends MetaDataVisitorNode> internalApplyAnnotation(ClassInfo info, Bean annotation, BeanMetaData beanMetaData)
   {
      AbstractBeanMetaData abmd = checkIfNotAbstractBeanMetaDataSpecific(beanMetaData);

      // TODO - add missing attributes
      
      if (abmd.getAutowireType() == null)
         abmd.setAutowireType(annotation.autowireType());
      if (abmd.getMode() == null)
         abmd.setMode(annotation.mode());
      if (abmd.getErrorHandlingMode() == null)
         abmd.setErrorHandlingMode(annotation.errorHandlingMode());
      if (abmd.getAccessMode() == null)
         abmd.setAccessMode(annotation.accessMode());

      // we don't put bmd back to be inspected
      // since the changes we apply *here* don't really
      // trigger any change in metadata - all enums
      return null;
   }
}