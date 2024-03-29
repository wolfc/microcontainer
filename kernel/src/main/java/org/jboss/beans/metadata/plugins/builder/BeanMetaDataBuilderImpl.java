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
package org.jboss.beans.metadata.plugins.builder;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.beans.info.spi.BeanAccessMode;
import org.jboss.beans.metadata.api.model.AutowireType;
import org.jboss.beans.metadata.api.model.FromContext;
import org.jboss.beans.metadata.api.model.InjectOption;
import org.jboss.beans.metadata.plugins.AbstractAnnotationMetaData;
import org.jboss.beans.metadata.plugins.AbstractArrayMetaData;
import org.jboss.beans.metadata.plugins.AbstractBeanMetaData;
import org.jboss.beans.metadata.plugins.AbstractCallbackMetaData;
import org.jboss.beans.metadata.plugins.AbstractClassLoaderMetaData;
import org.jboss.beans.metadata.plugins.AbstractCollectionMetaData;
import org.jboss.beans.metadata.plugins.AbstractConstructorMetaData;
import org.jboss.beans.metadata.plugins.AbstractDemandMetaData;
import org.jboss.beans.metadata.plugins.AbstractDependencyMetaData;
import org.jboss.beans.metadata.plugins.AbstractDependencyValueMetaData;
import org.jboss.beans.metadata.plugins.AbstractFeatureMetaData;
import org.jboss.beans.metadata.plugins.AbstractInjectionValueMetaData;
import org.jboss.beans.metadata.plugins.AbstractInstallMetaData;
import org.jboss.beans.metadata.plugins.AbstractListMetaData;
import org.jboss.beans.metadata.plugins.AbstractMapMetaData;
import org.jboss.beans.metadata.plugins.AbstractPropertyMetaData;
import org.jboss.beans.metadata.plugins.AbstractRelatedClassMetaData;
import org.jboss.beans.metadata.plugins.AbstractSetMetaData;
import org.jboss.beans.metadata.plugins.AbstractSupplyMetaData;
import org.jboss.beans.metadata.plugins.AbstractValueFactoryMetaData;
import org.jboss.beans.metadata.plugins.AbstractValueMetaData;
import org.jboss.beans.metadata.plugins.DirectAnnotationMetaData;
import org.jboss.beans.metadata.plugins.StringValueMetaData;
import org.jboss.beans.metadata.plugins.ThisValueMetaData;
import org.jboss.beans.metadata.plugins.AbstractParameterMetaData;
import org.jboss.beans.metadata.spi.AnnotationMetaData;
import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.BeanMetaDataFactory;
import org.jboss.beans.metadata.spi.ClassLoaderMetaData;
import org.jboss.beans.metadata.spi.DemandMetaData;
import org.jboss.beans.metadata.spi.DependencyMetaData;
import org.jboss.beans.metadata.spi.ParameterMetaData;
import org.jboss.beans.metadata.spi.PropertyMetaData;
import org.jboss.beans.metadata.spi.RelatedClassMetaData;
import org.jboss.beans.metadata.spi.SupplyMetaData;
import org.jboss.beans.metadata.spi.ValueMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.beans.metadata.spi.builder.ParameterMetaDataBuilder;
import org.jboss.dependency.spi.Cardinality;
import org.jboss.dependency.spi.ControllerMode;
import org.jboss.dependency.spi.ControllerState;
import org.jboss.dependency.spi.ErrorHandlingMode;
import org.jboss.dependency.spi.graph.SearchInfo;

/**
 * Helper class.
 * Similar to StringBuffer, methods return current instance of BeanMetaDataBuilder.
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 * @author <a href="mailto:adrian@jboss.org">Adrian Brock</a>
 */
class BeanMetaDataBuilderImpl extends BeanMetaDataBuilder
{
   /** The bean metadata */
   private AbstractBeanMetaData beanMetaData;

   /** The constructor builder */
   private ParameterMetaDataBuilderImpl<AbstractConstructorMetaData> constructorBuilder;
   
   /** The create lifecycle builder */
   private LifecycleMetaDataBuilder createBuilder;
   
   /** The start lifecycle builder */
   private LifecycleMetaDataBuilder startBuilder;
   
   /** The stop lifecycle builder */
   private LifecycleMetaDataBuilder stopBuilder;
   
   /** The destroy lifecycle builder */
   private LifecycleMetaDataBuilder destroyBuilder;

   /** The install builder */
   private StateActionBuilder<AbstractInstallMetaData> installBuilder;
   
   /** The uninstall builder */
   private StateActionBuilder<AbstractInstallMetaData> uninstallBuilder;

