/*
* JBoss, Home of Professional Open Source
* Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.test.dependency.controller.test;

import java.util.Collections;
import java.util.Map;

import junit.framework.Test;

import org.jboss.dependency.plugins.AbstractControllerContext;
import org.jboss.dependency.plugins.AbstractControllerContextActions;
import org.jboss.dependency.plugins.action.ControllerContextAction;
import org.jboss.dependency.spi.ControllerContext;
import org.jboss.dependency.spi.ControllerContextActions;
import org.jboss.dependency.spi.ControllerMode;
import org.jboss.dependency.spi.ControllerState;
import org.jboss.dependency.spi.DependencyInfo;
import org.jboss.test.dependency.controller.support.RecursiveDependencyItem;

/**
 * RecursiveResolutionTestCase.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class RecursiveResolutionTestCase extends AbstractDependencyTest
{
   public static Test suite()
   {
      return suite(RecursiveResolutionTestCase.class);
   }
   
   public RecursiveResolutionTestCase(String name)
   {
      super(name);
   }
   
   public void testRecursiveResolve() throws Throwable
   {
      Map<ControllerState, ControllerContextAction> actionsMap = Collections.emptyMap();
      ControllerContextActions actions = new AbstractControllerContextActions(actionsMap);

      ControllerContext ctx1 = new AbstractControllerContext("1", actions);
      ctx1.setMode(ControllerMode.MANUAL);
      assertInstall(ctx1, ControllerState.NOT_INSTALLED);
      
      ControllerContext ctx2 = new AbstractControllerContext("2", actions); 
      ctx2.setMode(ControllerMode.MANUAL);
      DependencyInfo ctx2dependencies = ctx2.getDependencyInfo();
      ctx2dependencies.addIDependOn(new RecursiveDependencyItem("2", "1", ControllerState.INSTALLED, ControllerState.INSTALLED));
      assertInstall(ctx2, ControllerState.NOT_INSTALLED);
      
      assertChange(ctx2, ControllerState.INSTALLED);
      assertContext(ctx1, ControllerState.INSTALLED);
   }
}
