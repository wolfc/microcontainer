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
package org.jboss.beans.metadata.plugins;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.jboss.beans.metadata.spi.MetaDataVisitor;
import org.jboss.beans.metadata.spi.ValueMetaData;
import org.jboss.dependency.plugins.AbstractDependencyItem;
import org.jboss.dependency.plugins.graph.Search;
import org.jboss.dependency.plugins.graph.SearchDependencyItem;
import org.jboss.dependency.spi.Controller;
import org.jboss.dependency.spi.ControllerContext;
import org.jboss.dependency.spi.ControllerState;
import org.jboss.dependency.spi.DependencyItem;
import org.jboss.dependency.spi.dispatch.AttributeDispatchContext;
import org.jboss.dependency.spi.graph.LookupStrategy;
import org.jboss.dependency.spi.graph.SearchInfo;
import org.jboss.kernel.spi.dependency.KernelControllerContext;
import org.jboss.managed.api.annotation.ManagementProperty;
import org.jboss.reflect.spi.TypeInfo;
import org.jboss.util.JBossStringBuilder;
import org.jboss.xb.annotations.JBossXmlAttribute;

/**
 * Dependency value.
 *
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @author <a href="ales.justin@jboss.com">Ales Justin</a>
 * @author Radim Marek (obrien)
 * @version $Revision$
 */
@XmlType(name="injectionType")
public class AbstractDependencyValueMetaData extends AbstractValueMetaData
{
   private static final long serialVersionUID = 3L;

   /**
    * The context
    */
   protected transient KernelControllerContext context;

   /**
    * The when required, keep it for optional handling
    */
   protected transient ControllerState optionalWhenRequired;

   /**
    * The property name
    */
   protected String property;

   /**
    * The when required state of the dependency or null to use current context state
    */
   protected ControllerState whenRequiredState;

   /**
    * The required state of the dependency or null to look in the registry
    */
   protected ControllerState dependentState;

   /**
    * The search type
    */
   protected SearchInfo search = Search.DEFAULT;


   /**
    * Create a new dependency value
    */
   public AbstractDependencyValueMetaData()
   {
   }

   /**
    * Create a new dependency value
    *
    * @param value the value
    */
   public AbstractDependencyValueMetaData(Object value)
   {
      super(value);
   }

   /**
    * Create a new dependency value
    *
    * @param value    the value
    * @param property the property
    */
   public AbstractDependencyValueMetaData(Object value, String property)
   {
      super(value);
      this.property = property;
   }

   /**
    * Get the property
    *
    * @return the property
    */
   public String getProperty()
   {
      return property;
   }

   /**
    * Set the property
    *
    * @param property the property name
    */
   @XmlAttribute
   public void setProperty(String property)
   {
      this.property = property;
   }

   /**
    * Set the when required state of the dependency
    *
    * @param whenRequiredState the when required state or null if it uses current context state
    */
   @XmlAttribute(name="whenRequired")
   public void setWhenRequiredState(ControllerState whenRequiredState)
   {
      this.whenRequiredState = whenRequiredState;
      flushJBossObjectCache();
   }

   /**
    * Get when required state.
    *
    * @return the when required state
    */
   public ControllerState getWhenRequiredState()
   {
      return whenRequiredState;
   }

   /**
    * Set the required state of the dependency
    *
    * @param dependentState the required state or null if it must be in the registry
    */
   @XmlAttribute(name="state")
   public void setDependentState(ControllerState dependentState)
   {
      this.dependentState = dependentState;
      flushJBossObjectCache();
   }

