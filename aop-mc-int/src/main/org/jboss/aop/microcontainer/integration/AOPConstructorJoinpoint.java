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
package org.jboss.aop.microcontainer.integration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.List;

import org.jboss.aop.Advisor;
import org.jboss.aop.AspectManager;
import org.jboss.aop.advice.Interceptor;
import org.jboss.aop.joinpoint.ConstructorInvocation;
import org.jboss.aop.proxy.container.AOPProxyFactory;
import org.jboss.aop.proxy.container.AOPProxyFactoryParameters;
import org.jboss.aop.proxy.container.ContainerCache;
import org.jboss.aop.proxy.container.GeneratedAOPProxyFactory;
import org.jboss.joinpoint.plugins.BasicConstructorJoinPoint;
import org.jboss.kernel.Kernel;
import org.jboss.kernel.spi.dependency.KernelControllerContext;
import org.jboss.kernel.spi.dependency.KernelControllerContextAware;
import org.jboss.kernel.spi.metadata.KernelMetaDataRepository;
import org.jboss.metadata.spi.MetaData;
import org.jboss.metadata.spi.context.MetaDataContext;
import org.jboss.metadata.spi.retrieval.MetaDataRetrieval;
import org.jboss.metadata.spi.scope.CommonLevels;
import org.jboss.metadata.spi.scope.ScopeKey;
import org.jboss.metadata.spi.signature.MethodSignature;
import org.jboss.metadata.spi.stack.MetaDataStack;
import org.jboss.reflect.spi.ClassInfo;
import org.jboss.reflect.spi.ConstructorInfo;
import org.jboss.reflect.spi.MethodInfo;
import org.jboss.reflect.spi.TypeInfo;

/**
 * An AOPConstructorJoinpoint.
 *
 * @TODO This is not correct if the target is already advised
 *       there is no need for the proxy advisor.
 *
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision$
 */
public class AOPConstructorJoinpoint extends BasicConstructorJoinPoint implements KernelControllerContextAware
{
   private final static String[] EMPTY_PARAM_ARRAY = new String[0];
   
   AOPProxyFactory proxyFactory = new GeneratedAOPProxyFactory();
   
   KernelControllerContext context;
   /**
    * Create a new AOPConstructorJoinpoint.
    *
    * @param constructorInfo the constructor info
    */
   public AOPConstructorJoinpoint(ConstructorInfo constructorInfo)
   {
      super(constructorInfo);
   }

   public void setKernelControllerContext(KernelControllerContext context) throws Exception
   {
      this.context = context;
   }

   public void unsetKernelControllerContext(KernelControllerContext context) throws Exception
   {
      this.context = null;
   }


   public Object dispatch() throws Throwable
   {
      Class clazz = constructorInfo.getDeclaringClass().getType();
      AspectManager manager = AspectManager.instance();
      if (manager.isNonAdvisableClassName(clazz.getName()))
      {
         return super.dispatch();
      }
      MetaData metaData = MetaDataStack.peek();
      MetaDataStack.mask();
      try
      {
         boolean hasInstanceMetaData = hasInstanceOrJoinpointMetaData(metaData);
         ContainerCache cache = ContainerCache.initialise(manager, clazz, metaData, hasInstanceMetaData);
         AOPProxyFactoryParameters params = new AOPProxyFactoryParameters();
         Object target = createTarget(cache, params);
         params.setProxiedClass(target.getClass());
         params.setMetaData(metaData);
         params.setTarget(target);
         params.setContainerCache(cache);
         params.setMetaDataHasInstanceLevelData(hasInstanceMetaData);
         
         return proxyFactory.createAdvisedProxy(params);
      }
      finally
      {
         MetaDataStack.unmask();
      }
   }

