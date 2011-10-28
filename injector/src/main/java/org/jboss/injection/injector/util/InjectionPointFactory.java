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
      {
         return name;
      }
      return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
   }

   /**
    * Creates and return the correct {@link InjectionPoint} for the specified target class
    * and property. The <code>paramType</code> specifies the param type of the injection point.
    *
    * @param targetClass The class in which the injection point will be looked for
    * @param property    The property name which will be used to find the correct method or field for injection
    * @param paramType   The type of the field/method param which is being injected
    * @return
    * @throws NoSuchPropertyException If no injection point can be found for the passed property
    */
   public static InjectionPoint create(Class<?> targetClass, String property, Class<?> paramType) throws NoSuchPropertyException
   {
      String writeMethodName = "set" + capitalize(property);
      Method writeMethod = findWriteMethod(targetClass, writeMethodName, paramType);
      if (writeMethod != null)
      {
         return new MethodInjectionPoint(writeMethod);
      }

      // TODO: this is a bug in JBMETA where we can get a property name as a method name
      writeMethod = findWriteMethod(targetClass, property, paramType);
      if (writeMethod != null)
      {
         return new MethodInjectionPoint(writeMethod);
      }

      try
      {
         Field field = targetClass.getDeclaredField(property);
         return new FieldInjectionPoint(field);
      }
      catch (NoSuchFieldException e)
      {
         throw new NoSuchPropertyException("No such property " + property + " on " + targetClass, e);
      }
   }

   /**
    * Finds and returns the correct setter method. If no method is found, then returns null.
    *
    * @param cls
    * @param methodName
    * @param paramType
    * @return
    */
   private static Method findWriteMethod(Class<?> cls, String methodName, Class<?> paramType)
   {
      for (Method m : cls.getDeclaredMethods())
      {
         // Find a method which has the same name as the passed methodName and which takes one parameter
         // of the expected type and with a void return type
         if (m.getName().equals(methodName) && m.getReturnType() == Void.TYPE && m.getParameterTypes().length == 1)
         {
            // Consider the current method as a match, because the expected param type isn't specified
            if (paramType == null)
            {
               return m;
            }
            // check param type
            Class<?>[] paramTypes = m.getParameterTypes();
            if (paramTypes[0].isAssignableFrom(paramType))
            {
               return m;
            }
            // Check if auto-boxing is applicable for the expected and actual param types
            if (paramType.isPrimitive() && isCompatibleForBoxedType(paramType, paramTypes[0]))
            {
               return m;
            }
            if (paramTypes[0].isPrimitive() && isCompatibleForBoxedType(paramTypes[0], paramType))
            {
               return m;
            }
         }
      }
      // if the method isn't found recurse upward
      if(cls.getSuperclass() != null) {
         return findWriteMethod(cls.getSuperclass(), methodName, paramType);
      }
      return null;
   }

   /**
    * Returns true if the passed <code>wrapperType</code> is a wrapper class for the <code>primitiveType</code>
    * (ex: java.lang.Integer for the primitive type int). Else returns false.
    *
    * @param primitiveType
    * @param wrapperType
    * @return
    */
   private static boolean isCompatibleForBoxedType(Class<?> primitiveType, Class<?> wrapperType)
   {
      if (primitiveType.equals(Integer.TYPE))
      {
         return wrapperType.equals(Integer.class);
      }
      if (primitiveType.equals(Float.TYPE))
      {
         return wrapperType.equals(Float.class);
      }
      if (primitiveType.equals(Double.TYPE))
      {
         return wrapperType.equals(Double.class);
      }
      if (primitiveType.equals(Byte.TYPE))
      {
         return wrapperType.equals(Byte.class);
      }
      if (primitiveType.equals(Character.TYPE))
      {
         return wrapperType.equals(Character.class);
      }
      if (primitiveType.equals(Boolean.TYPE))
      {
         return wrapperType.equals(Boolean.class);
      }
      if (primitiveType.equals(Long.TYPE))
      {
         return wrapperType.equals(Long.class);
      }
      if (primitiveType.equals(Short.TYPE))
      {
         return wrapperType.equals(Short.class);
      }

      return false;
   }
}
