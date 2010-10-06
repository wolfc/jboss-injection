/*
 * JBoss, Home of Professional Open Source
 * Copyright (c) 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. See the copyright.txt in the
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

package org.jboss.injection.manager.core;

import org.jboss.injection.manager.spi.InjectionContext;
import org.jboss.injection.manager.spi.InjectionException;
import org.jboss.injection.manager.spi.Injector;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
* @author Marius Bogoevici
*/
public class DefaultInjectionContext<T> implements InjectionContext<T>
{
   Queue<Injector> injectors;
   private final T instance;
   private Class<? super T> clazz;

   public DefaultInjectionContext(T instance, Class<? super T> clazz, List<Injector> injectors)
   {
      this.instance = instance;
      this.clazz = clazz;
      this.injectors = new LinkedList<Injector>(injectors);
   }



   public void proceed() throws InjectionException
   {
      if(injectors.size() > 0)
      {
         injectors.remove().inject(this);
      }
   }

   public T getInjectionTarget()
   {
      return instance;
   }

   public Class<? super T> getInjectedType()
   {
      return clazz;
   }
}
