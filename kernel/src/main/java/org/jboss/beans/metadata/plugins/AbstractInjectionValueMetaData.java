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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.jboss.beans.metadata.api.model.AutowireType;
import org.jboss.beans.metadata.api.model.FromContext;
import org.jboss.beans.metadata.api.model.InjectOption;
import org.jboss.beans.metadata.spi.MetaDataVisitor;
import org.jboss.beans.metadata.spi.MetaDataVisitorNode;
import org.jboss.dependency.plugins.AttributeCallbackItem;
import org.jboss.dependency.spi.ControllerContext;
import org.jboss.dependency.spi.ControllerState;
import org.jboss.dependency.spi.DependencyItem;
import org.jboss.kernel.plugins.dependency.SearchClassContextDependencyItem;
import org.jboss.reflect.spi.TypeInfo;
import org.jboss.util.JBossStringBuilder;

/**
 * Injection value.
 *
 * @author <a href="ales.justin@gmail.com">Ales Justin</a>
 */
@XmlType
public class AbstractInjectionValueMetaData extends AbstractDependencyValueMetaData
{
   private static final long serialVersionUID = 3L;

   protected AutowireType injectionType = AutowireType.BY_CLASS;

   protected InjectOption injectionOption = InjectOption.STRICT;

   protected FromContext fromContext;

   /**
    * Simplyifies things with AutowireType.BY_NAME
    */
   protected AbstractPropertyMetaData propertyMetaData;

   /**
    * Create a new injection value
    */
   public AbstractInjectionValueMetaData()
   {
   }

   /**
    * Create a new injection value
    *
    * @param value the value
    */
   public AbstractInjectionValueMetaData(Object value)
   {
      super(value);
   }

   /**
    * Create a new injection value
    *
    * @param value    the value
    * @param property the property
    */
   public AbstractInjectionValueMetaData(Object value, String property)
   {
      super(value, property);
   }

   public AutowireType getInjectionType()
   {
      return injectionType;
   }

   @XmlAttribute(name="type")
   public void setInjectionType(AutowireType injectionType)
   {
      this.injectionType = injectionType;
   }

   public InjectOption getInjectionOption()
   {
      return injectionOption;
   }

   @XmlAttribute(name="option")
   public void setInjectionOption(InjectOption injectionOption)
   {
      this.injectionOption = injectionOption;
   }

   public FromContext getFromContext()
   {
      return fromContext;
   }

   @XmlAttribute(name="fromContext")
   public void setFromContext(FromContext fromContext)
   {
      this.fromContext = fromContext;
   }

   public AbstractPropertyMetaData getPropertyMetaData()
   {
      return propertyMetaData;
   }

   @XmlTransient
   public void setPropertyMetaData(AbstractPropertyMetaData propertyMetaData)
   {
      this.propertyMetaData = propertyMetaData;
   }

   /**
    * Add install/callback item.
    *
    * @param name the callback name
    */
   protected void addInstallItem(Object name)
   {
      if (propertyMetaData == null)
         throw new IllegalArgumentException("Illegal usage of option Callback - injection not used with property = " + this);
      context.getDependencyInfo().addInstallItem(new AttributeCallbackItem<Object>(name, whenRequiredState, dependentState, context, propertyMetaData.getName()));
   }

   protected boolean isLookupValid(ControllerContext lookup)
   {
      boolean lookupExists = super.isLookupValid(lookup);
      boolean isCallback = InjectOption.CALLBACK.equals(injectionOption);
      boolean isOptional = InjectOption.OPTIONAL.equals(injectionOption);
      if (lookupExists == false && isCallback)
      {
         addInstallItem(getUnderlyingValue());
      }
      return lookupExists || isCallback || isOptional;
   }

   protected boolean isOptional()
   {
      return InjectOption.OPTIONAL.equals(injectionOption);
   }

   @SuppressWarnings({"deprecation"})
   public Object getValue(TypeInfo info, ClassLoader cl) throws Throwable
   {
      // controller context property injection
      if (fromContext != null)
      {
         ControllerState state = dependentState;
         if (state == null)
            state = ControllerState.INSTANTIATED;

         ControllerContext lookup = getControllerContext(getUnderlyingValue(), state);
         if (lookup == null)
            throw new Error("Should not be here - dependency failed - " + this);

         return fromContext.executeLookup(lookup);
      }

      // by class type
      if (getUnderlyingValue() == null)
      {
         ControllerContext lookup = getControllerContext(info.getType(), ControllerState.INSTALLED);
         if (lookup == null)
         {
            if (InjectOption.STRICT.equals(injectionOption))
            {
               throw new IllegalArgumentException("Possible multiple matching beans, see log for info.");
            }
            else
            {
               if (InjectOption.CALLBACK.equals(injectionOption))
                  addInstallItem(info.getType());

               return null;
            }
         }
         // TODO - add progression here, then fix BeanMetaData as well
         return lookup.getTarget();
      }
      return super.getValue(info, cl);
   }

