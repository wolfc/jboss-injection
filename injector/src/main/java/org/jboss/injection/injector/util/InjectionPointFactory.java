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
package org.jboss.injection.injector.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static java.util.Locale.ENGLISH;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class InjectionPointFactory
{
   static String capitalize(String name)
   {
      if (name == null || name.length() == 0)
         return name;
      return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
   }

   public static InjectionPoint create(Class<?> cls, String property) throws NoSuchPropertyException
   {
      String writeMethodName = "set" + capitalize(property);
      Method writeMethod = findWriteMethod(cls, writeMethodName);
      if(writeMethod != null)
         return new MethodInjectionPoint(writeMethod);

      // TODO: this is a bug in JBMETA where we can get a property name as a method name
      writeMethod = findWriteMethod(cls, property);
      if(writeMethod != null)
         return new MethodInjectionPoint(writeMethod);
      
      try
      {
         Field field = cls.getDeclaredField(property);
         return new FieldInjectionPoint(field);
      }
      catch(NoSuchFieldException e)
      {
         throw new NoSuchPropertyException("No such property " + property + " on " + cls, e);
      }
   }

   private static Method findWriteMethod(Class<?> cls, String methodName)
   {
      for(Method m : cls.getDeclaredMethods())
      {
         if(m.getName().equals(methodName))
            return m;
      }
      return null;
   }
}
