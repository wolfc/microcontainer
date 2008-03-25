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
package org.jboss.test.kernel.annotations.support;

import java.util.Set;
import java.io.PrintStream;

import org.jboss.beans.metadata.api.annotations.Aliases;
import org.jboss.beans.metadata.api.annotations.Demands;
import org.jboss.beans.metadata.api.annotations.Demand;
import org.jboss.beans.metadata.api.annotations.Supply;
import org.jboss.beans.metadata.api.annotations.Depends;
import org.jboss.beans.metadata.api.annotations.ExternalInstalls;
import org.jboss.beans.metadata.api.annotations.ExternalInstall;
import org.jboss.beans.metadata.api.annotations.ExternalUninstalls;
import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.beans.metadata.api.annotations.Create;
import org.jboss.beans.metadata.api.annotations.Start;
import org.jboss.beans.metadata.api.annotations.Stop;
import org.jboss.beans.metadata.api.annotations.Destroy;
import org.jboss.beans.metadata.api.annotations.ValueFactory;
import org.jboss.beans.metadata.api.annotations.Install;
import org.jboss.beans.metadata.api.annotations.Uninstall;
import org.jboss.beans.metadata.api.annotations.InstallMethod;
import org.jboss.beans.metadata.api.annotations.UninstallMethod;

/**
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public class OtherVerifier implements AfterInstallVerifier<Object>
{
   public void verify(Object target)
   {
   }

   public Class<Object> getTargetClass()
   {
      return Object.class;
   }

   public PrintStream getSystemErr()
   {
      return System.err;
   }

   public long getCurrentTime()
   {
      return System.currentTimeMillis();
   }

   public void touch()
   {
      System.out.println(getCurrentTime());
   }
}