   /** The incallback builder */
   private StateActionBuilder<AbstractCallbackMetaData> propIncallbackBuilder;

   /** The uncallback builder */
   private StateActionBuilder<AbstractCallbackMetaData> propUncallbackBuilder;

   /** The incallback builder */
   private StateActionBuilder<AbstractCallbackMetaData> incallbackBuilder;

   /** The uncallback builder */
   private StateActionBuilder<AbstractCallbackMetaData> uncallbackBuilder;

   /**
    * Create a new BeanMetaDataBuilderImpl.
    * 
    * @param bean the bean
    */
   public BeanMetaDataBuilderImpl(String bean)
   {
      this(new AbstractBeanMetaData(bean));
   }

   /**
    * Create a new BeanMetaDataBuilderImpl.
    * 
    * @param name the bean name
    * @param bean the bean
    */
   public BeanMetaDataBuilderImpl(String name, String bean)
   {
      this(new AbstractBeanMetaData(name, bean));
   }

   /**
    * Create a new BeanMetaDataBuilderImpl.
    * 
    * @param beanMetaData the bean metadata
    */
   public BeanMetaDataBuilderImpl(AbstractBeanMetaData beanMetaData)
   {
      this.beanMetaData = beanMetaData;
      // lifecycle builders
      createBuilder = createCreateLifecycleMetaDataBuilder(beanMetaData);
      startBuilder = createStartLifecycleMetaDataBuilder(beanMetaData);
      stopBuilder = createStopLifecycleMetaDataBuilder(beanMetaData);
      destroyBuilder = createDestroyLifecycleMetaDataBuilder(beanMetaData);
      // install
      installBuilder = createInstallMetaDataBuilder(beanMetaData);
      uninstallBuilder = createUninstallMetaDataBuilder(beanMetaData);
      // callback
      propIncallbackBuilder = createPropertyInstallCallbackMetaDataBuilder(beanMetaData);
      propUncallbackBuilder = createPropertyUninstallCallbackMetaDataBuilder(beanMetaData);
      incallbackBuilder = createInstallCallbackMetaDataBuilder(beanMetaData);
      uncallbackBuilder = createUninstallCallbackMetaDataBuilder(beanMetaData);
   }

   /**
    * Create lifecycle metadata builder.
    *
    * @param beanMetaData the bean metadata
    * @return the lifecycle metadata builder
    */
   protected LifecycleMetaDataBuilder createCreateLifecycleMetaDataBuilder(AbstractBeanMetaData beanMetaData)
   {
      return new CreateLifecycleMetaDataBuilder(beanMetaData);
   }

   /**
    * Create lifecycle metadata builder.
    *
    * @param beanMetaData the bean metadata
    * @return the lifecycle metadata builder
    */
   protected LifecycleMetaDataBuilder createStartLifecycleMetaDataBuilder(AbstractBeanMetaData beanMetaData)
   {
      return new StartLifecycleMetaDataBuilder(beanMetaData);
   }

   /**
    * Create lifecycle metadata builder.
    *
    * @param beanMetaData the bean metadata
    * @return the lifecycle metadata builder
    */
   protected LifecycleMetaDataBuilder createStopLifecycleMetaDataBuilder(AbstractBeanMetaData beanMetaData)
   {
      return new StopLifecycleMetaDataBuilder(beanMetaData);
   }

   /**
    * Create lifecycle metadata builder.
    *
    * @param beanMetaData the bean metadata
    * @return the lifecycle metadata builder
    */
   protected LifecycleMetaDataBuilder createDestroyLifecycleMetaDataBuilder(AbstractBeanMetaData beanMetaData)
   {
      return new DestroyLifecycleMetaDataBuilder(beanMetaData);
   }

   /**
    * Create install metadata builder.
    *
    * @param beanMetaData the bean metadata
    * @return the install builder
    */
   protected StateActionBuilder<AbstractInstallMetaData> createInstallMetaDataBuilder(AbstractBeanMetaData beanMetaData)
   {
      return new InstallMetaDataBuilder(beanMetaData);
   }

   /**
    * Create install metadata builder.
    *
    * @param beanMetaData the bean metadata
    * @return the install builder
    */
   protected StateActionBuilder<AbstractInstallMetaData> createUninstallMetaDataBuilder(AbstractBeanMetaData beanMetaData)
   {
      return new UninstallMetaDataBuilder(beanMetaData);
   }

   /**
    * Create callback metadata builder.
    *
    * @param beanMetaData the bean metadata
    * @return the callback metadata builder
    */
   private StateActionBuilder<AbstractCallbackMetaData> createPropertyInstallCallbackMetaDataBuilder(AbstractBeanMetaData beanMetaData)
   {
      return new PropertyInstallCallbackMetaDataBuilder(beanMetaData);
   }

