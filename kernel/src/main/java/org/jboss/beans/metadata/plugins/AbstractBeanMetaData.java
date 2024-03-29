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

import static org.jboss.beans.metadata.plugins.CloneUtil.cloneList;
import static org.jboss.beans.metadata.plugins.CloneUtil.cloneObject;
import static org.jboss.beans.metadata.plugins.CloneUtil.cloneSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.jboss.beans.info.spi.BeanAccessMode;
import org.jboss.beans.metadata.api.model.AutowireType;
import org.jboss.beans.metadata.spi.AliasMetaData;
import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.BeanMetaDataFactory;
import org.jboss.beans.metadata.spi.CallbackMetaData;
import org.jboss.beans.metadata.spi.ClassLoaderMetaData;
import org.jboss.beans.metadata.spi.ConstructorMetaData;
import org.jboss.beans.metadata.spi.DemandMetaData;
import org.jboss.beans.metadata.spi.DependencyMetaData;
import org.jboss.beans.metadata.spi.InstallMetaData;
import org.jboss.beans.metadata.spi.LifecycleMetaData;
import org.jboss.beans.metadata.spi.MetaDataVisitor;
import org.jboss.beans.metadata.spi.MetaDataVisitorNode;
import org.jboss.beans.metadata.spi.PropertyMetaData;
import org.jboss.beans.metadata.spi.RelatedClassMetaData;
import org.jboss.beans.metadata.spi.SupplyMetaData;
import org.jboss.dependency.plugins.AbstractDependencyItem;
import org.jboss.dependency.spi.Controller;
import org.jboss.dependency.spi.ControllerContext;
import org.jboss.dependency.spi.ControllerMode;
import org.jboss.dependency.spi.ControllerState;
import org.jboss.dependency.spi.DependencyItem;
import org.jboss.dependency.spi.ErrorHandlingMode;
import org.jboss.kernel.spi.dependency.KernelControllerContext;
import org.jboss.managed.api.annotation.ManagementObject;
import org.jboss.managed.api.annotation.ManagementProperties;
import org.jboss.managed.api.annotation.ManagementProperty;
import org.jboss.reflect.spi.TypeInfo;
import org.jboss.util.JBossObject;
import org.jboss.util.JBossStringBuilder;

/**
 * Metadata for a bean.
 *
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @author <a href="ales.justin@jboss.com">Ales Justin</a>
 * @version $Revision$
 */
