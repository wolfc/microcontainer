/*
* JBoss, Home of Professional Open Source.
* Copyright 2006, Red Hat Middleware LLC, and individual contributors
* as indicated by the @author tags. See the copyright.txt file in the
* distribution for a full listing of individual contributors. 
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
package org.jboss.aop.microcontainer.beans.metadata;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


import org.jboss.aop.microcontainer.beans.AOPDomain;
import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.BeanMetaDataFactory;
import org.jboss.beans.metadata.spi.ValueMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.xb.annotations.JBossXmlSchema;

/**
 * 
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
@JBossXmlSchema(namespace="urn:jboss:aop-beans:1.0", elementFormDefault=XmlNsForm.QUALIFIED)
@XmlRootElement(name="domain")
//Use the same propOrder as the GenericBeanMetaDataFactory, but remove the annotations part since we don't want that to be handled by the super class
@XmlType(name="domainType", propOrder={"aliases", "classLoader", "constructor", "properties", "create", "start", "depends", "demands", "supplies", "installs", "uninstalls", "installCallbacks", "uninstallCallbacks"})
public class DomainBeanMetaDataFactory extends AspectManagerAwareBeanMetaDataFactory
{
   private static final long serialVersionUID = 1L;
   
   //Same defaults as org.jboss.aop.Domain
   Boolean parentFirst;
   Boolean inheritDefinitions = true;
   Boolean inheritBindings;
   String extendz;

   List<BeanMetaDataFactory> childBeans;
   
   public DomainBeanMetaDataFactory()
   {
   }
   

   @XmlAttribute(name="parentFirst")
   public void setParentFirst(boolean parentFirst)
   {
      this.parentFirst = parentFirst;
   }
   
   public boolean getParentFirst()
   {
      return parentFirst;
   }

   @XmlAttribute(name="inheritDefinitions")
   public void setInheritDefinitions(boolean  inheritDefinitions)
   {
      this.inheritDefinitions = inheritDefinitions;
   }

   public boolean getInheritDefinitions()
   {
      return inheritDefinitions;
   }

   @XmlAttribute(name="inheritBindings")
   public void setInheritBindings(boolean  inheritBindings)
   {
      this.inheritBindings = inheritBindings;
   }

   public boolean getInheritBindings()
   {
      return inheritBindings;
   }

   @XmlAttribute
   public void setExtends(String extendz)
   {
      this.extendz = extendz;
   }

   public String getExtends()
   {
      return extendz;
   }

   @XmlElements
   ({
      @XmlElement(name="annotation", type=AnnotationOverrideBeanMetaDataFactory.class),
      @XmlElement(name="annotation-introduction", type=AnnotationIntroductionBeanMetaDataFactory.class),
      @XmlElement(name="arrayreplacement", type=ArrayReplacementBeanMetaDataFactory.class),
      @XmlElement(name="arraybind", type=ArrayBindBeanMetaDataFactory.class),
      @XmlElement(name="aspect", type=AspectBeanMetaDataFactory.class),
      @XmlElement(name="bind", type=BindBeanMetaDataFactory.class),
      @XmlElement(name="cflow-stack", type=CFlowStackBeanMetaDataFactory.class),
      @XmlElement(name="declare-error", type=DeclareErrorBeanMetaDataFactory.class),
      @XmlElement(name="declare-warning", type=DeclareWarningBeanMetaDataFactory.class),
      @XmlElement(name="dynamic-cflow", type=DynamicCflowBeanMetaDataFactory.class),
      @XmlElement(name="interceptor", type=InterceptorBeanMetaDataFactory.class),
      @XmlElement(name="introduction", type=IntroductionBeanMetaDataFactory.class),
      @XmlElement(name="lifecycle-configure", type=ConfigureLifecycleBeanMetaDataFactory.class),
      @XmlElement(name="lifecycle-create", type=CreateLifecycleBeanMetaDataFactory.class),
      @XmlElement(name="lifecycle-describe", type=DescribeLifecycleBeanMetaDataFactory.class),
      @XmlElement(name="lifecycle-install", type=InstallLifecycleBeanMetaDataFactory.class),
      @XmlElement(name="lifecycle-instantiate", type=InstantiateLifecycleBeanMetaDataFactory.class),
      @XmlElement(name="lifecycle-preinstall", type=PreInstallLifecycleBeanMetaDataFactory.class),
      @XmlElement(name="lifecycle-start", type=StartLifecycleBeanMetaDataFactory.class),
      @XmlElement(name="metadata", type=MetaDataBeanMetaDataFactory.class),
      @XmlElement(name="metadata-loader", type=MetaDataLoaderBeanMetaDataFactory.class),
      @XmlElement(name="pointcut", type=NamedPointcutBeanMetaDataFactory.class),
      @XmlElement(name="precedence", type=PrecedenceBeanMetaDataFactory.class),
      @XmlElement(name="prepare", type=PrepareMetaDataFactory.class),
      @XmlElement(name="stack", type=StackBeanMetaDataFactory.class),
      @XmlElement(name="typedef", type=TypeDefBeanMetaDataFactory.class)
   })
   @XmlAnyElement
   public void setChildren(List<BeanMetaDataFactory> childBeans)
   {
      this.childBeans = childBeans;
   }
   
   public List<BeanMetaDataFactory> getChildren()
   {
      return childBeans;
   }
   
   @Override
   public List<BeanMetaData> getBeans()
   {
      ArrayList<BeanMetaData> result = new ArrayList<BeanMetaData>();
      
      BeanMetaDataBuilder domainBuilder = AOPBeanMetaDataBuilder.createBuilder(getName(), AOPDomain.class.getName());
      domainBuilder.addPropertyMetaData("name", getName());
    
      if (parentFirst != null)
      {
         domainBuilder.addPropertyMetaData("parentFirst", parentFirst);
      }
      if (inheritDefinitions != null)
      {
         domainBuilder.addPropertyMetaData("inheritDefinitions", inheritDefinitions);
      }
      if (inheritBindings != null)
      {
         domainBuilder.addPropertyMetaData("inheritBindings", inheritBindings);
      }
      if (extendz != null)
      {
         ValueMetaData value = domainBuilder.createInject(extendz);
         domainBuilder.addPropertyMetaData("parent", value);
      }
      
      setAspectManagerProperty(domainBuilder);
      result.add(domainBuilder.getBeanMetaData());
      
      if (childBeans != null && childBeans.size() > 0)
      {
         for (BeanMetaDataFactory child : childBeans)
         {
            if (child instanceof AspectManagerAwareBeanMetaDataFactory)
            {
               //Set this domain as the manager of the child beans
               AspectManagerAwareBeanMetaDataFactory amChild = (AspectManagerAwareBeanMetaDataFactory)child;
               amChild.setManagerBean(name);
               amChild.setManagerProperty("domain");
               amChild.setAspectManagerProperty(AOPBeanMetaDataBuilder.createBuilder(bean));
            }
            List<BeanMetaData> childResult = child.getBeans();
            if (childResult != null && childBeans.size() > 0)
            {
               result.addAll(childResult);
            }
         }
      }
      
      return result;
   }
}