   /**
    * Create callback metadata builder.
    *
    * @param beanMetaData the bean metadata
    * @return the callback metadata builder
    */
   private StateActionBuilder<AbstractCallbackMetaData> createPropertyUninstallCallbackMetaDataBuilder(AbstractBeanMetaData beanMetaData)
   {
      return new PropertyUninstallCallbackMetaDataBuilder(beanMetaData);
   }

   /**
    * Create callback metadata builder.
    *
    * @param beanMetaData the bean metadata
    * @return the callback metadata builder
    */
   private StateActionBuilder<AbstractCallbackMetaData> createInstallCallbackMetaDataBuilder(AbstractBeanMetaData beanMetaData)
   {
      return new InstallCallbackMetaDataBuilder(beanMetaData);
   }

   /**
    * Create callback metadata builder.
    *
    * @param beanMetaData the bean metadata
    * @return the callback metadata builder
    */
   private StateActionBuilder<AbstractCallbackMetaData> createUninstallCallbackMetaDataBuilder(AbstractBeanMetaData beanMetaData)
   {
      return new UninstallCallbackMetaDataBuilder(beanMetaData);
   }

   public BeanMetaData getBeanMetaData()
   {
      return beanMetaData;
   }

   public BeanMetaDataFactory getBeanMetaDataFactory()
   {
      return beanMetaData;
   }

   public BeanMetaDataBuilder setName(String name)
   {
      beanMetaData.setName(name);
      return this;
   }

   public BeanMetaDataBuilder setBean(String bean)
   {
      beanMetaData.setBean(bean);
      return this;
   }

   public BeanMetaDataBuilder addRelatedClass(String className, Object... enabled)
   {
      RelatedClassMetaData related = createRelated(className, enabled);
      return addRelatedClass(related);
   }

   public BeanMetaDataBuilder addRelatedClass(RelatedClassMetaData related)
   {
      Set<RelatedClassMetaData> relatedSet = beanMetaData.getRelated();
      if (relatedSet == null)
      {
         relatedSet = new HashSet<RelatedClassMetaData>();
         beanMetaData.setRelated(relatedSet);
      }
      relatedSet.add(related);
      return this;
   }

   public BeanMetaDataBuilder setRelated(Set<RelatedClassMetaData> related)
   {
      beanMetaData.setRelated(related);
      return this;
   }

   public BeanMetaDataBuilder setAliases(Set<Object> aliases)
   {
      beanMetaData.setAliases(aliases);
      return this;
   }

   public BeanMetaDataBuilder addAlias(Object alias)
   {
      Set<Object> aliases = beanMetaData.getAliases();
      if (aliases == null)
      {
         aliases = new HashSet<Object>();
         beanMetaData.setAliases(aliases);
      }
      aliases.add(alias);
      return this;
   }

   /**
    * Create annotation metadata.
    *
    * @param annotation the string annotation
    * @return the annotation metadata
    */
   protected AnnotationMetaData createAnnotationMetaData(String annotation)
   {
      return new AbstractAnnotationMetaData(annotation);
   }

   /**
    * Create annotation metadata.
    *
    * @param annotation the real annotation
    * @return the annotation metadata
    */
   protected AnnotationMetaData createAnnotationMetaData(Annotation annotation)
   {
      return new DirectAnnotationMetaData(annotation);
   }

   /**
    * Create annotation metadata.
    *
    * @param annotation the string annotation
    * @param replace the replace flag
    * @return the annotation metadata
    */
   protected AnnotationMetaData createAnnotationMetaData(String annotation, boolean replace)
   {
      AbstractAnnotationMetaData amd = new AbstractAnnotationMetaData(annotation);
      amd.setReplace(replace);
      return amd;
   }

   public BeanMetaDataBuilder setAnnotations(Set<String> annotations)
   {
      if (annotations != null && annotations.isEmpty() == false)
      {
         Set<AnnotationMetaData> amds = new HashSet<AnnotationMetaData>();
         for (String annotation : annotations)
         {
            AnnotationMetaData amd = createAnnotationMetaData(annotation);
            amds.add(amd);
         }
         beanMetaData.setAnnotations(amds);
      }
      return this;
   }

   /**
    * Get the annotations.
    *
    * @return the annotations
    */
   protected Set<AnnotationMetaData> getAnnotations()
   {
      Set<AnnotationMetaData> annotations = beanMetaData.getAnnotations();
      if (annotations == null)
      {
         annotations = new HashSet<AnnotationMetaData>();
         beanMetaData.setAnnotations(annotations);
      }
      return annotations;
   }

