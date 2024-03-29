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

import java.util.List;

import org.jboss.beans.metadata.spi.MetaDataVisitorNode;
import org.jboss.dependency.spi.DependencyInfo;
import org.jboss.dependency.spi.DependencyItem;
import org.jboss.dependency.spi.ControllerState;
import org.jboss.dependency.plugins.AbstractDependencyItem;
import org.jboss.kernel.plugins.annotations.ClassAnnotationPlugin;
import org.jboss.kernel.spi.dependency.KernelControllerContext;
import org.jboss.metadata.spi.MetaData;
import org.jboss.reflect.spi.ClassInfo;

/**
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public class SuperDemandAnnotationPlugin extends ClassAnnotationPlugin<SuperDemand>
{
   public SuperDemandAnnotationPlugin()
   {
      super(SuperDemand.class);
   }

   protected List<? extends MetaDataVisitorNode> internalApplyAnnotation(ClassInfo info, MetaData retrieval, SuperDemand annotation, KernelControllerContext context) throws Throwable
   {
      DependencyInfo di = context.getDependencyInfo();
      DependencyItem item = new AbstractDependencyItem(context.getName(), annotation.demand(), new ControllerState(annotation.whenRequired()), new ControllerState(annotation.dependentState()));
      di.addIDependOn(item);
      return null;
   }
}