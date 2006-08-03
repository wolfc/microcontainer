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
package org.jboss.test.metadata.loader.memory.test;

import org.jboss.metadata.plugins.loader.memory.MemoryMetaDataLoader;
import org.jboss.test.metadata.loader.MutableMetaDataTest;
import org.jboss.test.metadata.shared.support.MetaDataAndMutableMetaData;
import org.jboss.test.metadata.shared.support.MutableMetaDataLoaderToMetaDataBridge;

/**
 * MemoryLoaderMutableMetaDataUnitTestCase.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision$
 */
public class MemoryLoaderMutableMetaDataUnitTestCase extends MutableMetaDataTest
{
   public MemoryLoaderMutableMetaDataUnitTestCase(String name)
   {
      super(name);
   }

   protected MetaDataAndMutableMetaData setupEmpty()
   {
      MemoryMetaDataLoader loader = new MemoryMetaDataLoader();
      return new MutableMetaDataLoaderToMetaDataBridge(loader);
   }
}
