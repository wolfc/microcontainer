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
package org.jboss.aop.microcontainer.beans;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jboss.aop.AspectManager;
import org.jboss.aop.advice.AspectDefinition;
import org.jboss.aop.advice.AspectFactory;
import org.jboss.aop.advice.Scope;
import org.jboss.aop.advice.ScopeUtil;
import org.jboss.aop.instrument.Untransformable;
import org.jboss.beans.metadata.plugins.factory.GenericBeanFactory;
import org.jboss.kernel.spi.dependency.KernelControllerContext;
import org.jboss.kernel.spi.dependency.KernelControllerContextAware;
import org.jboss.logging.Logger;
import org.w3c.dom.Element;

/**
 * An Aspect.
 * This installs the AspectDefinition and AspectFactory into aop
 *
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision$
 */
public class Aspect implements Untransformable, KernelControllerContextAware
{
   private static final Logger log = Logger.getLogger(Aspect.class);

   /**
    * The AspectManager/Domain we are creating this aspect for
    */
   protected AspectManager manager;

   /**
    * True if aspect is an aspect factory, rather than the aspect itself
    */
   protected boolean factory; 

   /**
    * The scope of the aspect we are creating
    */
   protected Scope scope;

   protected ManagedAspectDefinition definition;

   /**
    * The beanfactory representing the advice. This should be used if the advice has no dependencies,
    * in which case we have a real dependency on the beanfactory. If the advice has dependencies we need
    * to use adviceBean instead;
    */
   protected GenericBeanFactory advice;

   /**
    * The name of the beanfactory representing the advice. This should be used if the advice has dependencies,
    * in which case we have no real dependency on the beanfactory. If the advice has no dependencies we need
    * to use advice instead;    
    */
   protected String adviceBean;

   /**
    * The name of the aspect definition. This should be the same as the raw form of this bean's name 
    * (when run in AS, the name is massaged to avoid name clashes)
    */
   protected String name;

   /**
    * All the AspectBindings referencing this Aspect
    */
   protected Map<String, Binding> bindings = new LinkedHashMap<String, Binding>();
   
   /**
    * The element representing the interceptor in case we need to load xml
    */
   private   Element element;

   /**
    * The KernelControllerContext
    */
   private KernelControllerContext context;
   
   /**
    * Get the name.
    *
    * @return the adviceName.
    */
   public String getName()
   {
      return name;
   }

   /**
    * Sets if we are an aspect factory or not
    * 
    * @param factory true if we are a factory
    */
   public void setFactory(boolean factory)
   {
      this.factory = factory;
   }

   /**
    * Set the name.
    *
    * @param name The name to set.
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * Get the adviceBean.
    *
    * @return the adviceBean.
    */
   public String getAdviceBean()
   {
      return adviceBean;
   }

   /**
    * Set the adviceBean.
    *
    * @param adviceBean the adviceBean.
    */
   public void setAdviceBean(String adviceBean)
   {
      this.adviceBean = adviceBean;
   }

   /**
    * Get the definition.
    *
    * @return the definition.
    */
   public AspectDefinition getDefinition()
   {
      return definition;
   }

   /**
    * Get the manager.
    *
    * @return the manager.
    */
   public AspectManager getManager()
   {
      return manager;
   }

   /**
    * Set the manager.
    *
    * @param manager The manager to set.
    */
   public void setManager(AspectManager manager)
   {
      this.manager = manager;
   }

   /**
    * Get the advice.
    *
    * @return the advice.
    */
   public GenericBeanFactory getAdvice()
   {
      return advice;
   }

   /**
    * Set the advice.
    *
    * @param advice The advice to set.
    */
   public void setAdvice(GenericBeanFactory advice)
   {
      this.advice = advice;
   }

   /**
    * Get the scope.
    *
    * @return the scope.
    */
   public String getScope()
   {
      return scope.toString();
   }

   /**
    * Set the scope.
    *
    * @param scope The scope to set.
    */
   public void setScope(String scope)
   {
      this.scope = ScopeUtil.parse(scope);
   }

   public void setElement(Element element)
   {
      this.element = element;
   }

   public void install(GenericBeanFactory factory) throws Exception
   {
      this.advice = factory;
      start();
   }

   public void start() throws Exception
   {
      if (definition == null)
      {
         if (manager == null)
            throw new IllegalArgumentException("Null manager");
         if (name == null)
            throw new IllegalArgumentException("Null name");
         
         if (advice != null)
         {
            definition = getAspectDefinitionNoDependencies();
         }
         else if (adviceBean != null)
         {
            definition = getAspectDefintionDependencies();
         }
         else
         {
            throw new IllegalStateException("Unknown type of managed aspects");
         }
         addDefinitionToManager();
      }

      if (adviceBean != null && advice != null)
      {
         definition.setDeployed(true);
         BeanFactoryAwareAspectFactory factory = (BeanFactoryAwareAspectFactory)definition.getFactory();
         factory.setBeanFactory(advice);
      }
      
      setDefinitionControllerContext(context);
      
      //Copy the aspectbindings to avoid ConcurrentModificationExceptions
      ArrayList<Binding> clonedBindings = new ArrayList<Binding>();
      for (Binding aspectBinding : bindings.values())
      {
         clonedBindings.add(aspectBinding);
      }
      
      for (Binding binding : clonedBindings)
      {
         binding.rebind();
      }
         
      log.debug("Bound aspect " + name + "; deployed:" + definition.isDeployed());
   }

   protected ManagedAspectDefinition getAspectDefinitionNoDependencies()
   {
      AspectFactory factory = this.factory ?  
            new DelegatingBeanAspectFactory(name, advice, element) : new GenericBeanAspectFactory(name, advice, element);
      return new ManagedAspectDefinition(name, scope, factory);
   }

   protected ManagedAspectDefinition getAspectDefintionDependencies()
   {
      AspectFactory factory = this.factory ?  
            new DelegatingBeanAspectFactory(name, advice, element) : new GenericBeanAspectFactory(name, advice, element);
      return new ManagedAspectDefinition(name, scope, factory, adviceBean, false);
   }

   protected void addDefinitionToManager()
   {
      manager.addAspectDefinition(definition);
   }

   public void uninstall() throws Exception
   {
      stop();
   }


   public void stop()
   {
      log.debug("Unbinding aspect " + name);
      manager.removeAspectDefinition(name);
      if (definition != null)
      {
         setDefinitionControllerContext(null);
         definition.undeploy();
         definition = null;
      }
   }
   
   void addBinding(Binding binding)
   {
      bindings.put(binding.getName(), binding);
   }
   
   void removeBinding(Binding binding)
   {
      bindings.remove(binding.getName());
   }

   public void setKernelControllerContext(KernelControllerContext context) throws Exception
   {
      this.context = context;
   }

   public void unsetKernelControllerContext(KernelControllerContext context) throws Exception
   {
      this.context = null;
   }
   
   protected void setDefinitionControllerContext(KernelControllerContext context)
   {
      try
      {
         if (definition != null)
         {
            ((KernelControllerContextAware)definition.getFactory()).setKernelControllerContext(context);
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}