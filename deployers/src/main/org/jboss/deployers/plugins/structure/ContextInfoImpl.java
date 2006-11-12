/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.deployers.plugins.structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.deployers.spi.structure.vfs.ClassPathInfo;
import org.jboss.deployers.spi.structure.vfs.ContextInfo;

/**
 * Represents a deployment context in the vfs.
 * 
 * @author Scott.Stark@jboss.org
 * @version $Revision:$
 */
public class ContextInfoImpl
   implements ContextInfo, Serializable
{
   private static final long serialVersionUID = 1;

   private String vfsPath;
   private String metaDataPath;
   private ContextInfo parent;
   private ArrayList<ClassPathInfo> classpath = new ArrayList<ClassPathInfo>();

   public ContextInfoImpl()
   {
      this(null);
   }
   public ContextInfoImpl(String vfsPath)
   {
      setVfsPath(vfsPath);
   }
   public ContextInfoImpl(String vfsPath, ContextInfo parent)
   {
      setVfsPath(vfsPath);
      setParent(parent);
   }

   public ContextInfo getParent()
   {
      return parent;
   }
   public void setParent(ContextInfo parent)
   {
      this.parent = parent;
   }

   public String getVfsPath()
   {
      return vfsPath;
   }
   public void setVfsPath(String path)
   {
      this.vfsPath = path;
   }

   public String getMetaDataPath()
   {
      return metaDataPath;
   }
   public void setMetaDataPath(String metaDataPath)
   {
      this.metaDataPath = metaDataPath;
   }

   public List<ClassPathInfo> getClassPath()
   {
      return classpath;
   }
   public void setClassPath(List<ClassPathInfo> classpath)
   {
      this.classpath.clear();
      this.classpath.addAll(classpath);
   }
   public void addClassPathInfo(ClassPathInfo info)
   {
      classpath.add(info);
   }

   public String toString()
   {
      StringBuilder tmp = new StringBuilder(super.toString());
      tmp.append('(');
      tmp.append(this.getVfsPath());
      tmp.append(')');
      return tmp.toString();
   }
}
