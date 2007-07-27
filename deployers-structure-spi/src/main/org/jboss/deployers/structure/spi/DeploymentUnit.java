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
package org.jboss.deployers.structure.spi;

import java.util.List;
import java.util.Set;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.attachments.MutableAttachments;

/**
 * DeploymentUnit.<p>
 * 
 * A deployment unit represents a single unit
 * that deployers work with.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @author Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public interface DeploymentUnit extends MutableAttachments
{
   /**
    * Get the deployment units name
    * 
    *  @return the name;
    */
   String getName();

   /**
    * Get the simple vfs name of the deployment unit. This is the simple
    * name of the virtual file .
    * 
    * vfs path ------------------- simple name
    * deploy/some.ear              "some.ear"
    * deploy/some.ear/x.ejb        "x.ejb"
    * deploy/some.ear/y.sar        "y.sar"
    * deploy/some.ear/y.sar/z.rar  "z.rar"
    * deploy/complexwithappxml.ear/module-mbean1.sar/submbean.sar submbean.sar
    * @return the deployment unit simple path
    */
   String getSimpleName();

   /**
    * Get the path of this deployment relative to the top of the deployment
    * 
    * vfs path ------------------- relative path
    * deploy/some.ear              ""
    * deploy/some.ear/x.ejb        "/x.ejb"
    * deploy/some.ear/y.sar        "/y.sar"
    * deploy/some.ear/y.sar/z.rar  "/y.sar/z.rar"
    * 
    * @return the top-level deployment relative path
    */
   String getRelativePath();
   
   /**
    * Get the deployment types associated with this deployment.
    * 
    * @return set of deployment type names deployers have identified
    * in this deployment.
    */
   Set<String> getTypes();

   /**
    * Gets the classloader for this deployment unit
    * 
    * @return the classloader
    */
   ClassLoader getClassLoader();

   /**
    * Create the classloader
    * 
    * @param factory the classloader factory
    * @return false if the classloader already exists
    * @throws IllegalArgumentException for a null factory
    * @throws DeploymentException for any error
    */
   boolean createClassLoader(ClassLoaderFactory factory) throws DeploymentException;
   
   /**
    * Get all the metadata for the expected type
    * 
    * @param <T> the type to get
    * @param type the type
    * @return a set of metadata matching the type
    * @throws IllegalArgumentException if the type is null 
    */
   <T> Set<? extends T> getAllMetaData(Class<T> type);

   /**
    * Get the transient managed objects
    * 
    * @return the managed objects
    */
   MutableAttachments getTransientManagedObjects();
   
   /**
    * Get the top leve deployment unit
    * 
    * @return the top level deployment unit
    */
   DeploymentUnit getTopLevel();
   
   /**
    * Get the parent deployment unit
    * 
    * @return the parent or null if there is no parent
    */
   DeploymentUnit getParent();
   
   /**
    * Get the children
    * 
    * @return the children
    */
   List<DeploymentUnit> getChildren();
   
   /**
    * Whether this unit is a component
    * 
    * @return true for a component
    */
   boolean isComponent();
   
   /**
    * Add a component
    * 
    * @param name the name
    * @return the new deployment unit
    * @throws IllegalArgumentException for a null name
    */
   DeploymentUnit addComponent(String name);
   
   /**
    * Remove a component
    * 
    * @param name the name
    * @return true when removed
    * @throws IllegalArgumentException for a null name
    */
   boolean removeComponent(String name);
   
   /**
    * Get a resource loader
    * 
    * @return the resource loader
    */
   DeploymentResourceLoader getResourceLoader();
   
   /**
    * Get a resource classloader
    * 
    * @return the resource classloader loader
    */
   ClassLoader getResourceClassLoader();
}
