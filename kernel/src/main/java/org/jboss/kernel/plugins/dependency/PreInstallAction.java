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
package org.jboss.kernel.plugins.dependency;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.WeakHashMap;

import org.jboss.beans.info.spi.BeanInfo;
import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.dependency.plugins.AbstractController;
import org.jboss.dependency.spi.ControllerState;
import org.jboss.kernel.Kernel;
import org.jboss.kernel.spi.config.KernelConfigurator;
import org.jboss.kernel.spi.dependency.KernelController;
import org.jboss.kernel.spi.dependency.KernelControllerContext;
import org.jboss.kernel.spi.metadata.KernelMetaDataRepository;
import org.jboss.metadata.spi.MetaData;
import org.jboss.metadata.spi.repository.MutableMetaDataRepository;
import org.jboss.metadata.spi.scope.Scope;
import org.jboss.metadata.spi.scope.ScopeFactory;
import org.jboss.metadata.spi.scope.ScopeFactoryLookup;
import org.jboss.metadata.spi.scope.ScopeKey;

/**
 * PreInstallAction.
 *
 * @author <a href="ales.justin@jboss.com">Ales Justin</a>
 */
public class PreInstallAction extends InstallsAwareAction
{
   private Map<Class<? extends ScopeFactory<? extends Annotation>>, WeakReference<ScopeFactory<? extends Annotation>>> factories = new WeakHashMap<Class<? extends ScopeFactory<? extends Annotation>>, WeakReference<ScopeFactory<? extends Annotation>>>();

   /**
    * Get the scope factory.
    *
    * @param clazz the class key
    * @return scope factory
    * @throws Throwable for any error
    */
   protected ScopeFactory<? extends Annotation> getScopeFactory(Class<? extends ScopeFactory<? extends Annotation>> clazz) throws Throwable
   {
      ScopeFactory<? extends Annotation> factory = null;
      WeakReference<ScopeFactory<? extends Annotation>> weak = factories.get(clazz);
      if (weak != null)
      {
         factory = weak.get();
         if (factory != null)
            return factory;
      }

      factory = clazz.newInstance();
      factories.put(clazz, new WeakReference<ScopeFactory<? extends Annotation>>(factory));
      return factory;
   }

   protected void installActionInternal(KernelControllerContext context) throws Throwable
   {
      KernelController controller = (KernelController)context.getController();
      Kernel kernel = controller.getKernel();
      KernelConfigurator configurator = kernel.getConfigurator();

      BeanMetaData metaData = context.getBeanMetaData();
      if (metaData.getBean() != null)
      {
         BeanInfo info = configurator.getBeanInfo(metaData);
         context.setBeanInfo(info);

         KernelMetaDataRepository repository = controller.getKernel().getMetaDataRepository();
         ClassLoader oldCL = SecurityActions.setContextClassLoader(context);
         try
         {
            repository.addMetaData(context);
         }
         finally
         {
            SecurityActions.resetContextClassLoader(oldCL);
         }
         try
         {
            applyScoping(context);
         }
         catch (Throwable t)
         {
            removeMetaData(context);
            throw t;
         }
      }
   }

   @SuppressWarnings("unchecked")
   protected ScopeKey getInstallScopeKey(
         KernelControllerContext context,
         KernelMetaDataRepository repository) throws Throwable
   {
      ScopeKey scopeKey = context.getScopeInfo().getInstallScope();
      if (scopeKey != null)
         return scopeKey;

      MetaData retrieval = repository.getMetaData(context);
      if (retrieval != null)
      {
         Annotation[] annotations = retrieval.getAnnotations();
         if (annotations != null && annotations.length > 0)
         {
            Collection<Scope> scopes = new HashSet<Scope>();
            for (Annotation annotation : annotations)
            {
               if (annotation.annotationType().isAnnotationPresent(ScopeFactoryLookup.class))
               {
                  ScopeFactoryLookup sfl = annotation.annotationType().getAnnotation(ScopeFactoryLookup.class);
                  ScopeFactory scf = getScopeFactory(sfl.value()); 
                  Scope scope = scf.create(annotation);
                  scopes.add(scope);
               }
            }
            if (scopes.size() > 0)
            {
               return new ScopeKey(scopes);
            }
         }
      }
      return null;
   }

   protected void applyScoping(KernelControllerContext context) throws Throwable
   {
      KernelController controller = (KernelController)context.getController();
      KernelMetaDataRepository repository = controller.getKernel().getMetaDataRepository();
      ScopeKey scopeKey = getInstallScopeKey(context, repository);
      if (scopeKey != null)
      {
         scopeKey.freeze();
         context.getScopeInfo().setInstallScope(scopeKey);

         if (controller instanceof AbstractKernelController == false)
            throw new IllegalArgumentException("Can only handle AbstractKernelController: " + controller);

         MutableMetaDataRepository mmdr = repository.getMetaDataRepository();
         AbstractController abstractController = ScopeHierarchyBuilder.buildControllerHierarchy((AbstractKernelController)controller, mmdr, scopeKey);
         if (abstractController instanceof ScopedKernelController == false)
            throw new IllegalArgumentException("Should be ScopedKernelController instance: " + abstractController);

         ScopedKernelController scopedController = (ScopedKernelController)abstractController;
         scopedController.addScopedControllerContext(context);
      }
   }

   protected void removeScoping(KernelControllerContext context) throws Throwable
   {
      ScopeKey scopeKey = context.getScopeInfo().getInstallScope();
      if (scopeKey != null)
      {
         KernelController controller = (KernelController)context.getController();
         KernelMetaDataRepository repository = controller.getKernel().getMetaDataRepository();
         // find scoped controller
         MutableMetaDataRepository mmdr = repository.getMetaDataRepository();
         ScopeHierarchyBuilder.cleanControllerHierarchy(mmdr, scopeKey, context);
      }
   }

   protected void uninstallActionInternal(KernelControllerContext context)
   {
      try
      {
         removeScoping(context);
      }
      catch (Throwable ignored)
      {
         log.warn("Unexpected error removing scoping: ", ignored);
      }
      finally
      {
         removeMetaData(context);
         context.setBeanInfo(null);
      }
   }

   /**
    * Remove any previously added metadata
    *
    * @param context the context
    */
   private void removeMetaData(KernelControllerContext context)
   {
      try
      {
         KernelController controller = (KernelController)context.getController();
         KernelMetaDataRepository repository = controller.getKernel().getMetaDataRepository();
         repository.removeMetaData(context);
      }
      catch (Throwable ignored)
      {
         log.warn("Unexpected error removing metadata: ", ignored);
      }
   }

   protected ControllerState getState()
   {
      return ControllerState.PRE_INSTALL;
   }
}