   public BeanMetaDataBuilder addAnnotation(String annotation)
   {
      Set<AnnotationMetaData> annotations = getAnnotations();
      AnnotationMetaData amd = createAnnotationMetaData(annotation);
      annotations.add(amd);
      return this;
   }

   public BeanMetaDataBuilder addAnnotation(Annotation annotation)
   {
      Set<AnnotationMetaData> annotations = getAnnotations();
      AnnotationMetaData amd = createAnnotationMetaData(annotation);
      annotations.add(amd);
      return this;
   }

   public BeanMetaDataBuilder addAnnotation(String annotation, boolean replace)
   {
      Set<AnnotationMetaData> annotations = getAnnotations();
      AnnotationMetaData amd = createAnnotationMetaData(annotation, replace);
      annotations.add(amd);
      return this;
   }

   public BeanMetaDataBuilder setMode(ControllerMode mode)
   {
      beanMetaData.setMode(mode);
      return this;
   }

   public BeanMetaDataBuilder setAccessMode(BeanAccessMode mode)
   {
      beanMetaData.setAccessMode(mode);
      return this;
   }

   public BeanMetaDataBuilder setErrorHandlingMode(ErrorHandlingMode mode)
   {
      beanMetaData.setErrorHandlingMode(mode);
      return this;
   }

   public BeanMetaDataBuilder setAutowireType(AutowireType type)
   {
      beanMetaData.setAutowireType(type);
      return this;
   }

   public BeanMetaDataBuilder setAutowireCandidate(boolean candidate)
   {
      beanMetaData.setAutowireCandidate(candidate);
      return null;
   }

   /**
    * Create class loader metadata.
    *
    * @param classLoader the classloader value
    * @return the classloader metadata
    */
   protected ClassLoaderMetaData createClassLoaderMetaData(ValueMetaData classLoader)
   {
      return new AbstractClassLoaderMetaData(classLoader);
   }

   public BeanMetaDataBuilder setClassLoader(ValueMetaData classLoader)
   {
      ClassLoaderMetaData clvmd = createClassLoaderMetaData(classLoader);
      beanMetaData.setClassLoader(clvmd);
      return this;
   }

   public BeanMetaDataBuilder setClassLoader(ClassLoaderMetaData classLoader)
   {
      beanMetaData.setClassLoader(classLoader);
      return this;
   }

   /**
    * Create abstract constructor metadata.
    *
    * @return abstract constructor metadata
    */
   protected AbstractConstructorMetaData createAbstractConstructorMetaData()
   {
      return new AbstractConstructorMetaData();
   }

   /**
    * Create constructor metadata on demand.
    */
   protected void checkConstructorBuilder()
   {
      AbstractConstructorMetaData constructor = (AbstractConstructorMetaData) beanMetaData.getConstructor();
      if (constructor == null)
      {
         constructor = createAbstractConstructorMetaData();
         beanMetaData.setConstructor(constructor);
         constructorBuilder = new ParameterMetaDataBuilderImpl<AbstractConstructorMetaData>(constructor);
      }
   }

   public BeanMetaDataBuilder setFactory(ValueMetaData factory)
   {
      checkConstructorBuilder();
      AbstractConstructorMetaData constructor = (AbstractConstructorMetaData) beanMetaData.getConstructor();
      constructor.setFactory(factory);
      return this;
   }

   public BeanMetaDataBuilder setFactoryClass(String factoryClass)
   {
      checkConstructorBuilder();
      AbstractConstructorMetaData constructor = (AbstractConstructorMetaData) beanMetaData.getConstructor();
      constructor.setFactoryClass(factoryClass);
      return this;
   }

   public BeanMetaDataBuilder setFactoryMethod(String factoryMethod)
   {
      checkConstructorBuilder();
      AbstractConstructorMetaData constructor = (AbstractConstructorMetaData) beanMetaData.getConstructor();
      constructor.setFactoryMethod(factoryMethod);
      return this;
   }

   public BeanMetaDataBuilder setConstructorValue(ValueMetaData value)
   {
      checkConstructorBuilder();
      AbstractConstructorMetaData constructor = (AbstractConstructorMetaData) beanMetaData.getConstructor();
      constructor.setValue(value);
      return this;
   }

   public BeanMetaDataBuilder addConstructorParameter(String type, Object value)
   {
      checkConstructorBuilder();
      constructorBuilder.addParameterMetaData(type, value);
      return this;
   }

   public BeanMetaDataBuilder addConstructorParameter(String type, String value)
   {
      checkConstructorBuilder();
      constructorBuilder.addParameterMetaData(type, value);
      return this;
   }