@ManagementObject(properties = ManagementProperties.EXPLICIT) // TODO - explicitly add props we want to manage
@XmlRootElement(name="bean")
@XmlType(name="beanType", propOrder={"aliasMetaData", "related", "annotations", "classLoader", "constructor", "properties", "create", "start", "stop", "destroy", "depends", "demands", "supplies", "installs", "uninstalls", "installCallbacks", "uninstallCallbacks"})
public class AbstractBeanMetaData extends AbstractFeatureMetaData
   implements BeanMetaData, BeanMetaDataFactory, MutableLifecycleHolder, Serializable
{
   private static final long serialVersionUID = 4L;

   /** The bean fully qualified class name */
   protected String bean;

   /** The name of this instance */
   protected String name;

   /** The related */
   protected Set<RelatedClassMetaData> related;

   /** The aliases */
   protected Set<Object> aliases;

   /** The alias metadata */
   protected Set<AliasMetaData> aliasMetaData;

   /** The parent */
   protected String parent;

   /** Is abstract */
   protected boolean isAbstract; 

   /** Autowire type */
   protected AutowireType autowireType;

   /** The mode */
   protected ControllerMode mode;

   /** The error handling mode */
   protected ErrorHandlingMode errorHandlingMode;

   /** The access mode */
   protected BeanAccessMode accessMode;

   /** Is contextual injection candidate */
   protected boolean autowireCandidate = true;

   /** The properties configuration Set<PropertyMetaData> */
   private Set<PropertyMetaData> properties;

   /** The bean ClassLoader */
   protected ClassLoaderMetaData classLoader;

   /** The constructor */
   protected ConstructorMetaData constructor;

   /** The create lifecycle */
   protected LifecycleMetaData create;

   /** The start lifecycle */
   protected LifecycleMetaData start;

   /** The stop lifecycle */
   protected LifecycleMetaData stop;

   /** The destroy lifecycle */
   protected LifecycleMetaData destroy;

   /** What the bean demands Set<DemandMetaData> */
   protected Set<DemandMetaData> demands;

   /** What the bean supplies Set<SupplyMetaData> */
   protected Set<SupplyMetaData> supplies;

   /** What the bean dependencies Set<DependencyMetaData> */
   protected Set<DependencyMetaData> depends;

   /** The install operations List<InstallMetaData> */
   protected List<InstallMetaData> installs;

   /** The uninstall operations List<InstallMetaData> */
   protected List<InstallMetaData> uninstalls;

   /** The install callback List<InstallMetaData> */
   protected List<CallbackMetaData> installCallbacks;

   /** The uninstall callback List<InstallMetaData> */
   protected List<CallbackMetaData> uninstallCallbacks;

   /** The nested beans list */
   protected transient List<BeanMetaData> beans;

   /** The context */
   protected transient ControllerContext context;

   /**
    * Create a new bean meta data
    */
   public AbstractBeanMetaData()
   {
      super();
   }

   /**
    * Create a new bean meta data
    *
    * @param bean the bean class name
    */
   public AbstractBeanMetaData(String bean)
   {
      this.bean = bean;
   }
   /**
    * Create a new bean meta data
    *
    * @param name the name
    * @param bean the bean class name
    */
   public AbstractBeanMetaData(String name, String bean)
   {
      this.name = name;
      this.bean = bean;
   }

    public List<BeanMetaData> getBeans()
    {
       if (beans == null)
       {
          NestedBeanHandler handler = createNestedBeanHandler();
          beans = handler.checkForNestedBeans();
       }
       return beans;
    }

   /**
    * Create nested bean handler.
    * Can be overridden to change generateName policy.
    *
    * @return nested bean handler
    */
   protected NestedBeanHandler createNestedBeanHandler()
   {
      return new NestedBeanHandler(this);
   }

   /**
    * Get the bean class name.
    * @return the fully qualified bean class name.
    */
   public String getBean()
   {
      return bean;
   }

   /**
    * Set the bean class name and flush the object cache.
    *
    * @param bean The bean class name to set.
    */
   @XmlAttribute(name="class")
   public void setBean(String bean)
   {
      this.bean = bean;
      flushJBossObjectCache();
   }

   /**
    * Get a property
    *
    * @param name the name
    * @return the property name
    */
   public PropertyMetaData getProperty(String name)
   {
      if (name == null)
         throw new IllegalArgumentException("Null name");
      if (properties != null && properties.size() > 0)
      {
         for (PropertyMetaData prop : properties)
         {
            if (name.equals(prop.getName()))
               return prop;
         }
      }
      return null;
   }

   /**
    * Add a property
    *
    * @param property the property
    */
   public void addProperty(PropertyMetaData property)
   {
      if (property == null)
         throw new IllegalArgumentException("Null property");
      if (properties == null)
         properties = new HashSet<PropertyMetaData>();
      properties.add(property);
      flushJBossObjectCache();
   }

   /**
    * Set the propertiess.
    *
    * @param properties Set<PropertyMetaData>
    */
   @ManagementProperty(managed=true) // TODO - this ok? 
   @XmlElement(name="property", type=AbstractPropertyMetaData.class)
   public void setProperties(Set<PropertyMetaData> properties)
   {
      this.properties = properties;
      flushJBossObjectCache();
   }

   public ClassLoaderMetaData getClassLoader()
   {
      return classLoader;
   }

   @XmlElement(name="classloader", type=AbstractClassLoaderMetaData.class)
   public void setClassLoader(ClassLoaderMetaData classLoader)
   {
      this.classLoader = classLoader;
   }

   /**
    * Set the constructor
    *
    * @param constructor the constructor metadata
    */
   @XmlElement(name="constructor", type=AbstractConstructorMetaData.class)
   public void setConstructor(ConstructorMetaData constructor)
   {
      this.constructor = constructor;
   }

   /**
    * Set what the bean demands.
    *
    * @param demands Set<DemandMetaData>
    */
   @XmlElement(name="demand", type=AbstractDemandMetaData.class)
   public void setDemands(Set<DemandMetaData> demands)
   {
      this.demands = demands;
      flushJBossObjectCache();
   }

   /**
    * Set what the bean supplies.
    *
    * @param supplies Set<SupplyMetaData>
    */
   @XmlElement(name="supply", type=AbstractSupplyMetaData.class)
   public void setSupplies(Set<SupplyMetaData> supplies)
   {
      this.supplies = supplies;
      flushJBossObjectCache();
   }

   /**
    * Set what the bean depends.
    *
    * @param depends Set<DependencyMetaData>
    */
   @XmlElement(name="depends", type=AbstractDependencyMetaData.class)
   public void setDepends(Set<DependencyMetaData> depends)
   {
      this.depends = depends;
      flushJBossObjectCache();
   }

   public String getName()
   {
      return name;
   }

   /**
    * Set the name.
    *
    * @param name The name to set.
    */
   @XmlAttribute
   public void setName(String name)
   {
      this.name = name;
      flushJBossObjectCache();
   }

   public Set<RelatedClassMetaData> getRelated()
   {
      return related;
   }

   @XmlElement(name="related-class", type=AbstractRelatedClassMetaData.class)
   public void setRelated(Set<RelatedClassMetaData> related)
   {
      this.related = related;
   }

   public Set<Object> getAliases()
   {
      return aliases;
   }

   @XmlTransient
   public void setAliases(Set<Object> aliases)
   {
      this.aliases = aliases;
   }

   public Set<AliasMetaData> getAliasMetaData()
   {
      return aliasMetaData;
   }
   
   @XmlElement(name="alias", type=AbstractAliasMetaData.class)
   public void setAliasMetaData(Set<AliasMetaData> aliases)
   {
      this.aliasMetaData = aliases;
   }
   
   public String getParent()
   {
      return parent;
   }

   /**
    * Set the parent.
    *
    * @param parent the parent name
    */
   @XmlAttribute
   public void setParent(String parent)
   {
      this.parent = parent;
   }

   public boolean isAbstract()
   {
      return isAbstract;
   }

   /**
    * Set abstract.
    *
    * @param anAbstract is abstract
    */
   @XmlAttribute
   public void setAbstract(boolean anAbstract)
   {
      isAbstract = anAbstract;
   }

   public AutowireType getAutowireType()
   {
      return autowireType;
   }

   /**
    * Set autowire type.
    *
    * @param autowireType the type
    */
   @XmlAttribute(name="autowire-type")
   public void setAutowireType(AutowireType autowireType)
   {
      this.autowireType = autowireType;
   }

   public ControllerMode getMode()
   {
      return mode;
   }

   @XmlAttribute
   public void setMode(ControllerMode mode)
   {
      this.mode = mode;
      flushJBossObjectCache();
   }

   public ErrorHandlingMode getErrorHandlingMode()
   {
      return errorHandlingMode;
   }

   @XmlAttribute(name="error-handling")
   public void setErrorHandlingMode(ErrorHandlingMode errorHandlingMode)
   {
      this.errorHandlingMode = errorHandlingMode;
   }

   public BeanAccessMode getAccessMode()
   {
      return accessMode;
   }

   @XmlAttribute(name="access-mode")
   public void setAccessMode(BeanAccessMode accessMode)
   {
      this.accessMode = accessMode;
   }

   public boolean isAutowireCandidate()
   {
      return autowireCandidate;
   }

   @XmlAttribute(name="autowire-candidate")
   public void setAutowireCandidate(boolean autowireCandidate)
   {
      this.autowireCandidate = autowireCandidate;
   }

   public Set<PropertyMetaData> getProperties()
   {
      return properties;
   }

   public ConstructorMetaData getConstructor()
   {
      return constructor;
   }

   public LifecycleMetaData getCreate()
   {
      return create;
   }

   /**
    * Set the lifecycle metadata
    *
    * @param lifecycle the lifecycle metadata
    */
   @XmlElement(name="create", type=AbstractLifecycleMetaData.class)
   public void setCreate(LifecycleMetaData lifecycle)
   {
      if (lifecycle != null)
         lifecycle.setState(ControllerState.CREATE);
      this.create = lifecycle;
   }

   public LifecycleMetaData getStart()
   {
      return start;
   }

   /**
    * Set the start metadata
    *
    * @param lifecycle the lifecycle metadata
    */
   @XmlElement(name="start", type=AbstractLifecycleMetaData.class)
   public void setStart(LifecycleMetaData lifecycle)
   {
      if (lifecycle != null)
         lifecycle.setState(ControllerState.START);
      this.start = lifecycle;
   }

   public LifecycleMetaData getStop()
   {
      return stop;
   }

   /**
    * Set the stop metadata
    *
    * @param lifecycle the lifecycle metadata
    */
   @XmlElement(name="stop", type=AbstractLifecycleMetaData.class)
   public void setStop(LifecycleMetaData lifecycle)
   {
      if (lifecycle != null)
         lifecycle.setState(ControllerState.START);
      this.stop = lifecycle;
   }

   public LifecycleMetaData getDestroy()
   {
      return destroy;
   }

   /**
    * Set the destroy metadata
    *
    * @param lifecycle the lifecycle metadata
    */
   @XmlElement(name="destroy", type=AbstractLifecycleMetaData.class)
   public void setDestroy(LifecycleMetaData lifecycle)
   {
      if (lifecycle != null)
         lifecycle.setState(ControllerState.CREATE);
      this.destroy = lifecycle;
   }

   public Set<DemandMetaData> getDemands()
   {
      return demands;
   }

   public Set<SupplyMetaData> getSupplies()
   {
      return supplies;
   }

   public Set<DependencyMetaData> getDepends()
   {
      return depends;
   }

   public List<InstallMetaData> getInstalls()
   {
      return installs;
   }

   /**
    * Set the installs
    *
    * @param installs List<InstallMetaData>
    */
   @XmlElement(name="install", type=AbstractInstallMetaData.class)
   public void setInstalls(List<InstallMetaData> installs)
   {
      this.installs = installs;
      flushJBossObjectCache();
   }

   public List<InstallMetaData> getUninstalls()
   {
      return uninstalls;
   }

   /**
    * Set the uninstalls
    *
    * @param uninstalls List<InstallMetaData>
    */
   @XmlElement(name="uninstall", type=AbstractInstallMetaData.class)
   public void setUninstalls(List<InstallMetaData> uninstalls)
   {
      this.uninstalls = uninstalls;
      flushJBossObjectCache();
   }

   public List<CallbackMetaData> getInstallCallbacks()
   {
      return installCallbacks;
   }

   @XmlElement(name="incallback", type=InstallCallbackMetaData.class)
   public void setInstallCallbacks(List<CallbackMetaData> installCallbacks)
   {
      this.installCallbacks = installCallbacks;
      flushJBossObjectCache();
   }

   public List<CallbackMetaData> getUninstallCallbacks()
   {
      return uninstallCallbacks;
   }

   @XmlElement(name="uncallback", type=UninstallCallbackMetaData.class)
   public void setUninstallCallbacks(List<CallbackMetaData> uninstallCallbacks)
   {
      this.uninstallCallbacks = uninstallCallbacks;
      flushJBossObjectCache();
   }

   public void initialVisit(MetaDataVisitor visitor)
   {
      ConstructorMetaData constructor = getConstructor();
      if (getBean() == null)
      {
         if (isAbstract() == false && getParent() == null)
         {
            if (constructor == null)
               throw new IllegalArgumentException("Bean should have a class attribute or a constructor element.");
            if (constructor.getFactoryMethod() == null)
            {
               if (constructor.getValue() == null)
                  throw new IllegalArgumentException("Bean should have a class attribute or the constructor element should have either a factoryMethod attribute or embedded value.");
            }
            else if (constructor.getFactory() == null && constructor.getFactoryClass() == null)
               throw new IllegalArgumentException("Bean should have a class attribute or the constructor element should have one of a factoryClass attribute or a factory element, or embedded value.");
         }
      }
      else
      {
         checkConstructorFactoryClass(constructor);
      }

      KernelControllerContext ctx = visitor.getControllerContext();
      if (ctx.getBeanMetaData() == this)
         context = ctx;
      boolean isInnerBean = visitor.visitorNodeStack().isEmpty() == false;
      if (isInnerBean)
      {
         Object name = ctx.getName();
         Object iDependOn = getUnderlyingValue();
         ControllerState whenRequired = visitor.getContextState();
         DependencyItem di = new AbstractDependencyItem(name, iDependOn, whenRequired, ControllerState.INSTALLED);
         visitor.addDependency(di);
      }
      if (create != null && create.getMethodName() == null)
         create.setMethodName("create");
      if (start != null && start.getMethodName() == null)
         start.setMethodName("start");
      if (stop != null && stop.getMethodName() == null)
         stop.setMethodName("stop");
      if (destroy != null && destroy.getMethodName() == null)
         destroy.setMethodName("destroy");
      super.initialVisit(visitor);
   }

   /**
    * Check constructor factory class.
    *
    * @param constructor the constructor meta data
    */
   protected void checkConstructorFactoryClass(ConstructorMetaData constructor)
   {
      if (constructor == null)
         return;
      if (constructor.getFactoryMethod() == null)
         return;
      if (constructor.getFactoryClass() != null)
         return;
      if (constructor.getValue() != null)
         return;
      if (constructor.getFactory() != null)
         return;
      if (constructor instanceof AbstractConstructorMetaData == false)
         return;

      AbstractConstructorMetaData acmd = AbstractConstructorMetaData.class.cast(constructor);
      acmd.setFactoryClass(getBean());
   }

   protected void addChildren(Set<MetaDataVisitorNode> children)
   {
      super.addChildren(children);
      if (classLoader != null && classLoader.getClassLoader() != this)
         children.add(classLoader);
      if (constructor != null)
         children.add(constructor);
      if (properties != null)
         children.addAll(properties);
      if (create != null)
         children.add(create);
      if (start != null)
         children.add(start);
      if (stop != null)
         children.add(stop);
      if (destroy != null)
         children.add(destroy);
      if (demands != null)
         children.addAll(demands);
      if (supplies != null)
         children.addAll(supplies);
      if (depends != null)
         children.addAll(depends);
      if (installs != null)
         children.addAll(installs);
      if (uninstalls != null)
         children.addAll(uninstalls);
      if (installCallbacks != null)
         children.addAll(installCallbacks);
      if (uninstallCallbacks != null)
         children.addAll(uninstallCallbacks);
      if (aliasMetaData != null)
         children.addAll(aliasMetaData);
      if (related != null)
         children.addAll(related);
   }

   public TypeInfo getType(MetaDataVisitor visitor, MetaDataVisitorNode previous) throws Throwable
   {
      throw new IllegalArgumentException("Cannot determine inject class type: " + this);
   }

   public Object getUnderlyingValue()
   {
      return name;
   }

   @SuppressWarnings({"deprecation"})
   public Object getValue(TypeInfo info, ClassLoader cl) throws Throwable
   {
      if (context == null)
         throw new IllegalStateException("Context has not been set: " + this);
      Controller controller = context.getController();
      ControllerContext lookup = controller.getInstalledContext(name);
      if (lookup == null || lookup.getTarget() == null)
      {
         // possible call for classloader
         if (info == null && classLoader != null && classLoader.getClassLoader() == this)
         {
            return cl;
         }
         throw new IllegalArgumentException("Bean not yet installed: " + name);
      }
      Object target = lookup.getTarget();
      // TODO - add progression here as well?
      if (info != null && info.getType().isAssignableFrom(target.getClass()) == false)
      {
         throw new ClassCastException(target + " is not a " + info);
      }
      return target;
   }

   public void toString(JBossStringBuilder buffer)
   {
      buffer.append("name=").append(name);
      if (aliases != null)
         buffer.append(" aliases=").append(aliases);
      buffer.append(" bean=").append(bean);
      buffer.append(" properties=");
      JBossObject.list(buffer, properties);
      if (classLoader != null && classLoader.getClassLoader() != this)
         buffer.append(" classLoader=").append(classLoader);
      buffer.append(" constructor=").append(constructor);
      buffer.append(" autowireCandidate=").append(autowireCandidate);
      if (create != null)
         buffer.append(" create=").append(create);
      if (start != null)
         buffer.append(" start=").append(start);
      if (stop != null)
         buffer.append(" stop=").append(stop);
      if (destroy != null)
         buffer.append(" destroy=").append(destroy);
      if (demands != null)
      {
         buffer.append(" demands=");
         JBossObject.list(buffer, demands);
      }
      super.toString(buffer);
      if (supplies != null)
      {
         buffer.append(" supplies=");
         JBossObject.list(buffer, supplies);
      }
      if (depends != null)
      {
         buffer.append(" depends=");
         JBossObject.list(buffer, depends);
      }
      if (installs != null)
      {
         buffer.append(" installs=");
         JBossObject.list(buffer, installs);
      }
      if (uninstalls != null)
      {
         buffer.append(" uninstalls=");
         JBossObject.list(buffer, uninstalls);
      }
      if (installCallbacks != null)
      {
         buffer.append(" installCallbacks=");
         JBossObject.list(buffer, installCallbacks);
      }
      if (uninstallCallbacks != null)
      {
         buffer.append(" uninstallCallbacks=");
         JBossObject.list(buffer, uninstallCallbacks);
      }
      if (aliasMetaData != null)
      {
         buffer.append(" aliasMetaData=");
         JBossObject.list(buffer, aliasMetaData);
      }
      if (related != null)
      {
         buffer.append(" related=");
         JBossObject.list(buffer, related);
      }
   }

   public void toShortString(JBossStringBuilder buffer)
   {
      buffer.append(bean);
      buffer.append('/');
      buffer.append(name);
   }

   public AbstractBeanMetaData clone()
   {
      AbstractBeanMetaData clone = (AbstractBeanMetaData)super.clone();
      doClone(clone);
      return clone;
   }

   @SuppressWarnings("unchecked")
   protected void doClone(AbstractBeanMetaData clone)
   {
      super.doClone(clone);
      if (aliases != null)
         clone.setAliases(new HashSet<Object>(aliases));
      clone.setAliasMetaData(cloneSet(aliasMetaData, HashSet.class, AliasMetaData.class));
      clone.setRelated(cloneSet(related, HashSet.class, RelatedClassMetaData.class));
      clone.setClassLoader(cloneObject(classLoader, ClassLoaderMetaData.class));
      clone.setConstructor(cloneObject(constructor, ConstructorMetaData.class));
      clone.setCreate(cloneObject(create, LifecycleMetaData.class));
      clone.setDemands(cloneSet(demands, HashSet.class, DemandMetaData.class));
      clone.setDepends(cloneSet(depends, HashSet.class, DependencyMetaData.class));
      clone.setDestroy(cloneObject(destroy, LifecycleMetaData.class));
      clone.setInstallCallbacks(cloneList(installCallbacks, ArrayList.class, CallbackMetaData.class));
      clone.setInstalls(cloneList(installs, ArrayList.class, InstallMetaData.class));
      clone.setProperties(cloneSet(properties, HashSet.class, PropertyMetaData.class));
      clone.setStart(cloneObject(start, LifecycleMetaData.class));
      clone.setStop(cloneObject(stop, LifecycleMetaData.class));
      clone.setSupplies(cloneSet(supplies, HashSet.class, SupplyMetaData.class));
      clone.setUninstallCallbacks(cloneList(uninstallCallbacks, ArrayList.class, CallbackMetaData.class));
      clone.setUninstalls(cloneList(uninstalls, ArrayList.class, InstallMetaData.class));
   }
}
