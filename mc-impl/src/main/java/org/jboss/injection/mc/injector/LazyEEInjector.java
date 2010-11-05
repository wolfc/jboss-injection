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
package org.jboss.injection.mc.injector;

import javax.naming.Context;

import org.jboss.dependency.spi.ControllerState;
import org.jboss.injection.injector.EEInjector;
import org.jboss.injection.injector.metadata.JndiEnvironmentRefsGroup;
import org.jboss.injection.manager.spi.InjectionContext;
import org.jboss.injection.manager.spi.InjectionException;
import org.jboss.injection.manager.spi.Injector;
import org.jboss.switchboard.spi.Barrier;

/**
 * A MC dependent wrapper over {@link EEInjector} which can be created
 * without access to the java:comp {@link Context context} during creation.
 * <p>
 *  This {@link LazyEEInjector} delegates the injection to {@link EEInjector}.
 *  The delegate {@link EEInjector} is created when the SwitchBoard {@link Barrier}
 *  reaches a {@link ControllerState#INSTALLED installed} state and the {@link Barrier} has 
 *  fully populated the java:comp {@link Barrier#getContext() context}.
 * </p>
 *  
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class LazyEEInjector implements Injector
{

   /**
    * The JNDI environment entries
    */
   private JndiEnvironmentRefsGroup jndiEnvironment;
   
   /**
    * SwitchBoard {@link Barrier}
    */
   private Barrier barrier;
   
   /**
    * The delegate {@link EEInjector}
    */
   private EEInjector eeInjector;
   
   /**
    * Constructs a {@link LazyEEInjector}
    * @param jndiEnvironment
    */
   public LazyEEInjector(JndiEnvironmentRefsGroup jndiEnvironment)
   {
      this.jndiEnvironment = jndiEnvironment;
   }
   
   /**
    * Sets the SwitchBoard {@link Barrier}. 
    * <p>
    * {@link LazyEEInjector} is a MC construct and this {@link #setBarrier(Barrier)} will be
    * setup to be invoked through MC when the {@link Barrier} reaches installed state. Typically,
    * a MC deployer will deploy this {@link LazyEEInjector} as a MC bean and setup appropriate dependencies
    * so that this method is invoked when {@link Barrier} is installed and has fully populated the
    * java:comp {@link Context} 
    * </p>
    * @param barrier
    */
   public void setBarrier(Barrier barrier)
   {
      // make sure we don't have a barrier already set
      if (this.barrier != null)
      {
         throw new IllegalStateException("SwitchBoard Barrier is already set");
      }
      this.barrier = barrier;
      
      // Create the delegate EEInjector using the fully populated ENC context
      this.eeInjector = new EEInjector(this.barrier.getContext(), this.jndiEnvironment);
   }
   
   /**
    * Delegates the injection to the {@link EEInjector}
    * 
    * {@inheritDoc}
    */
   @Override
   public <T> void inject(InjectionContext<T> injectionContext) throws InjectionException
   {
      // make sure the EEInjector has been created
      if (this.eeInjector == null)
      {
         throw new IllegalStateException("EEInjector hasn't yet been set. Cannot do injections for context: " + injectionContext);
      }
      // let the delegate EEInjector handle the injector
      this.eeInjector.inject(injectionContext);
   }

}