   public BeanMetaDataBuilder addConstructorParameter(String type, ValueMetaData value)
   {
      checkConstructorBuilder();
      constructorBuilder.addParameterMetaData(type, value);
      return this;
   }

   /**
    * Create property metadata.
    *
    * @param name the name
    * @param value the value
    * @return property metadata
    */
   protected PropertyMetaData createPropertyMetaData(String name, Object value)
   {
      return new AbstractPropertyMetaData(name, value);
   }

   public BeanMetaDataBuilder addPropertyMetaData(String name, Object value)
   {
      Set<PropertyMetaData> properties = getProperties();
      removeProperty(properties, name);
      properties.add(createPropertyMetaData(name, value));
      return this;
   }

   /**
    * Create property metadata.
    *
    * @param name the name
    * @param value the value
    * @return property metadata
    */
   protected PropertyMetaData createPropertyMetaData(String name, String value)
   {
      return new AbstractPropertyMetaData(name, value);
   }

   public BeanMetaDataBuilder addPropertyMetaData(String name, String value)
   {
      Set<PropertyMetaData> properties = getProperties();
      removeProperty(properties, name);
      properties.add(createPropertyMetaData(name, value));
      return this;
   }

   /**
    * Create property metadata.
    *
    * @param name the name
    * @param value the value
    * @return property metadata
    */
   protected PropertyMetaData createPropertyMetaData(String name, ValueMetaData value)
   {
      return new AbstractPropertyMetaData(name, value);
   }

   public BeanMetaDataBuilder addPropertyMetaData(String name, ValueMetaData value)
   {
      Set<PropertyMetaData> properties = getProperties();
      removeProperty(properties, name);
      properties.add(createPropertyMetaData(name, value));
      return this;
   }

   public BeanMetaDataBuilder addPropertyMetaData(String name, Collection<ValueMetaData> value)
   {
      Set<PropertyMetaData> properties = getProperties();
      removeProperty(properties, name);
      
      if (value instanceof ValueMetaData)
      {
         properties.add(createPropertyMetaData(name, (ValueMetaData)value));
      }
      else
      {
         properties.add(createPropertyMetaData(name, value));
      }
      return this;
   }

   public BeanMetaDataBuilder addPropertyMetaData(String name, Map<ValueMetaData, ValueMetaData> value)
   {
      Set<PropertyMetaData> properties = getProperties();
      removeProperty(properties, name);

      if (value instanceof ValueMetaData)
      {
         properties.add(createPropertyMetaData(name, (ValueMetaData)value));
      }
      else
      {
         properties.add(createPropertyMetaData(name, value));
      }
      return this;
   }

   public BeanMetaDataBuilder addPropertyAnnotation(String name, String annotation)
   {
      AnnotationMetaData amd = createAnnotationMetaData(annotation);
      return addPropertyAnnotation(name, amd);
   }

   public BeanMetaDataBuilder addPropertyAnnotation(String name, String annotation, boolean replace)
   {
      AnnotationMetaData amd = createAnnotationMetaData(annotation, replace);
      return addPropertyAnnotation(name, amd);
   }

   public BeanMetaDataBuilder addPropertyAnnotation(String name, Annotation annotation)
   {
      AnnotationMetaData amd = createAnnotationMetaData(annotation);
      return addPropertyAnnotation(name, amd);
   }

   /**
    * Add property annotation metadata.
    *
    * @param name the property name
    * @param amd the annotation metadata
    * @return this builder
    */
   protected BeanMetaDataBuilder addPropertyAnnotation(String name, AnnotationMetaData amd)
   {
      PropertyMetaData pmd = beanMetaData.getProperty(name);
      Set<AnnotationMetaData> annotations = pmd.getAnnotations();
      if (annotations == null)
      {
         if (pmd instanceof AbstractFeatureMetaData == false)
            throw new IllegalArgumentException("PropertyMetaData is not AbstractFeatureMetaData instance: " + pmd);

         annotations = new HashSet<AnnotationMetaData>();
         AbstractFeatureMetaData afmd = AbstractFeatureMetaData.class.cast(pmd);
         afmd.setAnnotations(annotations);
      }
      annotations.add(amd);
      return this;
   }

   /**
    * Remove previous matching property.
    *
    * @param properties the properties
    * @param name the name
    * @return modified set of properties
    */
   private Set<PropertyMetaData> removeProperty(Set<PropertyMetaData> properties, String name)
   {
      for (Iterator<PropertyMetaData> it = properties.iterator() ; it.hasNext() ; )
      {
         PropertyMetaData property = it.next();
         if (name.equals(property.getName()))
         {
            it.remove();
         }
      }
      return properties;
   }

