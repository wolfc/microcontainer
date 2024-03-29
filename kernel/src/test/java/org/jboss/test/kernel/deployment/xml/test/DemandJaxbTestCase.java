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
package org.jboss.test.kernel.deployment.xml.test;

import java.util.Set;

import junit.framework.Test;
import org.jboss.beans.metadata.plugins.AbstractBeanMetaData;
import org.jboss.beans.metadata.plugins.AbstractDemandMetaData;
import org.jboss.beans.metadata.spi.DemandMetaData;
import org.jboss.dependency.spi.ControllerState;

/**
 * Demand TestCase.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @author <a href="ales.justin@jboss.com">Ales Justin</a>
 * @version $Revision: 40800 $
 */
public class DemandJaxbTestCase extends AbstractMCTest
{
   protected AbstractDemandMetaData getDemand() throws Exception
   {
      AbstractBeanMetaData bean = unmarshalBean();
      Set<DemandMetaData> demands = bean.getDemands();
      assertNotNull(demands);
      assertEquals(1, demands.size());
      AbstractDemandMetaData demand = (AbstractDemandMetaData) demands.iterator().next();
      assertNotNull(demand);
      return demand;
   }
   
   public void testDemand() throws Exception
   {
      AbstractDemandMetaData demand = getDemand();
      assertEquals("Demand", demand.getDemand());
      assertEquals(ControllerState.DESCRIBED, demand.getWhenRequired());
   }
   
   public void testDemandWithWhenRequired() throws Exception
   {
      AbstractDemandMetaData demand = getDemand();
      assertEquals("Demand", demand.getDemand());
      assertEquals(ControllerState.CONFIGURED, demand.getWhenRequired());
   }

   public void testDemandWithTargetState() throws Exception
   {
      AbstractDemandMetaData demand = getDemand();
      assertEquals("Demand", demand.getDemand());
      assertEquals(ControllerState.CREATE, demand.getTargetState());
   }

   /* TODO
   public void testDemandBadNoValue() throws Exception
   {
      try
      {
         unmarshalBean("DemandBadNoValue.xml");
         fail("Should not be here");
      }
      catch (Exception expected)
      {
         checkJBossXBException(IllegalArgumentException.class, expected);
      }
   }
   */

   public static Test suite()
   {
      return DemandJaxbTestCase.suite(DemandJaxbTestCase.class);
   }

   public DemandJaxbTestCase(String name)
   {
      super(name);
   }
}
