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
package org.jboss.injection.injector.util;

import org.jboss.injection.injector.metadata.EnvironmentEntryType;

/**
 * EnvironmentEntryUtil
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class EnvironmentEntryUtil
{

   public static String getENCName(EnvironmentEntryType environmentEntry)
   {
      if (environmentEntry == null || environmentEntry.getName() == null)
      {
         return null;
      }
      String refName = environmentEntry.getName();
      return getENCName(refName);
   }

   public static String getENCName(String refName)
   {
      if (refName == null)
      {
         return null;
      }
      
      if (refName.isEmpty())
      {
         return refName;
      }
      // If it starts with some namespace (like java:comp, java:module, java:app or java:global)
      // then return as is
      if (refName.startsWith("java:comp/") || refName.startsWith("java:module/") || refName.startsWith("java:app/")
            || refName.startsWith("java:global/"))
      {
         return refName;
      }
      
      // the reference name *doesn't* start with any namespace. So prefix a "env/" before the
      // name and return
      return "env/" + refName;
   }
}
