/*
* JBoss, Home of Professional Open Source
* Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.test.dependency.controller.support;

import org.jboss.dependency.plugins.AbstractDependencyItem;
import org.jboss.dependency.spi.Controller;
import org.jboss.dependency.spi.ControllerContext;
import org.jboss.dependency.spi.ControllerState;
import org.jboss.dependency.spi.ControllerStateModel;

/**
 * RecursiveDependencyItem.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class RecursiveDependencyItem extends AbstractDependencyItem
{
   public RecursiveDependencyItem(Object name, Object iDependOn, ControllerState whenRequired, ControllerState dependentState)
   {
      super(name, iDependOn, whenRequired, dependentState);
   }

   @Override
   public boolean resolve(Controller controller)
   {
      ControllerStateModel stateModel = controller.getStates();
      ControllerContext other = controller.getContext(getIDependOn(), null);
      ControllerState otherState = getDependentState();
      if (stateModel.isBeforeState(other.getState(), otherState))
      {
         try
         {
            controller.change(other, otherState);
         }
         catch (Throwable t)
         {
            log.warn("Error", t);
            return false;
         }
         if (stateModel.isBeforeState(other.getState(), otherState))
            return false;
      }
      addDependsOnMe(controller, other);
      return true;
   }
}