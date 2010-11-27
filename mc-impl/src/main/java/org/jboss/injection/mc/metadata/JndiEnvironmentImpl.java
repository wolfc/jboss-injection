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
package org.jboss.injection.mc.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.jboss.injection.injector.metadata.EnvironmentEntryType;
import org.jboss.injection.injector.metadata.InjectionTargetType;
import org.jboss.injection.injector.metadata.JndiEnvironmentRefsGroup;
import org.jboss.injection.injector.metadata.impl.EnvironmentEntryImpl;
import org.jboss.injection.injector.metadata.impl.InjectionTarget;
import org.jboss.injection.injector.util.EnvironmentEntryUtil;
import org.jboss.metadata.javaee.spec.AnnotatedEJBReferenceMetaData;
import org.jboss.metadata.javaee.spec.AnnotatedEJBReferencesMetaData;
import org.jboss.metadata.javaee.spec.EJBLocalReferenceMetaData;
import org.jboss.metadata.javaee.spec.EJBLocalReferencesMetaData;
import org.jboss.metadata.javaee.spec.EJBReferenceMetaData;
import org.jboss.metadata.javaee.spec.EJBReferencesMetaData;
import org.jboss.metadata.javaee.spec.Environment;
import org.jboss.metadata.javaee.spec.EnvironmentEntriesMetaData;
import org.jboss.metadata.javaee.spec.EnvironmentEntryMetaData;
import org.jboss.metadata.javaee.spec.MessageDestinationReferenceMetaData;
import org.jboss.metadata.javaee.spec.MessageDestinationReferencesMetaData;
import org.jboss.metadata.javaee.spec.PersistenceContextReferenceMetaData;
import org.jboss.metadata.javaee.spec.PersistenceContextReferencesMetaData;
import org.jboss.metadata.javaee.spec.PersistenceUnitReferenceMetaData;
import org.jboss.metadata.javaee.spec.PersistenceUnitReferencesMetaData;
import org.jboss.metadata.javaee.spec.ResourceEnvironmentReferenceMetaData;
import org.jboss.metadata.javaee.spec.ResourceEnvironmentReferencesMetaData;
import org.jboss.metadata.javaee.spec.ResourceInjectionTargetMetaData;
import org.jboss.metadata.javaee.spec.ResourceReferenceMetaData;
import org.jboss.metadata.javaee.spec.ResourceReferencesMetaData;
import org.jboss.metadata.javaee.spec.ServiceReferenceMetaData;
import org.jboss.metadata.javaee.spec.ServiceReferencesMetaData;


