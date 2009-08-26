/*
* JBoss, Home of Professional Open Source
* Copyright 2005, JBoss Inc., and individual contributors as indicated
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

import junit.framework.Test;
import org.jboss.dependency.plugins.AbstractDependencyItem;
import org.jboss.dependency.spi.ControllerContext;
import org.jboss.dependency.spi.ControllerState;
import org.jboss.dependency.spi.DependencyItem;
import org.jboss.test.dependency.controller.support.TestDelegate;

/**
 * A BasicDependencyTestCase.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 60558 $
 */
public class SelfDependencyTestCase extends AbstractDependencyTest
{
   public static Test suite()
   {
      return suite(SelfDependencyTestCase.class);
   }
   
   public SelfDependencyTestCase(String name)
   {
      super(name);
   }
   
   public void testDependencyCorrectOrder() throws Throwable
   {
      TestDelegate delegate = new TestDelegate("Self");
      ControllerContext context = assertInstall(delegate);
      DependencyItem dependency = new AbstractDependencyItem("Self", "Self", ControllerState.DESCRIBED, ControllerState.INSTALLED);
      delegate.addDependency(dependency);
      dependency.resolve(controller);

      assertUninstall(context);
      assertNull(context.getError());
   }
}
