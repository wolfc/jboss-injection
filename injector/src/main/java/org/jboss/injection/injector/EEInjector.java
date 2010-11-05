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
package org.jboss.injection.injector;

import org.jboss.injection.injector.metadata.EnvironmentEntryType;
import org.jboss.injection.injector.metadata.InjectionTargetType;
import org.jboss.injection.injector.metadata.JndiEnvironmentRefsGroup;
import org.jboss.injection.injector.util.InjectionPoint;
import org.jboss.injection.injector.util.InjectionPointFactory;
import org.jboss.injection.injector.util.NoSuchPropertyException;
import org.jboss.injection.manager.spi.InjectionContext;
import org.jboss.injection.manager.spi.InjectionException;
import org.jboss.injection.manager.spi.Injector;

import javax.naming.Context;
import javax.naming.NamingException;

/**
 * Inject entries from the naming environment into the application component,
 * as specified by the environment parameter.
 *
 * Java EE 6 EE.5.3.4 bullet 4
 * 
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class EEInjector implements Injector
{
   private Context context;
   private JndiEnvironmentRefsGroup environment;

   /**
    * @param context the naming environment of an application component
    * @param environment the representation of the application component descriptor and annotations
    */
   public EEInjector(Context context, JndiEnvironmentRefsGroup environment)
   {
      this.context = context;
      this.environment = environment;
   }

   /**
    * @return the context from which injection takes place
    */
   protected Context getContext()
   {
      return context;
   }

   protected JndiEnvironmentRefsGroup getEnvironment()
   {
      return environment;
   }

   public <T> void  inject(InjectionContext<T> injectionContext)
   {
      try
      {
         this.inject(injectionContext.getInjectedType(), injectionContext.getInjectionTarget());
         injectionContext.proceed();
      }
      catch (NamingException e)
      {
         throw new InjectionException(e);
      }
   }

   /**
    * Inject the given instance using the resources targeted at the
    * instances class.
    *
    * Before calling this method the associated context must have the
    * environment entries specified. The entries must be ready to be
    * looked up.
    *
    * @param instance the instance to inject
    */
   public void inject(Object instance) throws NamingException
   {
      inject(instance.getClass(), instance);
   }

   /**
    * Inject the given instance using the resources targeted at the
    * given class.
    *
    * The instance must be an instance of the given class.
    *
    * Before calling this method the associated context must have the
    * environment entries specified. The entries must be ready to be
    * looked up.
    *
    * @param cls the class to which the resources belong
    * @param instance the instance to inject
    */
   private void inject(Class<?> cls, Object instance) throws NamingException
   {
      // TODO: optimize for speed
      try
      {
         for(EnvironmentEntryType entry : environment.getEntries())
         {
            String name = entry.getName();
            Object value = null;
            for(InjectionTargetType target : entry.getInjectionTargets())
            {
               if(target.getInjectionTargetClass().isAssignableFrom(cls))
               {
                  if(value == null)
                     value = getContext().lookup(name);
                  inject(value, target.getInjectionTargetClass(), instance, target.getInjectionTargetName());
               }
            }
         }
      }
      catch(NoSuchPropertyException e)
      {
         // TODO: should have been checked much earlier
         throw new RuntimeException(e);
      }
   }

   private void inject(Object value, Class<?> cls, Object instance, String name) throws NoSuchPropertyException
   {
      InjectionPoint p = InjectionPointFactory.create(cls, name);
      p.set(instance, value);
   }
}