   /**
    * Get the required state of dependency.
    *
    * @return the required dependency state
    */
   public ControllerState getDependentState()
   {
      return dependentState;
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

   /**
    * Set search type.
    *
    * @param search the search type
    */
   @XmlAttribute(name = "search")
   public void setSearch(SearchInfo search)
   {
      this.search = search;
   }

   /**
    * Get search type.
    *
    * @return the search type
    */
   public SearchInfo getSearch()
   {
      return search;
   }

   /**
    * Is search applied.
    *
    * @return true if search is applied
    */
   protected boolean isSearchApplied()
   {
      return (search != Search.DEFAULT);
   }

   protected boolean isLookupValid(ControllerContext lookup)
   {
      return (lookup != null);
   }

   protected boolean isOptional()
   {
      return false;
   }

   /**
    * Add optional dependency.
    *
    * @param controller the controller
    * @param lookup the lookup context
    */
   protected void addOptionalDependency(Controller controller, ControllerContext lookup)
   {
      OptionalDependencyItem dependency = new OptionalDependencyItem(context.getName(), optionalWhenRequired, lookup.getName(), lookup.getState(), search);
      context.getDependencyInfo().addIDependOn(dependency);
      lookup.getDependencyInfo().addDependsOnMe(dependency);
   }

   /**
    * Get controller context.
    *
    * @param name the name
    * @param state the state
    * @return the controller context
    */
   protected ControllerContext getControllerContext(Object name, ControllerState state)
   {
      Controller controller = context.getController();
      LookupStrategy strategy = search.getStrategy();
      return strategy.getContext(controller, name, state);
   }

   public Object getValue(TypeInfo info, ClassLoader cl) throws Throwable
   {
      ControllerState state = dependentState;
      if (state == null)
         state = ControllerState.INSTALLED;
      if (context == null)
         throw new IllegalStateException("No context for " + this);

      ControllerContext lookup = getControllerContext(getUnderlyingValue(), state);

      if (isLookupValid(lookup) == false)
         throw new Error("Should not be here - dependency failed - " + this);

      if (lookup == null)
      {
         return null;
      }
      else if (isOptional())
      {
         Controller controller = context.getController();
         addOptionalDependency(controller, lookup);
      }

      Object result;
      if (property != null && property.length() > 0)
      {
         if (lookup instanceof AttributeDispatchContext)
         {
            AttributeDispatchContext adc = (AttributeDispatchContext)lookup;
            result = adc.get(property);
         }
         else
            throw new IllegalArgumentException(
                  "Cannot use property attribute, context is not AttributeDispatchContext: " + lookup +
                  ", metadata: " + this);
      }
      else
      {
         result = lookup.getTarget();
      }
      
      return info != null ? info.convertValue(result) : result;
   }

   protected boolean addDependencyItem()
   {
      return true;
   }

   public void initialVisit(MetaDataVisitor visitor)
   {
      if (search == null)
         throw new IllegalArgumentException("Null search");

      context = visitor.getControllerContext();

      ControllerState whenRequired = whenRequiredState;
      if (whenRequired == null)
      {
         whenRequired = visitor.getContextState();
      }

      if (isOptional())
         optionalWhenRequired = whenRequired;               

      // used for sub class optional handling
      if (addDependencyItem())
      {
         Object name = context.getName();
         Object iDependOn = getUnderlyingValue();

         DependencyItem item;
         if (isSearchApplied())
         {
            item = new SearchDependencyItem(name, iDependOn, whenRequired, dependentState, search);
         }
         else
         {
            item = new AbstractDependencyItem(name, iDependOn, whenRequired, dependentState);
         }
         visitor.addDependency(item);
      }
      super.initialVisit(visitor);
   }

   public void toString(JBossStringBuilder buffer)
   {
      super.toString(buffer);
      if (property != null)
         buffer.append(" property=").append(property);
      if (whenRequiredState != null)
         buffer.append(" whenRequiredState=").append(whenRequiredState.getStateString());
      if (dependentState != null)
         buffer.append(" dependentState=").append(dependentState.getStateString());
      if (isSearchApplied())
         buffer.append(" search=").append(search);
   }

   public AbstractDependencyValueMetaData clone()
   {
      return (AbstractDependencyValueMetaData)super.clone();
   }

   /**
    * Optional depedency item.
    */
   protected static class OptionalDependencyItem extends SearchDependencyItem
   {
      public OptionalDependencyItem(Object name, ControllerState optionalWhenRequired, Object iDependOn, ControllerState dependentState, SearchInfo search)
      {
         super(name, iDependOn, optionalWhenRequired, dependentState, search);
         setResolved(true);
      }
   }
}
