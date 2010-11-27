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
package org.jboss.injection.injector.test.wicked;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Proxy;
import java.util.Collection;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.ejb3.sis.Interceptor;
import org.jboss.ejb3.sis.InterceptorAssembly;
import org.jboss.ejb3.sis.reflect.InterceptorInvocationHandler;
import org.jboss.injection.injector.EEInjector;
import org.jboss.injection.injector.metadata.EnvironmentEntryType;
import org.jboss.injection.injector.metadata.InjectionTargetType;
import org.jboss.injection.injector.metadata.JndiEnvironmentRefsGroup;
import org.jboss.injection.injector.test.common.ObjectInvocationHandler;
import org.jboss.injection.manager.core.DefaultInjectionManager;
import org.jboss.injection.manager.spi.InjectionManager;
import org.jboss.injection.manager.spi.Injector;
import org.jboss.util.naming.Util;
import org.jnp.server.SingletonNamingServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Just to ease my mind I want to make sure a SLSB with an Interceptor
 * which both share a super class work.
 * 
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class WickedTestCase
{
   private static SingletonNamingServer server;
   private static InitialContext iniCtx;

   @AfterClass
   public static void afterClass() throws NamingException
   {
      iniCtx.close();

      server.destroy();
   }

   @BeforeClass
   public static void beforeClass() throws NamingException
   {
      server = new SingletonNamingServer();

      iniCtx = new InitialContext();
   }

   private static EnvironmentEntryType entry(String name, Collection<InjectionTargetType> injectionTargets)
   {
      EnvironmentEntryType entry = mock(EnvironmentEntryType.class);
      when(entry.getName()).thenReturn(name);
      when(entry.getInjectionTargets()).thenReturn(injectionTargets);
      return entry;
   }

   private static InjectionTargetType injectionTarget(Class<?> cls, String name)
   {
      InjectionTargetType injectionTarget = mock(InjectionTargetType.class);
      when(injectionTarget.getInjectionTargetClass()).thenReturn((Class) cls);
      when(injectionTarget.getInjectionTargetName()).thenReturn(name);
      return injectionTarget;
   }

   private static InterceptorAssembly interceptors(Interceptor... interceptors)
   {
      return new InterceptorAssembly(interceptors);
   }

   @Test
   public void testWicked() throws Exception
   {
      Util.rebind(iniCtx, "java:comp/env/wicked", "Wicked");
      Util.rebind(iniCtx, "java:comp/env/" + ValueStatelessBean.class.getName() + "/value", "Bean");

      JndiEnvironmentRefsGroup environment = mock(JndiEnvironmentRefsGroup.class);

      EnvironmentEntryType entry = entry("env/wicked", asList(injectionTarget(AbstractWicked.class, "value")));

      // EE.5.2.5 defines the default name of a resource
      EnvironmentEntryType entry2 = entry("env/" + ValueStatelessBean.class.getName() + "/value",
              asList(injectionTarget(ValueStatelessBean.class, "value")));
      
      Collection<EnvironmentEntryType> entries = asList(entry, entry2);
      when(environment.getEntries()).thenReturn(entries);

      Injector injector = new EEInjector(environment);

      InjectionManager injectionManager = new DefaultInjectionManager();
      injectionManager.addInjector(injector);
      ValueStatelessBean bean = new ValueStatelessBean();
      injectionManager.inject(bean);
      MyInterceptor interceptor = new MyInterceptor();
      injectionManager.inject(interceptor);

      // the bean has a bean method interceptor
      InterceptorInvocationHandler handler = new InterceptorInvocationHandler(new ObjectInvocationHandler(bean), interceptors(interceptor, bean));
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      ValueLocal proxy = (ValueLocal) Proxy.newProxyInstance(loader, new Class<?>[] { ValueLocal.class }, handler);

      String expected = MyInterceptor.class.getName() + ":Wicked:" + ValueStatelessBean.class.getName() + ":Wicked:Bean";
      assertEquals(expected, proxy.getValue());
   }
}
