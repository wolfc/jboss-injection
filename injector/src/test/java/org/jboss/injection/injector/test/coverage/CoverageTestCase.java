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
package org.jboss.injection.injector.test.coverage;

import org.jboss.injection.injector.EEInjector;
import org.jboss.injection.injector.metadata.EnvironmentEntryType;
import org.jboss.injection.injector.metadata.InjectionTargetType;
import org.jboss.injection.injector.metadata.JndiEnvironmentRefsGroup;
import org.jboss.reloaded.naming.service.NameSpaces;
import org.jboss.util.naming.Util;
import org.jnp.server.SingletonNamingServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class CoverageTestCase
{
   private static InitialContext iniCtx;
   private static SingletonNamingServer server;
   private static NameSpaces ns;

   @AfterClass
   public static void afterClass() throws NamingException
   {
      iniCtx.close();

      //ns.stop();

      server.destroy();
   }

   @BeforeClass
   public static void beforeClass() throws NamingException
   {
      server = new SingletonNamingServer();

      //ns = new NameSpaces();
      //ns.start();

      iniCtx = new InitialContext();

      //javaGlobal = (Context) iniCtx.lookup("java:global");
   }
   
   @Test
   public void test1() throws NamingException
   {
      Util.rebind(iniCtx, "java:comp/env/message", "Hi");

      Context context = (Context) iniCtx.lookup("java:comp/env");

      JndiEnvironmentRefsGroup environment = mock(JndiEnvironmentRefsGroup.class);

      InjectionTargetType injectionTarget = mock(InjectionTargetType.class);
      when(injectionTarget.getInjectionTargetClass()).thenReturn((Class) GreeterBean.class);
      when(injectionTarget.getInjectionTargetName()).thenReturn("value");
      
      Collection<InjectionTargetType> injectionTargets = asList(injectionTarget);
      
      EnvironmentEntryType entry = mock(EnvironmentEntryType.class);
      when(entry.getName()).thenReturn("message");
      when(entry.getInjectionTargets()).thenReturn(injectionTargets);
      
      Collection<EnvironmentEntryType> entries = asList(entry);
      when(environment.getEntries()).thenReturn(entries);

      EEInjector injector = new EEInjector(context, environment);

      GreeterBean instance = new GreeterBean();

      injector.inject(instance);

      String result = instance.greet("test1");

      assertEquals("Hi test1", result);
   }
}