   private boolean hasInstanceOrJoinpointMetaData(MetaData metaData)
   {
      if (metaData == null)
      {
         return false;
      }

      MetaDataRetrieval retrieval = null;
      if (context != null)
      {
         //TODO We might need the context injected somehow by the GenericBeanFactory, since that is used for creating the aspect instances...
         Kernel kernel = context.getKernel();
         KernelMetaDataRepository repository = kernel.getMetaDataRepository();
         retrieval = repository.getMetaDataRetrieval(context);
         
         if (retrieval instanceof MetaDataContext)
         {
            ScopeKey instanceKey = new ScopeKey(CommonLevels.INSTANCE, (String)context.getName());
            
            List<MetaDataRetrieval> retrievals =((MetaDataContext)retrieval).getLocalRetrievals();
            for (MetaDataRetrieval ret : retrievals)
            {
               ScopeKey key = ret.getScope();
               if (instanceKey.equals(key))
               {
                  Annotation[] anns = ret.retrieveAnnotations().getValue();
                  if (anns != MetaData.NO_ANNOTATIONS)
                  {
                     return true;
                  }
               }
            }
         }
         
         //Check for method annotations
         if (hasMethodMetaData(metaData))
         {
            return true;
         }
      }      
      
      return false; 
   }
   
   private boolean hasMethodMetaData(MetaData metaData)
   {
      //Check for method annotations
      ClassInfo info = constructorInfo.getDeclaringClass();
      while (info != null)
      {
         MethodInfo[] methods = info.getDeclaredMethods();
         if (methods != null)
         {
            for (MethodInfo mi : methods)
            {
               if (methodHasAnnotations(metaData, mi))
               {
                  return true;
               }
            }
         }
         info = info.getSuperclass();
      }
      
      return false;
   }
   
   private boolean methodHasAnnotations(MetaData metaData, MethodInfo mi)
   {
      TypeInfo[] types = mi.getParameterTypes();
      String[] typeStrings = null;
      
      if (types.length == 0)
      {
         typeStrings = EMPTY_PARAM_ARRAY;
      }
      else
      {
         typeStrings = new String[types.length];
         for (int j = 0 ; j < types.length ; j++)
         {
            typeStrings[j] = types[j].getName();
         }
      }
      MethodSignature sig = new MethodSignature(mi.getName(), typeStrings);
      MetaData methodMD = metaData.getComponentMetaData(sig);
      if (methodMD != null)
      {
         return methodMD.getAnnotations() != MetaData.NO_ANNOTATIONS;
      }
      return false;
   }
   
   private Object createTarget(ContainerCache cache, AOPProxyFactoryParameters params) throws Throwable
   {
      Advisor advisor = cache.getAdvisor();
      if (advisor != null)
      {
         org.jboss.aop.ConstructorInfo aopinfo = findAopConstructorInfo(advisor);
         
         Interceptor[] interceptors = (aopinfo != null) ? aopinfo.getInterceptors() : null;

         if (interceptors != null)
         {
            ConstructorInvocation inv = new ConstructorInvocation(aopinfo, aopinfo.getInterceptors());
            inv.setArguments(getArguments());
            return inv.invokeNext();
         }
         
         if (getConstructorInfo().getParameterTypes().length > 0)
         {
            Constructor constructor = null;
            if (aopinfo == null)
            {
               //Fall back to using the class;
               Class clazz = advisor.getClazz();
               Constructor[] ctors = clazz.getConstructors();
               for (Constructor ctor : ctors)
               {
                  if (matchConstructor(ctor))
                  {
                     constructor = ctor;
                     break;
                  }
               }
            }
            else
            {
               constructor = aopinfo.getConstructor();
            }
            params.setCtor(constructor.getParameterTypes(), getArguments());
         }
      }

      return super.dispatch();
   }

   private org.jboss.aop.ConstructorInfo findAopConstructorInfo(Advisor advisor)
   {
      org.jboss.aop.ConstructorInfo[] infos = advisor.getConstructorInfos();
      for (int i = 0 ; i < infos.length ; i++)
      {
         if (matchConstructor(infos[i].getConstructor()))
         {
            return infos[i];
         }
      }
      return null;
   }
   
   private boolean matchConstructor(Constructor ctor)
   {
      TypeInfo[] params = constructorInfo.getParameterTypes();
      Class[] ctorParams = ctor.getParameterTypes();
      if (params.length == ctorParams.length)
      {
         boolean match = true;
         for (int p = 0 ; p < params.length ; p++)
         {
            if (!params[p].getName().equals(ctorParams[p].getName()))
            {
               match = false;
               break;
            }
         }

         if (match)
         {
            return true;
         }
      }
      return false;
   }

}