   /**
    * Get the properties.
    *
    * @return the properties
    */
   private Set<PropertyMetaData> getProperties()
   {
      Set<PropertyMetaData> properties = beanMetaData.getProperties();
      if (properties == null)
      {
         properties = new HashSet<PropertyMetaData>();
         beanMetaData.setProperties(properties);
      }
      return properties;
   }

   public BeanMetaDataBuilder ignoreCreate()
   {
      createBuilder.setIgnored();
      return this;
   }

   public BeanMetaDataBuilder setCreate(String methodName)
   {
      createBuilder.createStateActionMetaData(methodName);
      return this;
   }

   public BeanMetaDataBuilder addCreateParameter(String type, Object value)
   {
      createBuilder.addParameterMetaData(type, value);
      return this;
   }

   public BeanMetaDataBuilder addCreateParameter(String type, String value)
   {
      createBuilder.addParameterMetaData(type, value);
      return this;
   }

   public BeanMetaDataBuilder addCreateParameter(String type, ValueMetaData value)
   {
      createBuilder.addParameterMetaData(type, value);
      return this;
   }

   public BeanMetaDataBuilder ignoreStart()
   {
      startBuilder.setIgnored();
      return this;
   }

   public BeanMetaDataBuilder setStart(String methodName)
   {
      startBuilder.createStateActionMetaData(methodName);
      return this;
   }

   public BeanMetaDataBuilder addStartParameter(String type, Object value)
   {
      startBuilder.addParameterMetaData(type, value);
      return this;
   }

   public BeanMetaDataBuilder addStartParameter(String type, String value)
   {
      startBuilder.addParameterMetaData(type, value);
      return this;
   }

   public BeanMetaDataBuilder addStartParameter(String type, ValueMetaData value)
   {
      startBuilder.addParameterMetaData(type, value);
      return this;
   }

   public BeanMetaDataBuilder ignoreStop()
   {
      stopBuilder.setIgnored();
      return this;
   }

   public BeanMetaDataBuilder setStop(String methodName)
   {
      stopBuilder.createStateActionMetaData(methodName);
      return this;
   }

   public BeanMetaDataBuilder addStopParameter(String type, Object value)
   {
      stopBuilder.addParameterMetaData(type, value);
      return this;
   }

   public BeanMetaDataBuilder addStopParameter(String type, String value)
   {
      stopBuilder.addParameterMetaData(type, value);
      return this;
   }

   public BeanMetaDataBuilder addStopParameter(String type, ValueMetaData value)
   {
      stopBuilder.addParameterMetaData(type, value);
      return this;
   }

   public BeanMetaDataBuilder ignoreDestroy()
   {
      destroyBuilder.setIgnored();
      return this;
   }

   public BeanMetaDataBuilder setDestroy(String methodName)
   {
      destroyBuilder.createStateActionMetaData(methodName);
      return this;
   }

   public BeanMetaDataBuilder addDestroyParameter(String type, Object value)
   {
      destroyBuilder.addParameterMetaData(type, value);
      return this;
   }

   public BeanMetaDataBuilder addDestroyParameter(String type, String value)
   {
      destroyBuilder.addParameterMetaData(type, value);
      return this;
   }

   public BeanMetaDataBuilder addDestroyParameter(String type, ValueMetaData value)
   {
      destroyBuilder.addParameterMetaData(type, value);
      return this;
   }

   /**
    * Create supply metadata.
    *
    * @param supply the supply
    * @param type the type
    * @return supply metadata
    */
   protected SupplyMetaData createSupplyMetaData(Object supply, String type)
   {
      AbstractSupplyMetaData asmd = new AbstractSupplyMetaData(supply);
      if (type != null)
         asmd.setType(type);
      return asmd;
   }

   public BeanMetaDataBuilder addSupply(Object supply, String type)
   {
      Set<SupplyMetaData> supplies = beanMetaData.getSupplies();
      if (supplies == null)
      {
         supplies = new HashSet<SupplyMetaData>();
         beanMetaData.setSupplies(supplies);
      }
      supplies.add(createSupplyMetaData(supply, type));
      return this;
   }

   /**
    * Create demand metadata.
    *
    * @param demand the demand
    * @param whenRequired the when required
    * @param targetState the target state
    * @param transformer the transformer
    * @return the demand metadata
    */
   protected DemandMetaData createDemandMetaData(Object demand, ControllerState whenRequired, ControllerState targetState, String transformer)
   {
      AbstractDemandMetaData admd = new AbstractDemandMetaData(demand);
      if (whenRequired != null)
         admd.setWhenRequired(whenRequired);
      if (targetState != null)
         admd.setTargetState(targetState);
      if (transformer != null)
         admd.setTransformer(transformer);
      return admd;
   }

