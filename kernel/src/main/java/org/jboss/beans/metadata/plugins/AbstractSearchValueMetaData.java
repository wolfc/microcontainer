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
package org.jboss.beans.metadata.plugins;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.jboss.beans.metadata.spi.MetaDataVisitor;
import org.jboss.beans.metadata.spi.ValueMetaData;
import org.jboss.dependency.plugins.graph.Search;
import org.jboss.dependency.plugins.graph.SearchDependencyItem;
import org.jboss.dependency.spi.Controller;
import org.jboss.dependency.spi.ControllerContext;
import org.jboss.dependency.spi.ControllerState;
import org.jboss.dependency.spi.DependencyItem;
import org.jboss.dependency.spi.dispatch.AttributeDispatchContext;
import org.jboss.dependency.spi.graph.GraphController;
import org.jboss.dependency.spi.graph.SearchInfo;
import org.jboss.managed.api.annotation.ManagementProperty;
import org.jboss.reflect.spi.TypeInfo;
import org.jboss.util.JBossStringBuilder;
import org.jboss.xb.annotations.JBossXmlAttribute;

/**
 * Search value metadata.
 *
 * @deprecated use <inject search="search-type"/> 
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
@Deprecated
@XmlType(name="searchType")
public class AbstractSearchValueMetaData extends AbstractValueMetaData
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 1L;
   
   private ControllerState state;
   private SearchInfo search;
   private String property;

   private ControllerContext context;

   // Used in JBossXB
   public AbstractSearchValueMetaData()
   {
   }

   public AbstractSearchValueMetaData(Object value, ControllerState state, SearchInfo search, String property)
   {
      super(value);
      if (search == null)
         throw new IllegalArgumentException("Null search type");
      
      this.state = state;
      this.search = search;
      this.property = property;
   }

   public Object getValue(TypeInfo info, ClassLoader cl) throws Throwable
   {
      // we're here, so it must be GraphController instance
      Controller controller = context.getController();
      GraphController gc = (GraphController)controller;

      ControllerState dependentState = state;
      if (dependentState == null)
         dependentState = ControllerState.INSTALLED;
      ControllerContext context = gc.getContext(getUnderlyingValue(), dependentState, search);

      Object result;
      if (property != null && property.length() > 0)
      {
         if (context instanceof AttributeDispatchContext)
         {
            AttributeDispatchContext adc = (AttributeDispatchContext)context;
            result = adc.get(property);
         }
         else
            throw new IllegalArgumentException(
                  "Cannot use property attribute, context is not AttributeDispatchContext: " + context +
                  ", metadata: " + this);
      }
      else
      {
         result = context.getTarget();
      }

      return info != null ? info.convertValue(result) : result;
   }

   public void initialVisit(MetaDataVisitor visitor)
   {
      context = visitor.getControllerContext();

      super.initialVisit(visitor);
   }

   public void describeVisit(MetaDataVisitor visitor)
   {
      Object name = context.getName();
      Object iDependOn = getUnderlyingValue();

      ControllerState whenRequired = visitor.getContextState();
      ControllerState dependentState = state;
      if (dependentState == null)
         dependentState = ControllerState.INSTALLED;

      DependencyItem item = new SearchDependencyItem(name, iDependOn, whenRequired, dependentState, search);
      visitor.addDependency(item);

      super.describeVisit(visitor);
   }

   @XmlAttribute(name="bean")
   @JBossXmlAttribute(type=String.class)
   public void setValue(Object value)
   {
      super.setValue(value);
   }

   @XmlAnyElement
   @ManagementProperty(ignored = true)
   public void setValueObject(Object value)
   {
      if (value == null)
         setValue(null);
      else if (value instanceof ValueMetaData)
         setValue(value);
      else
         setValue(new AbstractValueMetaData(value));
   }

   @XmlAttribute
   public void setState(ControllerState state)
   {
      this.state = state;
   }

   @XmlAttribute(name = "type")
   public void setSearch(Search search)
   {
      this.search = search;
   }

   @XmlAttribute
   public void setProperty(String property)
   {
      this.property = property;
   }

   public void toString(JBossStringBuilder buffer)
   {
      super.toString(buffer);
      buffer.append("search=").append(search);
   }

   public void toShortString(JBossStringBuilder buffer)
   {
      super.toShortString(buffer);
      buffer.append("search=").append(search);
   }
}
