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
package org.jboss.deployers.spi.deployer;

import java.io.InputStream;
import java.net.URL;

/**
 * DeploymentUnit.<p>
 * 
 * A deployment unit represents a single unit
 * that deployers work with.
 * 
 * TODO managed object stuff
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public interface DeploymentUnit
{
   /**
    * Get the deployment units name
    * 
    *  @return the name;
    */
   String getName();
   
   /**
    * Gets some metadata for this deployment unit
    * 
    * @param name the resource name
    * @return the url of the metadata or null if not found
    */
   URL getMetaData(String name);
   
   /**
    * Gets some metadata as a stream
    * 
    * @param name the resource name
    * @return the stream or null if not found
    */
   InputStream getMetaDataAsStream(String name);
   
   /**
    * Gets the classloader for this deployment unit
    * 
    * @return the classloader
    */
   ClassLoader getClassLoader();
}
