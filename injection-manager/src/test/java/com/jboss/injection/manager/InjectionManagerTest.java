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

package com.jboss.injection.manager;

import org.jboss.injection.manager.core.DefaultInjectionManager;
import org.jboss.injection.manager.spi.InjectionException;
import org.jboss.injection.manager.spi.InjectionManager;
import org.junit.Assert;
import org.junit.Test;

public class InjectionManagerTest
{
   @Test
   public void testInjectionManager()
   {
      InjectionManager injectionManager = new DefaultInjectionManager();

      DummyInjector1 injector1 = new DummyInjector1();
      DummyInjector2 injector2 = new DummyInjector2();

      DummyInjector1.invocationCount = 0;
      DummyInjector2.invocationCount = 0;

      injectionManager.addInjector(injector1);
      injectionManager.addInjector(injector2);


      DummyType dummyInstance = new DummyType();

      injectionManager.inject(dummyInstance, DummyType.class);

      Assert.assertEquals(1, DummyInjector1.invocationCount);
      Assert.assertEquals(1, DummyInjector2.invocationCount);
   }

   @Test
   public void tryRecoverableInjectors()
   {
      InjectionManager injectionManager = new DefaultInjectionManager();

      DummyInjector1 injector1 = new DummyInjector1();
      DummyInjector2 injector2 = new DummyInjector2();
      RetryInjector retryInjector = new RetryInjector(3);
      FailingRecoverableInjector failingRecoverableInjector = new FailingRecoverableInjector(2);

      DummyInjector1.invocationCount = 0;
      DummyInjector2.invocationCount = 0;

      injectionManager.addInjector(retryInjector);
      injectionManager.addInjector(injector1);
      injectionManager.addInjector(injector2);
      injectionManager.addInjector(failingRecoverableInjector);

      DummyType dummyInstance = new DummyType();

      injectionManager.inject(dummyInstance, DummyType.class);

      Assert.assertEquals(3, DummyInjector1.invocationCount);
      Assert.assertEquals(3, DummyInjector2.invocationCount);
      Assert.assertEquals(2, retryInjector.getInvocationCount());
      Assert.assertEquals(3, failingRecoverableInjector.getTries());

    }

   @Test(expected = InjectionException.class)
   public void tryInjectorDoesNotRecover()
   {
      InjectionManager injectionManager = new DefaultInjectionManager();

      DummyInjector1 injector1 = new DummyInjector1();
      DummyInjector2 injector2 = new DummyInjector2();
      RetryInjector retryInjector = new RetryInjector(3);
      FailingRecoverableInjector failingRecoverableInjector = new FailingRecoverableInjector(4);

      DummyInjector1.invocationCount = 0;
      DummyInjector2.invocationCount = 0;

      injectionManager.addInjector(retryInjector);
      injectionManager.addInjector(injector1);
      injectionManager.addInjector(injector2);
      injectionManager.addInjector(failingRecoverableInjector);

      DummyType dummyInstance = new DummyType();

      injectionManager.inject(dummyInstance, DummyType.class);

      Assert.assertEquals(3, DummyInjector1.invocationCount);
      Assert.assertEquals(3, DummyInjector2.invocationCount);
      Assert.assertEquals(2, retryInjector.getInvocationCount());
      Assert.assertEquals(3, failingRecoverableInjector.getTries());

    }
}
