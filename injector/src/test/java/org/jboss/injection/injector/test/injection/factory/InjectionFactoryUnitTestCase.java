/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
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

package org.jboss.injection.injector.test.injection.factory;

import org.jboss.injection.injector.util.FieldInjectionPoint;
import org.jboss.injection.injector.util.InjectionPoint;
import org.jboss.injection.injector.util.InjectionPointFactory;
import org.jboss.injection.injector.util.MethodInjectionPoint;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link InjectionPointFactory}
 * <p/>
 * Author : Jaikiran Pai
 */
public class InjectionFactoryUnitTestCase
{

   /**
    * Test that the {@link InjectionPointFactory#create(Class, String, Class)} finds the correct
    * injection point
    *
    * @throws Exception
    */
   @Test
   public void testFindCorrectInjectionPoint() throws Exception
   {
      String stringValue = "someStringValue";
      InjectionPoint injectionPoint = InjectionPointFactory.create(SimpleClass.class, "something", stringValue.getClass());

      Assert.assertNotNull("No injection point found", injectionPoint);
      Assert.assertTrue("Unexpected injection point found: " + injectionPoint.getClass(), injectionPoint instanceof MethodInjectionPoint);

      int somePrimitiveInt = 2;
      InjectionPoint someIntInjectionPoint = InjectionPointFactory.create(SimpleClass.class, "someInt", Integer.TYPE);

      Assert.assertNotNull("No injection point found", someIntInjectionPoint);
      Assert.assertTrue("Unexpected injection point found: " + someIntInjectionPoint.getClass(), someIntInjectionPoint instanceof MethodInjectionPoint);

      Object em = new Object();
      InjectionPoint emInjectionPoint = InjectionPointFactory.create(SimpleClass.class, "em", em.getClass());

      Assert.assertNotNull("No injection point found", emInjectionPoint);
      Assert.assertTrue("Unexpected injection point found: " + emInjectionPoint.getClass(), emInjectionPoint instanceof FieldInjectionPoint);


   }
}
