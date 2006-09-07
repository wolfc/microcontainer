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
package org.jboss.deployers.plugins.structure.vfs;

import org.jboss.deployers.spi.structure.DeploymentContext;
import org.jboss.deployers.spi.structure.vfs.StructureDeployer;
import org.jboss.logging.Logger;

/**
 * StructureDeployerWrapper.
 * 
 * To avoid any problems with error handling by the deployers.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class StructureDeployerWrapper implements StructureDeployer
{
   /** The log */
   private Logger log;
   
   /** The structure deployer */
   private StructureDeployer deployer;   

   /**
    * Create a new StructureDeployerWrapper.
    * 
    * @param deployer the deployer
    */
   public StructureDeployerWrapper(StructureDeployer deployer)
   {
      if (deployer == null)
         throw new IllegalArgumentException("Null deployer");
      this.deployer = deployer;
   }
   
   public boolean determineStructure(DeploymentContext context)
   {
      if (context == null)
         throw new IllegalArgumentException("Null context");
      
      try
      {
         boolean result = deployer.determineStructure(context);
         if (log.isTraceEnabled())
         {
            if (result == false)
               log.trace("Not recognised: " + context.getName());
            else
               context.dump();
         }
         return result;
      }
      catch (Throwable t)
      {
         log.warn("Error during determineStructure: " + context.getName(), t);
         return false;
      }
   }
   
   public int getRelativeOrder()
   {
      return deployer.getRelativeOrder();
   }

   @Override
   public boolean equals(Object obj)
   {
      return deployer.equals(obj);
   }
   
   @Override
   public int hashCode()
   {
      return deployer.hashCode();
   }
}
