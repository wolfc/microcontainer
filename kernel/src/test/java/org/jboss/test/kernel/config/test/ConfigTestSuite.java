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
package org.jboss.test.kernel.config.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Config Test Suite.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision$
 */
public class ConfigTestSuite extends TestSuite
{
   public static void main(String[] args)
   {
      TestRunner.run(suite());
   }

   public static Test suite()
   {
      TestSuite suite = new TestSuite("Config Tests");

      suite.addTest(InstantiateTestCase.suite());
      suite.addTest(InstantiateXMLTestCase.suite());
      suite.addTest(InstantiateAnnotationTestCase.suite());
      suite.addTest(GenericFactoryInstantiateTestCase.suite());
      suite.addTest(GenericFactoryInstantiateXMLTestCase.suite());
      suite.addTest(FactoryTestCase.suite());
      suite.addTest(FactoryXMLTestCase.suite());
      suite.addTest(FactoryAnnotationTestCase.suite());
      suite.addTest(ConfigureAttributeFromObjectTestCase.suite());
      suite.addTest(ConfigureAttributeFromStringTestCase.suite());
      suite.addTest(ConfigureFromObjectTestCase.suite());
      suite.addTest(ConfigureFromStringTestCase.suite());
      suite.addTest(ConfigureFromStringXMLTestCase.suite());
      suite.addTest(ConfigureFromStringAnnotationTestCase.suite());
      suite.addTest(CollectionTestCase.suite());
      suite.addTest(CollectionXMLTestCase.suite());
      suite.addTest(CollectionAnnotationTestCase.suite());
      suite.addTest(SetTestCase.suite());
      suite.addTest(SetXMLTestCase.suite());
      suite.addTest(SetAnnotationTestCase.suite());
      suite.addTest(ListTestCase.suite());
      suite.addTest(ListXMLTestCase.suite());
      suite.addTest(ListAnnotationTestCase.suite());
      suite.addTest(ArrayTestCase.suite());
      suite.addTest(ArrayXMLTestCase.suite());
      suite.addTest(ArrayAnnotationTestCase.suite());
      suite.addTest(MapTestCase.suite());
      suite.addTest(MapXMLTestCase.suite());
      suite.addTest(MapAnnotationTestCase.suite());
      suite.addTest(ProgressionTestCase.suite());
      suite.addTest(ProgressionXMLTestCase.suite());
      suite.addTest(ProgressionAnnotationTestCase.suite());
      suite.addTest(BeanMetaDataBuilderTestCase.suite());
      suite.addTest(PropertyReplaceTestCase.suite());
      suite.addTest(PropertyReplaceXMLTestCase.suite());
      suite.addTest(PropertyReplaceAnnotationTestCase.suite());
      suite.addTest(ElementTestCase.suite());
      suite.addTest(ElementXMLTestCase.suite());
      suite.addTest(ElementAnnotationTestCase.suite());
      suite.addTest(ValueFactoryTestCase.suite());
      suite.addTest(ValueFactoryXMLTestCase.suite());
      suite.addTest(ValueFactoryAnnotationTestCase.suite());
      suite.addTest(ValueConverterValueOfTestCase.suite());
      suite.addTest(ValueConverterValueOfXMLTestCase.suite());
      suite.addTest(ValueTrimTestCase.suite());
      suite.addTest(ValueTrimXMLTestCase.suite());
      suite.addTest(ValueTrimAnnotationTestCase.suite());
      suite.addTest(PreInstantiatedFieldsTestCase.suite());
      suite.addTest(PreInstantiatedFieldsXMLTestCase.suite());

      return suite;
   }
}
