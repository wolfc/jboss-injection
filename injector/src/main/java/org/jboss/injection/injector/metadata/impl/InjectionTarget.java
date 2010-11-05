/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.injection.injector.metadata.impl;

import org.jboss.injection.injector.metadata.InjectionTargetType;

/**
 * A simple implementation of {@link InjectionTargetType} which holds
 * the injection target class and the injection target field/method name   
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class InjectionTarget implements InjectionTargetType
{

   /**
    * Injection target class
    */
   private Class<?> injectionTargetClass;

   /**
    * The target method/field 
    */
   private String targetName;

   /**
    * Constructs a {@link InjectionTarget} 
    *  
    * @param injectionTargetClass The injection target class
    * @param targetName The target field/method
    */
   public InjectionTarget(Class<?> injectionTargetClass, String targetName)
   {
      this.injectionTargetClass = injectionTargetClass;
      this.targetName = targetName;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Class<?> getInjectionTargetClass()
   {
      return this.injectionTargetClass;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getInjectionTargetName()
   {
      return this.targetName;
   }

}
