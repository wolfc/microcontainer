/*
 * JBoss, Home of Professional Open Source
 * Copyright 2007, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.managed.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.managed.api.ManagedOperation.Impact;
import org.jboss.managed.api.annotation.ManagementParameter.NULL_CONSTRAINTS;
import org.jboss.managed.spi.factory.ManagedParameterConstraintsPopulatorFactory;

/**
 * An annotation for describing a ManagedOperation
 * 
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ManagementOperation
{
   /** The name of the operation */
   String name() default AnnotationDefaults.EMPTY_STRING;
   /** The parameters of the operation */
   ManagementParameter[] params() default {};

   /** The description */
   String description() default AnnotationDefaults.EMPTY_STRING;
   /** The side-effect impact of invoking the operation */
   Impact impact() default Impact.Unknown;

   /** The parameter constraints, allowed values populator factory */
   Class<? extends ManagedParameterConstraintsPopulatorFactory> constraintsFactory()
      default NULL_CONSTRAINTS.class;

}
