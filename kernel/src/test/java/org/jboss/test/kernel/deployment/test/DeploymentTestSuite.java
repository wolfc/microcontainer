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
package org.jboss.test.kernel.deployment.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Deployment Test Suite.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision$
 */
public class DeploymentTestSuite extends TestSuite
{
   public static void main(String[] args)
   {
      TestRunner.run(suite());
   }

   public static Test suite()
   {
      TestSuite suite = new TestSuite("Deployment Tests");

      suite.addTest(BeanMetaDataTestCase.suite());
      suite.addTest(BeanMetaDataWithClassloaderTestCase.suite());
      suite.addTest(DeploymentClassLoaderTestCase.suite());
      suite.addTest(GenericBeanFactoryLifecycleTestCase.suite());
      suite.addTest(WildcardClassLoaderTestCase.suite());
      suite.addTest(BeanMetaDataFactoryTestCase.suite());
      suite.addTest(AnnotatedLifecycleTestCase.suite());
      suite.addTest(IgnoredAnnotatedLifecycleTestCase.suite());
      suite.addTest(IgnoredLifecycleTestCase.suite());
      suite.addTest(IgnoredFactoryLifecycleTestCase.suite());
      suite.addTest(MixedLifecycleTestCase.suite());
      suite.addTest(FineGrainedActionTestCase.suite());
      suite.addTest(ScopingTestCase.suite());
      suite.addTest(AnnotatedDeploymentScopingTestCase.suite());
      suite.addTest(IllegalScopingTestCase.suite());
      suite.addTest(AnnotatedClassesScopingTestCase.suite());
      suite.addTest(ContextualScopingTestCase.suite());
      suite.addTest(InnerBeanScopingTestCase.suite());
      suite.addTest(ScopingShutdownTestCase.suite());
      suite.addTest(FromContextUnsupportedTestCase.suite());
      suite.addTest(InCallbackInstantiatedTestCase.suite());
      suite.addTest(AnonymousBeansTestCase.suite());
      suite.addTest(AnonymousBeansXMLTestCase.suite());
      suite.addTest(MutableMetaDataTestCase.suite());
      suite.addTest(AnnotationRedeployTestCase.suite());
      suite.addTest(MockServiceBindingTestCase.suite());
      suite.addTest(MockEjb3DependsTestCase.suite());
      suite.addTest(AnnotationUsageTestCase.suite());
      suite.addTest(PropertyUsageTestCase.suite());
      suite.addTest(AliasInjectTestCase.suite());
      suite.addTest(GetInstanceTestCase.suite());
      suite.addTest(NullifyTestCase.suite());
      suite.addTest(TransitiveBeansTestCase.suite());
      suite.addTest(BeanFactoryContextualInjectionTestCase.suite());
      // bean container tests
      suite.addTest(BeanContainerUsageTestCase.suite());
      suite.addTest(BeanContainerUsageMDTestCase.suite());
      suite.addTest(BeanContainerScopingTestCase.suite());
      suite.addTest(BeanContainerInjectionTestCase.suite());
      suite.addTest(BeanContainerStaticTestCase.suite());
      suite.addTest(BeanContainerSetDemandTestCase.suite());
      // scoping alias tests
      suite.addTest(ScopingAliasTestCase.suite());
      suite.addTest(ScopingAliasAPITestCase.suite());
      suite.addTest(ScopingOverrideTestCase.suite());
      suite.addTest(ScopingDependencyTestCase.suite());
      // super-demand
      suite.addTest(SuperDemandAnnotationTestCase.suite());
      suite.addTest(SuperDemandInstallTestCase.suite());

      return suite;
   }
}
