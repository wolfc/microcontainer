/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.test.deployers.deployer.support;

import java.util.HashSet;

import org.jboss.beans.info.spi.PropertyInfo;
import org.jboss.managed.api.Fields;
import org.jboss.managed.spi.factory.ManagedPropertyConstraintsPopulator;
import org.jboss.managed.spi.factory.ManagedPropertyConstraintsPopulatorFactory;
import org.jboss.metatype.api.types.EnumMetaType;
import org.jboss.metatype.api.values.EnumValueSupport;
import org.jboss.metatype.api.values.MetaValue;

/**
 * Contraints populator for the TxSupportMetaData enum
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class AllowedTxSupportMetaData implements
      ManagedPropertyConstraintsPopulator,
      ManagedPropertyConstraintsPopulatorFactory
{
   private static EnumMetaType TYPE = new EnumMetaType(TxSupportMetaData.values());
   private static HashSet<MetaValue> values = new HashSet<MetaValue>();
   static
   {
      values.add(new EnumValueSupport(TYPE, TxSupportMetaData.NONE));
      values.add(new EnumValueSupport(TYPE, TxSupportMetaData.LOCAL));
      values.add(new EnumValueSupport(TYPE, TxSupportMetaData.XA));
   }

   public void populateManagedProperty(Class attachmentClass, PropertyInfo info, Fields fields)
   {
      fields.setField(Fields.LEGAL_VALUES, values);
   }

   public ManagedPropertyConstraintsPopulator newInstance()
   {
      return this;
   }

}
