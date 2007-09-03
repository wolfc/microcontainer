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
package org.jboss.test.reliance.drools.test;

import junit.framework.Test;
import org.jboss.dependency.spi.ControllerState;
import org.jboss.kernel.spi.dependency.KernelControllerContext;
import org.jboss.test.AbstractTestDelegate;
import org.jboss.test.kernel.junit.MicrocontainerTest;
import org.jboss.test.kernel.junit.MicrocontainerTestDelegate;
import org.jboss.test.reliance.drools.support.RolesAdapter;

/**
 * Initial concept test case.
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public class ConceptTestCase extends MicrocontainerTest
{
   public ConceptTestCase(String name)
   {
      super(name);
   }

   public static Test suite()
   {
      return suite(ConceptTestCase.class);
   }

   public void testRulesConcept() throws Throwable
   {
      // only in Configured state, since there is no admin role present
      KernelControllerContext testerContext = getControllerContext("tester", ControllerState.CONFIGURED);
      assertEquals(ControllerState.CONFIGURED, testerContext.getState());

      RolesAdapter rolesAdapter = (RolesAdapter)getBean("roles");
      rolesAdapter.addRole("admin");

      // should be Installed now, since admin role is present
      change(testerContext, ControllerState.INSTALLED);
      assertEquals(ControllerState.INSTALLED, testerContext.getState());

      rolesAdapter.removeRole("admin");
      // should be unwinded to Configured, since the admin role was removed
      assertEquals(ControllerState.CONFIGURED, testerContext.getState());

      KernelControllerContext identityContext = getControllerContext("identity");
      change(identityContext, ControllerState.NOT_INSTALLED);

      // should be uninstalled as well, since it relies on identity
      assertEquals(ControllerState.NOT_INSTALLED, testerContext.getState());
   }

   public static AbstractTestDelegate getDelegate(Class clazz) throws Exception
   {
      return new NonValidatingDelegate(clazz);
   }

   private static class NonValidatingDelegate extends MicrocontainerTestDelegate
   {
      public NonValidatingDelegate(Class clazz) throws Exception
      {
         super(clazz);
      }

      protected void validate() throws Exception
      {
      }
   }
}