   public BeanMetaDataBuilder addDemand(Object demand, ControllerState whenRequired, ControllerState targetState, String transformer)
   {
      Set<DemandMetaData> demands = beanMetaData.getDemands();
      if (demands == null)
      {
         demands = new HashSet<DemandMetaData>();
         beanMetaData.setDemands(demands);
      }
      demands.add(createDemandMetaData(demand, whenRequired, targetState, transformer));
      return this;
   }

   /**
    * Create dependency metadata.
    *
    * @param dependency the dependency
    * @return the dependency metadata
    */
   protected DependencyMetaData createDependencyMetaData(Object dependency)
   {
      return new AbstractDependencyMetaData(dependency);
   }

   public BeanMetaDataBuilder addDependency(Object dependency)
   {
      Set<DependencyMetaData> dependencies = beanMetaData.getDepends();
      if (dependencies == null)
      {
         dependencies = new HashSet<DependencyMetaData>();
         beanMetaData.setDepends(dependencies);
      }
      dependencies.add(createDependencyMetaData(dependency));
      return this;
   }

   public ParameterMetaDataBuilder addInstallWithParameters(String methodName, String bean, ControllerState state, ControllerState whenRequired)
   {
      AbstractInstallMetaData install = installBuilder.createStateActionMetaData(methodName);
      install.setBean(bean);
      if (state != null)
         install.setDependentState(state);
      if (whenRequired != null)
         install.setState(whenRequired);
      return new ParameterMetaDataBuilderImpl<AbstractInstallMetaData>(install);
   }

   public ParameterMetaDataBuilder addUninstallWithParameters(String methodName, String bean, ControllerState state, ControllerState whenRequired)
   {
      AbstractInstallMetaData uninstall = uninstallBuilder.createStateActionMetaData(methodName);
      uninstall.setBean(bean);
      if (state != null)
         uninstall.setDependentState(state);
      if (whenRequired != null)
         uninstall.setState(whenRequired);
      return new ParameterMetaDataBuilderImpl<AbstractInstallMetaData>(uninstall);
   }

   public BeanMetaDataBuilder addPropertyInstallCallback(String property, String signature, ControllerState whenRequired, ControllerState dependentState, Cardinality cardinality)
   {
      AbstractCallbackMetaData callback = propIncallbackBuilder.createStateActionMetaData(property);
      callback.setSignature(signature);
      callback.setState(whenRequired);
      if (dependentState != null)
         callback.setDependentState(dependentState);
      callback.setCardinality(cardinality);
      return this;
   }

   public BeanMetaDataBuilder addPropertyUninstallCallback(String property, String signature, ControllerState whenRequired, ControllerState dependentState, Cardinality cardinality)
   {
      AbstractCallbackMetaData callback = propUncallbackBuilder.createStateActionMetaData(property);
      callback.setSignature(signature);
      callback.setState(whenRequired);
      if (dependentState != null)
         callback.setDependentState(dependentState);
      callback.setCardinality(cardinality);
      return this;
   }

   public BeanMetaDataBuilder addMethodInstallCallback(String method, String signature, ControllerState whenRequired, ControllerState dependentState, Cardinality cardinality)
   {
      AbstractCallbackMetaData callback = incallbackBuilder.createStateActionMetaData(method);
      callback.setSignature(signature);
      callback.setState(whenRequired);
      if (dependentState != null)
         callback.setDependentState(dependentState);
      callback.setCardinality(cardinality);
      return this;
   }

   public BeanMetaDataBuilder addMethodUninstallCallback(String method, String signature, ControllerState whenRequired, ControllerState dependentState, Cardinality cardinality)
   {
      AbstractCallbackMetaData callback = uncallbackBuilder.createStateActionMetaData(method);
      callback.setSignature(signature);
      callback.setState(whenRequired);
      if (dependentState != null)
         callback.setDependentState(dependentState);
      callback.setCardinality(cardinality);
      return this;
   }

   public RelatedClassMetaData createRelated(String className, Object... enabled)
   {
      AbstractRelatedClassMetaData related = new AbstractRelatedClassMetaData();
      related.setClassName(className);
      if (enabled != null && enabled.length > 0)
         related.setEnabled(new HashSet<Object>(Arrays.asList(enabled)));
      return related;
   }

   public ValueMetaData createNull()
   {
      return new AbstractValueMetaData();
   }

   public ValueMetaData createThis()
   {
      return new ThisValueMetaData();
   }

   public ValueMetaData createValue(Object value)
   {
      return new AbstractValueMetaData(value);
   }

