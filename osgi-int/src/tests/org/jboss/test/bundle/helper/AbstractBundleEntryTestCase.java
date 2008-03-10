/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, JBoss Inc., and individual contributors as indicated
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
package org.jboss.test.bundle.helper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.jboss.test.OSGiTestCase;
import org.jboss.virtual.VirtualFile;

/**
 * A AbsractBundleEntryTestCase - used for helper classes or Bundle entries
 * 
 * @author <a href="baileyje@gmail.com">John Bailey</a>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractBundleEntryTestCase extends OSGiTestCase
{

   /**
    * Create a new AbsractBundleEntryTestCase.
    * 
    * @param name
    */
   public AbstractBundleEntryTestCase(String name)
   {
      super(name);
   }

   /**
    * Assert a returned entry is the expected virtual file based on the root VirtualFile and path
    * 
    * @param root Root VirtualFile to get path
    * @param virualFilePath a path to a virtual file in the root
    * @param entryUrl a URL to an entry to assert same as expected path
    * @throws IOException
    * @throws URISyntaxException
    */
   protected void assertEntry(VirtualFile root, String virualFilePath, URL entryUrl) throws IOException,
         URISyntaxException
   {
      VirtualFile expectedEntry = root.getChild(virualFilePath);
      assertNotNull("Invalid assertiong.  Expected file does not exisist", expectedEntry); // Sanity check

      assertEquals(expectedEntry.toURL(), entryUrl);
   }

}