/**
 * A implementation of {@link JndiEnvironmentRefsGroup}, which works off 
 * JBMETA based {@link Environment}
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class JndiEnvironmentImpl implements JndiEnvironmentRefsGroup
{

   /**
    * JBMETA based delegate metadata
    */
   private Environment delegate;

   /**
    * Environment entries
    */
   private Collection<EnvironmentEntryType> envEntries;
   
   /**
    * The classloader to use while processing the metadata
    */
   private ClassLoader classLoader;

   
   /**
    * Constructs a {@link JndiEnvironmentImpl} based on the JBMETA {@link Environment}
    * 
    * @param environment
    * @param classloader
    */
   public JndiEnvironmentImpl(Environment environment, ClassLoader classloader)
   {
      if (environment == null)
      {
         throw new IllegalArgumentException("Cannot create " + this.getClass().getName() + " from a null " + Environment.class);
      }
      this.delegate = environment;
      this.classLoader = classloader;
      // convert the JBMETA environment entries to jboss-injection
      // specific metadata
      this.initEnvironmentReferences();
   }
   
   /**
    * Returns the {@link EnvironmentEntryType environment entries}
    * {@inheritDoc}
    */
   @Override
   public Collection<EnvironmentEntryType> getEntries()
   {
      return this.envEntries;
   }

   /**
    * Process the JBMETA based {@link Environment} to convert it to 
    * jboss-injection specific metadata
    */
   private void initEnvironmentReferences()
   {
      this.envEntries = new ArrayList<EnvironmentEntryType>();

      // simple env entry references
      EnvironmentEntriesMetaData envEntries = this.delegate.getEnvironmentEntries();
      if (envEntries != null)
      {
         for (EnvironmentEntryMetaData envEntry : envEntries)
         {
            // According to Java EE6 Spec, section EE.5.4.1.3,
            // The container must only inject a value for this resource if the deployer has specified a 
            // value to override the default value. The env-entry-value element in the deployment descriptor 
            // is optional when an injection target is specified. If the element is *not* specified, no value 
            // will be injected. In addition, if the element is not specified, the named resource is not 
            // initialized in the naming context; explicit lookups of the named resource will fail.
            
            // so create an EnvironmentEntryImpl only if env-entry-value or a lookup-name is specified
            if (envEntry.getValue() != null || envEntry.getLookupName() != null)
            {
               Collection<InjectionTargetType> injectionTargets = this.convertInjectionTargets(envEntry.getInjectionTargets());
               if (!injectionTargets.isEmpty())
               {
                  String encName = EnvironmentEntryUtil.getENCName(envEntry.getName());
                  this.envEntries.add(new EnvironmentEntryImpl(encName, injectionTargets));
               }
            }
         }
      }
      
      // annotated ejb references
      AnnotatedEJBReferencesMetaData annotatedEjbRefs = this.delegate.getAnnotatedEjbReferences();
      if (annotatedEjbRefs != null)
      {
         for (AnnotatedEJBReferenceMetaData annotatedEjbRef : annotatedEjbRefs)
         {
            Collection<InjectionTargetType> injectionTargets = this.convertInjectionTargets(annotatedEjbRef.getInjectionTargets());
            if (!injectionTargets.isEmpty())
            {
               String encName = EnvironmentEntryUtil.getENCName(annotatedEjbRef.getName());
               this.envEntries.add(new EnvironmentEntryImpl(encName, injectionTargets));
            }
         }
      }

      // ejb local references
      EJBLocalReferencesMetaData ejbLocalRefs = this.delegate.getEjbLocalReferences();
      if (ejbLocalRefs != null)
      {
         for (EJBLocalReferenceMetaData ejbLocalRef : ejbLocalRefs)
         {
            Collection<InjectionTargetType> injectionTargets = this.convertInjectionTargets(ejbLocalRef.getInjectionTargets());
            if (!injectionTargets.isEmpty())
            {
               String encName = EnvironmentEntryUtil.getENCName(ejbLocalRef.getName());
               this.envEntries.add(new EnvironmentEntryImpl(encName, injectionTargets));
            }
         }
      }

      // ejb reference
      EJBReferencesMetaData ejbRefs = this.delegate.getEjbReferences();
      if (ejbRefs != null)
      {
         for (EJBReferenceMetaData ejbRef : ejbRefs)
         {
            Collection<InjectionTargetType> injectionTargets = this.convertInjectionTargets(ejbRef.getInjectionTargets());
            if (!injectionTargets.isEmpty())
            {
               String encName = EnvironmentEntryUtil.getENCName(ejbRef.getName());
               this.envEntries.add(new EnvironmentEntryImpl(encName, injectionTargets));
            }
         }
      }

      // persistence unit reference
      PersistenceUnitReferencesMetaData persistenceUnitRefs = this.delegate.getPersistenceUnitRefs();
      if (persistenceUnitRefs != null)
      {
         for (PersistenceUnitReferenceMetaData persistenceUnitRef : persistenceUnitRefs)
         {
            Collection<InjectionTargetType> injectionTargets = this.convertInjectionTargets(persistenceUnitRef.getInjectionTargets());
            if (!injectionTargets.isEmpty())
            {
               String encName = EnvironmentEntryUtil.getENCName(persistenceUnitRef.getName());
               this.envEntries.add(new EnvironmentEntryImpl(encName, injectionTargets));
            }
         }
      }

      // persistence context reference
      PersistenceContextReferencesMetaData persistenceCtxRefs = this.delegate.getPersistenceContextRefs();
      if (persistenceCtxRefs != null)
      {
         for (PersistenceContextReferenceMetaData persistenceCtxRef : persistenceCtxRefs)
         {
            Collection<InjectionTargetType> injectionTargets = this.convertInjectionTargets(persistenceCtxRef.getInjectionTargets());
            if (!injectionTargets.isEmpty())
            {
               String encName = EnvironmentEntryUtil.getENCName(persistenceCtxRef.getName());
               this.envEntries.add(new EnvironmentEntryImpl(encName, injectionTargets));
            }
         }
      }

      // resource env reference
      ResourceEnvironmentReferencesMetaData resourceEnvRefs = this.delegate.getResourceEnvironmentReferences();
      if (resourceEnvRefs != null)
      {
         for (ResourceEnvironmentReferenceMetaData resourceEnvRef : resourceEnvRefs)
         {
            Collection<InjectionTargetType> injectionTargets = this.convertInjectionTargets(resourceEnvRef.getInjectionTargets());
            if (!injectionTargets.isEmpty())
            {
               String encName = EnvironmentEntryUtil.getENCName(resourceEnvRef.getName());
               this.envEntries.add(new EnvironmentEntryImpl(encName, injectionTargets));
            }
         }
      }
      
      // resource reference
      ResourceReferencesMetaData resourceRefs = this.delegate.getResourceReferences();
      if (resourceRefs != null)
      {
         for (ResourceReferenceMetaData resourceRef : resourceRefs)
         {
            Collection<InjectionTargetType> injectionTargets = this.convertInjectionTargets(resourceRef.getInjectionTargets());
            if (!injectionTargets.isEmpty())
            {
               String encName = EnvironmentEntryUtil.getENCName(resourceRef.getName());
               this.envEntries.add(new EnvironmentEntryImpl(encName, injectionTargets));
            }
         }
      }
      
      // message destination references
      MessageDestinationReferencesMetaData messageDestRefs = this.delegate.getMessageDestinationReferences();
      if (messageDestRefs != null)
      {
         for (MessageDestinationReferenceMetaData messageDestRef : messageDestRefs)
         {
            Collection<InjectionTargetType> injectionTargets = this.convertInjectionTargets(messageDestRef.getInjectionTargets());
            if (!injectionTargets.isEmpty())
            {
               String encName = EnvironmentEntryUtil.getENCName(messageDestRef.getName());
               this.envEntries.add(new EnvironmentEntryImpl(encName, injectionTargets));
            }
         }
      }
      
      // service references
      ServiceReferencesMetaData serviceRefs = this.delegate.getServiceReferences();
      if (serviceRefs != null)
      {
         for (ServiceReferenceMetaData serviceRef : serviceRefs)
         {
            Collection<InjectionTargetType> injectionTargets = this.convertInjectionTargets(serviceRef.getInjectionTargets());
            if (!injectionTargets.isEmpty())
            {
               String encName = EnvironmentEntryUtil.getENCName(serviceRef.getName());
               this.envEntries.add(new EnvironmentEntryImpl(encName, injectionTargets));
            }
         }
      }
   }

   /**
    * Converts and returns a collection of {@link ResourceInjectionTargetMetaData} to a collection of 
    * jboss-injection specific {@link InjectionTargetType injection targets}
    * 
    * @param resourceInjectionTargets
    * @return Returns an empty collection if the passed <code>resourceInjectionTargets</code> is null or empty
    */
   private Collection<InjectionTargetType> convertInjectionTargets(Collection<ResourceInjectionTargetMetaData> resourceInjectionTargets)
   {
      if (resourceInjectionTargets == null || resourceInjectionTargets.isEmpty())
      {
         return new HashSet<InjectionTargetType>();
      }
      Collection<InjectionTargetType> injectionTargets = new HashSet<InjectionTargetType>();
      for (ResourceInjectionTargetMetaData resourceInjectionTarget : resourceInjectionTargets)
      {
         Class<?> targetClass;
         try
         {
            targetClass = this.classLoader.loadClass(resourceInjectionTarget.getInjectionTargetClass());
         }
         catch (ClassNotFoundException cnfe)
         {
            throw new RuntimeException("Could not load injection target class: " + resourceInjectionTarget.getInjectionTargetClass(), cnfe);
         }
         injectionTargets.add(new InjectionTarget(targetClass, resourceInjectionTarget.getInjectionTargetName()));
      }
      return injectionTargets;
   }
}