   public ValueMetaData createValueFactory(Object bean, String method, ParameterMetaData... parameters)
   {
      AbstractValueFactoryMetaData value = new AbstractValueFactoryMetaData(bean, method);
      value.setParameters(Arrays.asList(parameters));
      return value;
   }

   public ParameterMetaData createParameter(ValueMetaData value, String type, int index)
   {
      ParameterMetaData parameter = new AbstractParameterMetaData(type, value);
      parameter.setIndex(index);
      return parameter;
   }

   public ValueMetaData createString(String type, String value)
   {
      StringValueMetaData result = new StringValueMetaData(value);
      result.setType(type);
      return result;
   }

   /**
    * Create abstract dependency metadata.
    *
    * @param bean the bean
    * @param property the property
    * @return abstract dependency value metadata
    */
   protected AbstractDependencyValueMetaData createAbstractDependencyValueMetaData(Object bean, String property)
   {
      return new AbstractDependencyValueMetaData(bean, property);
   }

   public ValueMetaData createInject(Object bean, String property, ControllerState whenRequired, ControllerState dependentState, SearchInfo search)
   {
      AbstractDependencyValueMetaData result = createAbstractDependencyValueMetaData(bean, property);
      if (whenRequired != null)
         result.setWhenRequiredState(whenRequired);
      if (dependentState != null)
         result.setDependentState(dependentState);
      if (search != null)
         result.setSearch(search);
      return result;
   }

   /**
    * Create abstract dependency metadata.
    *
    * @return abstract dependency value metadata
    */
   protected AbstractInjectionValueMetaData createAbstractInjectionValueMetaData()
   {
      return new AbstractInjectionValueMetaData();
   }

   /**
    * Create abstract dependency metadata.
    *
    * @param name the name
    * @return abstract dependency value metadata
    */
   protected AbstractInjectionValueMetaData createAbstractInjectionValueMetaData(Object name)
   {
      return new AbstractInjectionValueMetaData(name);
   }

   public ValueMetaData createContextualInject(ControllerState whenRequired, ControllerState dependentState, AutowireType autowire, InjectOption option, SearchInfo search)
   {
      AbstractInjectionValueMetaData result = createAbstractInjectionValueMetaData();
      if (whenRequired != null)
         result.setWhenRequiredState(whenRequired);
      if (dependentState != null)
         result.setDependentState(dependentState);
      if (autowire != null)
         result.setInjectionType(autowire);
      if (option != null)
         result.setInjectionOption(option);
      if (search != null)
         result.setSearch(search);
      return result;
   }

   public ValueMetaData createFromContextInject(FromContext fromContext, Object contextName, ControllerState dependentState, SearchInfo search)
   {
      AbstractInjectionValueMetaData result = createAbstractInjectionValueMetaData(contextName);
      result.setFromContext(fromContext);
      if (dependentState != null)
         result.setDependentState(dependentState);
      if (search != null)
         result.setSearch(search);
      return result;
   }

   @SuppressWarnings("unchecked")
   public Collection<ValueMetaData> createCollection(String collectionType, String elementType)
   {
      AbstractCollectionMetaData collection = new AbstractCollectionMetaData();
      if (collectionType != null)
         collection.setType(collectionType);
      if (elementType != null)
         collection.setElementType(elementType);
      return (Collection) collection;
   }

   @SuppressWarnings("unchecked")
   public List<ValueMetaData> createList(String listType, String elementType)
   {
      AbstractListMetaData collection = new AbstractListMetaData();
      if (listType != null)
         collection.setType(listType);
      if (elementType != null)
         collection.setElementType(elementType);
      return (List) collection;
   }

   @SuppressWarnings("unchecked")
   public Set<ValueMetaData> createSet(String setType, String elementType)
   {
      AbstractSetMetaData collection = new AbstractSetMetaData();
      if (setType != null)
         collection.setType(setType);
      if (elementType != null)
         collection.setElementType(elementType);
      return (Set) collection;
   }
   
   @SuppressWarnings("unchecked")
   public List<ValueMetaData> createArray(String arrayType, String elementType)
   {
      AbstractArrayMetaData collection = new AbstractArrayMetaData();
      if (arrayType != null)
         collection.setType(arrayType);
      if (elementType != null)
         collection.setElementType(elementType);
      return (List) collection;
   }
   
   @SuppressWarnings("unchecked")
   public Map<ValueMetaData, ValueMetaData> createMap(String mapType, String keyType, String valueType)
   {
      AbstractMapMetaData map = new AbstractMapMetaData();
      if (mapType != null)
         map.setType(mapType);
      if (keyType != null)
         map.setKeyType(keyType);
      if (valueType != null)
         map.setValue(valueType);
      return (Map) map;
   }
}
