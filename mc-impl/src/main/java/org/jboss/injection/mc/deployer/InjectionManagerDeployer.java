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
package org.jboss.injection.mc.deployer;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.injection.manager.core.DefaultInjectionManager;
import org.jboss.injection.manager.spi.InjectionManager;
import org.jboss.logging.Logger;

/**
 * {@link InjectionManagerDeployer} will attach a new {@link InjectionManager}
 * instance to each {@link DeploymentUnit} during the {@link DeploymentStages#REAL REAL}
 * stage.
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class InjectionManagerDeployer extends AbstractDeployer
{

   /**
    * Logger
    */
   private static Logger logger = Logger.getLogger(InjectionManagerDeployer.class);
   
   /**
    * Setup the deployer
    */
   public InjectionManagerDeployer()
   {
      // we run in REAL stage because we want to process
      // even component deployment unit (which are created during REAL stage)
      this.setStage(DeploymentStages.REAL);
      // we want to attach InjectionManager to both non-component and
      // component deployment units
      this.setWantComponents(true);
      // we attach InjectionManager
      this.addOutput(InjectionManager.class);
   }

   /**
    * @param unit The deployment unit being deployed
    */
   @Override
   public void deploy(DeploymentUnit unit) throws DeploymentException
   {
      // create and attach a new InjectionManager for the unit
      InjectionManager injectionManager = new DefaultInjectionManager();
      unit.addAttachment(InjectionManager.class, injectionManager);
      if (logger.isTraceEnabled())
      {
         logger.trace("Added InjectionManager: " + injectionManager + " to unit " + unit);
      }
   }
   
}
