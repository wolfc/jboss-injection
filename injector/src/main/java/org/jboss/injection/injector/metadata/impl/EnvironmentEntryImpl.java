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

import java.util.Collection;

import org.jboss.injection.injector.metadata.EnvironmentEntryType;
import org.jboss.injection.injector.metadata.InjectionTargetType;

/**
 * A simple implementation of {@link EnvironmentEntryType} which has the 
 * environment entry name and the {@link InjectionTargetType injection targets}
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class EnvironmentEntryImpl implements EnvironmentEntryType
{

   /**
    * The ENC name of the environment entry
    */
   private String name;
   
   /**
    * Injection targets
    */
   private Collection<InjectionTargetType> injectionTargets;
   
   /**
    * Constructs a {@link EnvironmentEntryImpl}
    * @param name
    * @param injectionTargets
    */
   public EnvironmentEntryImpl(String name, Collection<InjectionTargetType> injectionTargets)
   {
      this.name = name;
      this.injectionTargets = injectionTargets;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public Collection<InjectionTargetType> getInjectionTargets()
   {
      return this.injectionTargets;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName()
   {
      return this.name;
   }

}
