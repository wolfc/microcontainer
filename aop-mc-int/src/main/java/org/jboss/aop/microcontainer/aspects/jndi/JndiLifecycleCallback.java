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
package org.jboss.aop.microcontainer.aspects.jndi;

import java.util.Properties;

import javax.naming.InitialContext;

import org.jboss.aop.joinpoint.Invocation;
import org.jboss.aop.joinpoint.MethodInvocation;
import org.jboss.dependency.spi.ControllerContext;
import org.jboss.kernel.spi.dependency.KernelControllerContext;
import org.jboss.logging.Logger;
import org.jboss.metadata.spi.MetaData;
import org.jboss.util.naming.Util;

/**
 * The jndi lifecycle callback.
 * This puts the beans marked with @JndiBinding into jndi.
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
public class JndiLifecycleCallback
{
   private static final Logger log = Logger.getLogger(JndiLifecycleCallback.class);
   private Properties env;

   public String getName()
   {
      return getClass().getName();
   }
 
   /**
    * Get the InitialContext properties to use for binding/unbinding
    * @return the InitialContext ctor env
    */
   public Properties getEnv()
   {
      return env;
   }

   /**
    * Set the InitialContext properties to use for binding/unbinding
    * @param env - the InitialContext ctor env
    */
   public void setEnv(Properties env)
   {
      this.env = env;
   }

   /**
    * Bind the target on setKernelControllerContext, unbind on any other method provided that
    * the invocation has a JndiBinding annotation.
    * 
    * @param invocation the invocation
    * @return the result
    * @throws Throwable for any error
    */
   public Object invoke(Invocation invocation) throws Throwable
   {
      if (invocation instanceof MethodInvocation == false)
         throw new IllegalStateException("Invocation is not an instance of MethodInvocation: " + invocation);
      
      MethodInvocation mi = (MethodInvocation) invocation;
      KernelControllerContext context = (KernelControllerContext) mi.getArguments()[0];

      boolean trace = log.isTraceEnabled();
      JndiBinding bindingInfo = (JndiBinding) invocation.resolveClassAnnotation(JndiBinding.class);
      if( trace )
         log.trace("Checking method: "+mi.getMethod()+", bindingInfo: "+bindingInfo);
      // If this is the setKernelControllerContext callback, bind the target into jndi
      if ("setKernelControllerContext".equals(mi.getMethod().getName()) && bindingInfo != null)
      {
         InitialContext ctx = new InitialContext(env);
         Object target = context.getTarget();
         Util.bind(ctx, bindingInfo.name(), target);
         if( trace )
            log.trace("Bound to: "+bindingInfo.name());
         String[] aliases = bindingInfo.aliases();
         if( aliases != null )
         {
            for(String name : aliases)
            {
               Util.bind(ctx, name, target);               
               if( trace )
                  log.trace("Bound to alias: "+bindingInfo.name());
            }
         }
      }
      // If this is the unsetKernelControllerContext callback, unbind the target
      else if( bindingInfo != null )
      {
         InitialContext ctx = new InitialContext(env);
         Util.unbind(ctx, bindingInfo.name());
         if( trace )
            log.trace("Unbound: "+bindingInfo.name());
         String[] aliases = bindingInfo.aliases();
         if( aliases != null )
         {
            for(String name : aliases)
            {
               Util.unbind(ctx, name);               
               if( trace )
                  log.trace("Unbound alias: "+bindingInfo.name());
            }
         }
      }
      else if ( trace )
      {
         log.trace("Ignoring null binding info");
      }

      return null;
   }

   public void install(ControllerContext context) throws Exception
   {
      JndiBinding bindingInfo = readJndiAnnotation(context);
      boolean trace = log.isTraceEnabled();
      if( trace )
         log.trace("Binding into JNDI: " + context.getName() + ", bindingInfo: " + bindingInfo);
      // If this is the setKernelControllerContext callback, bind the target into jndi
      InitialContext ctx = new InitialContext(env);
      Object target = context.getTarget();
      Util.bind(ctx, bindingInfo.name(), target);
      if( trace )
         log.trace("Bound to: "+bindingInfo.name());
      String[] aliases = bindingInfo.aliases();
      if( aliases != null )
      {
         for(String name : aliases)
         {
            Util.bind(ctx, name, target);               
            if( trace )
               log.trace("Bound to alias: "+bindingInfo.name());
         }
      }
   }
   
   public void uninstall(ControllerContext context) throws Exception
   {
      JndiBinding bindingInfo = readJndiAnnotation(context);
      boolean trace = log.isTraceEnabled();
      InitialContext ctx = new InitialContext(env);
      Util.unbind(ctx, bindingInfo.name());
      if( trace )
         log.trace("Unbound: "+bindingInfo.name());
      String[] aliases = bindingInfo.aliases();
      if( aliases != null )
      {
         for(String name : aliases)
         {
            Util.unbind(ctx, name);               
            if( trace )
               log.trace("Unbound alias: "+bindingInfo.name());
         }
      }
   }
   
   private JndiBinding readJndiAnnotation(ControllerContext context)
   {
      MetaData metaData = context.getScopeInfo().getMetaData();
      if (metaData != null)
         return metaData.getAnnotation(JndiBinding.class);
      return null;
   }
}
