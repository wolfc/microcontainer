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
package org.jboss.beans.metadata.plugins.policy;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.jboss.beans.metadata.spi.AnnotationMetaData;
import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.policy.BindingMetaData;
import org.jboss.beans.metadata.spi.policy.PolicyMetaData;
import org.jboss.beans.metadata.spi.policy.ScopeMetaData;
import org.jboss.util.JBossObject;
import org.jboss.util.JBossStringBuilder;

/**
 * Meta data for policy.
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public class AbstractPolicyMetaData extends JBossObject implements PolicyMetaData, Serializable
{
   private static final long serialVersionUID = 1;

   protected String name;
   protected String ext;
   protected ScopeMetaData scope;
   protected Set<AnnotationMetaData> annotations;
   protected Set<BindingMetaData> bindings;

   public String getName()
   {
      return name;
   }

   public String getExtends()
   {
      return ext;
   }

   public ScopeMetaData getScope()
   {
      return scope;
   }

   public Set<AnnotationMetaData> getAnnotations()
   {
      return annotations;
   }

   public Set<BindingMetaData> getBindings()
   {
      return bindings;
   }

   public List<BeanMetaData> getBeans()
   {
      return null; // todo
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public void setExtends(String ext)
   {
      this.ext = ext;
   }

   public void setScope(ScopeMetaData scope)
   {
      this.scope = scope;
   }

   public void setAnnotations(Set<AnnotationMetaData> annotations)
   {
      this.annotations = annotations;
   }

   public void setBindings(Set<BindingMetaData> bindings)
   {
      this.bindings = bindings;
   }

   public void toString(JBossStringBuilder buffer)
   {
      buffer.append("name=").append(name);
      buffer.append(" extends=").append(ext);
      buffer.append(" scope=").append(scope);
      super.toString(buffer);
      buffer.append(" bindings=").append(bindings);
   }

   public void toShortString(JBossStringBuilder buffer)
   {
      buffer.append(name);
      buffer.append('/');
      buffer.append(ext);
      buffer.append('/');
      buffer.append(scope);
      buffer.append('/');
      super.toShortString(buffer);
      buffer.append('/');
      buffer.append(bindings);
   }

}
