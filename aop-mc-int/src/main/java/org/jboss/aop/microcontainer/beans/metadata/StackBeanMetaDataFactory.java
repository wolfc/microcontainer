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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jboss.aop.microcontainer.beans.Stack;
import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.ValueMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.xb.annotations.JBossXmlSchema;

/**
 * 
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
@JBossXmlSchema(namespace="urn:jboss:aop-beans:1.0", elementFormDefault=XmlNsForm.QUALIFIED)
@XmlRootElement(name="stack")
@XmlType(name="stackType", propOrder={"aliases", "annotations", "classLoader", "constructor", "properties", "interceptors", "create", "start", "depends", "demands", "supplies", "installs", "uninstalls", "installCallbacks", "uninstallCallbacks"})
public class StackBeanMetaDataFactory extends AspectManagerAwareBeanMetaDataFactory
{
   private static final long serialVersionUID = 1L;
   private List<BaseInterceptorData> interceptors = new ArrayList<BaseInterceptorData>();

   public StackBeanMetaDataFactory()
   {
   }
   
   @Override
   public List<BeanMetaData> getBeans()
   {
      ArrayList<BeanMetaData> result = new ArrayList<BeanMetaData>();

      //Create Stack
      BeanMetaDataBuilder stackBuilder = AOPBeanMetaDataBuilder.createBuilder(name, Stack.class.getName());
      stackBuilder.addPropertyMetaData("name", name);
      setAspectManagerProperty(stackBuilder);
      result.add(stackBuilder.getBeanMetaData());
      
      if (interceptors.size() > 0)
      {
         List<ValueMetaData> advices = stackBuilder.createList();
         int i = 0;
         for (BaseInterceptorData interceptor : interceptors)
         {
            String intName = name + "$" + i++; 
            BeanMetaDataBuilder interceptorBuilder = AOPBeanMetaDataBuilder.createBuilder(intName, interceptor.getBeanClassName());
            setAspectManagerProperty(interceptorBuilder);
            interceptorBuilder.addPropertyMetaData("forStack", Boolean.TRUE);
            
            if (interceptor instanceof AdviceOrInterceptorData)
            {
               ValueMetaData injectAspect = interceptorBuilder.createInject(interceptor.getRefName());
               interceptorBuilder.addPropertyMetaData("aspect", injectAspect);
               if (((AdviceOrInterceptorData)interceptor).getAdviceMethod() != null)
               {
                  interceptorBuilder.addPropertyMetaData("aspectMethod", ((AdviceOrInterceptorData)interceptor).getAdviceMethod());
               }
               interceptorBuilder.addPropertyMetaData("type", ((AdviceOrInterceptorData)interceptor).getType());
            }
            else
            {
               ValueMetaData injectStack = interceptorBuilder.createInject(interceptor.getRefName());
               interceptorBuilder.addPropertyMetaData("stack", injectStack);
            }
            result.add(interceptorBuilder.getBeanMetaData());
            ValueMetaData injectAdvice = stackBuilder.createInject(intName);
            advices.add(injectAdvice);
         }         
         stackBuilder.addPropertyMetaData("advices", advices);
      }
      
      return result;
   }
   
   public void addInterceptor(BaseInterceptorData interceptorData)
   {
      interceptors.add(interceptorData);
   }

   @XmlElements
   ({
      @XmlElement(name="advice", type=AdviceData.class),
      @XmlElement(name="around", type=AdviceData.class),
      @XmlElement(name="before", type=BeforeAdviceData.class),
      @XmlElement(name="after", type=AfterAdviceData.class),
      @XmlElement(name="throwing", type=ThrowingAdviceData.class),
      @XmlElement(name="finally", type=FinallyAdviceData.class),
      @XmlElement(name="interceptor-ref", type=InterceptorRefData.class),
      @XmlElement(name="stack-ref", type=StackRefData.class)
   })
   public List<BaseInterceptorData> getInterceptors()
   {
      return interceptors;
   }

   public void setInterceptors(List<BaseInterceptorData> interceptors)
   {
      this.interceptors = interceptors;
   }
   
}