   protected boolean addDependencyItem()
   {
      return InjectOption.STRICT.equals(injectionOption) || fromContext != null;
   }

   @XmlTransient
   public Object getUnderlyingValue()
   {
      Object original = super.getUnderlyingValue();
      // might be used for internal compare, in that case context will still be null
      return (fromContext != null && original == null) ? (context != null ? context.getName() : null) : original;
   }

   public void initialVisit(MetaDataVisitor visitor)
   {
      // controller context property injection
      if (fromContext != null)
      {
         // check if dependent is not set when used on itself
         if (super.getUnderlyingValue() == null && dependentState == null)
         {
            dependentState = fromContext.getWhenValid();
         }

         super.initialVisit(visitor);
         return;
      }

      // no bean specified
      if (getUnderlyingValue() == null)
      {
         // check for property
         if (property != null)
         {
            property = null;
            log.warn("Ignoring property - contextual injection: " + this);
         }

         if (AutowireType.BY_NAME.equals(injectionType))
         {
            if (propertyMetaData == null)
               throw new IllegalArgumentException("Illegal usage of type ByName - injection not used with property = " + this);
            setValue(propertyMetaData.getName());
         }
         else
         {
            visitor.initialVisit(this);
         }
      }
      // check if was maybe set with by_name
      if (getUnderlyingValue() != null)
      {
         super.initialVisit(visitor);
      }
   }

   @SuppressWarnings("deprecation")
   public void describeVisit(MetaDataVisitor visitor)
   {
      // no bean and not by_name
      if (getUnderlyingValue() == null)
      {
         if (AutowireType.BY_CLASS.equals(injectionType))
         {
            context = visitor.getControllerContext();

            // dependency item or install item
            if (InjectOption.STRICT.equals(injectionOption))
            {
               // add dependency item only for strict inject behaviour
               // we pop it so that parent node has the same semantics as this one
               // meaning that his current peek is also his parent
               // and all other nodes that cannot determine type follow the same
               // contract - popping and pushing
               // maybe the whole thing can be rewritten to LinkedList
               // or simply using the fact that Stack is also a Vector?
               MetaDataVisitorNode node = visitor.visitorNodeStack().pop();
               try
               {
                  if (node instanceof TypeProvider)
                  {
                     TypeProvider typeProvider = (TypeProvider)node;
                     Class<?> injectionClass = typeProvider.getType(visitor, this).getType();
                     log.debug(context.getName() + ": Contextual injection usage (class -> classloader): " + injectionClass + " -> " + SecurityActions.getClassLoader(injectionClass) + " defined by " + node);
                     // set when required
                     ControllerState whenRequired = whenRequiredState;
                     if (whenRequired == null)
                     {
                        whenRequired = visitor.getContextState();
                     }
                     DependencyItem item = new SearchClassContextDependencyItem(
                           context.getName(),
                           injectionClass,
                           whenRequired,
                           dependentState,
                           search);
                     visitor.addDependency(item);
                  }
                  else
                  {
                     throw new Error(TypeProvider.ERROR_MSG);
                  }
               }
               catch (Error error)
               {
                  throw error;
               }
               catch (Throwable throwable)
               {
                  throw new Error(throwable);
               }
               finally
               {
                  visitor.visitorNodeStack().push(node);
               }
            }
         }
         else
         {
            throw new IllegalArgumentException("Unknown injection type=" + injectionType);
         }
      }
      super.describeVisit(visitor);
   }

   public void toString(JBossStringBuilder buffer)
   {
      super.toString(buffer);
      if (injectionType != null)
         buffer.append(" injectionType=").append(injectionType);
      if (propertyMetaData != null)
         buffer.append(" propertyMetaData=").append(propertyMetaData.getName()); //else overflow - indefinite recursion
      if (fromContext != null)
         buffer.append(" fromContext=").append(fromContext);
   }

   public AbstractInjectionValueMetaData clone()
   {
      return (AbstractInjectionValueMetaData)super.clone();
   }
}
