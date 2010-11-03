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
package org.jboss.injection.injector.metadata;

import java.util.Collection;

/**
 * The environment entry type is used to declare an application's
 * environment entry (see JavaEE 6 EE.5.2.1).
 *
 * Note: not to be mistaken with simple environment entries (Java
 * EE 6 EE.5.4).
 *
 * It also includes optional elements to define injection of
 * the named resource into fields or JavaBeans properties.
 * 
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public interface EnvironmentEntryType
{
   
   /**
    * The name is a JNDI name relative to the java:comp/env
    * context. The name must be unique within a Deployment
    * Component.
    * @return the name of a Deployment Component's environment entry
    */
   String getName();

   Collection<InjectionTargetType> getInjectionTargets();
}
