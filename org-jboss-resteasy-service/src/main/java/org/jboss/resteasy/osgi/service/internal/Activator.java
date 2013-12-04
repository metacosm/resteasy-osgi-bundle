/**
 * Copyright (c) 2012.
 */
package org.jboss.resteasy.osgi.service.internal;

import org.jboss.resteasy.osgi.service.api.ResteasyService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Activator implements BundleActivator
{

    private static final transient Logger LOG = LoggerFactory.getLogger( Activator.class );

    private volatile ServiceRegistration resteasyServiceReference;


    @SuppressWarnings( { "rawtypes", "unchecked" } )
    @Override
    public void start( BundleContext bundleContext ) throws Exception
    {
        LOG.info( "Starting bundle "
                + bundleContext.getBundle().getSymbolicName() + " ["
                + bundleContext.getBundle().getVersion() + "]" );


        ResteasyServiceImpl resteasyServiceImpl = new ResteasyServiceImpl( bundleContext );

        resteasyServiceReference =
            bundleContext.registerService( ResteasyService.class.getName(), resteasyServiceImpl, null );


    }

    @Override
    public void stop( BundleContext bundleContext ) throws Exception
    {
        LOG.info( "Stopping bundle " + bundleContext.getBundle().getSymbolicName() + " ["
                + bundleContext.getBundle().getVersion() + "]" );

        if ( resteasyServiceReference != null )
        {
            ResteasyServiceImpl resteasyServiceImpl =
                (ResteasyServiceImpl)bundleContext.getService( resteasyServiceReference.getReference() );
            resteasyServiceImpl.tearDown();
            resteasyServiceReference.unregister();
            resteasyServiceReference = null;
        }

        LOG.info( "Bundle stopped sucessfully" );
    }


}