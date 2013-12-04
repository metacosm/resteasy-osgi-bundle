/**
 * Copyright (c) 2012.
 */
package org.jboss.resteasy.osgi.service.internal;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServlet;

import org.jboss.resteasy.osgi.service.api.ResteasyService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ResteasyServiceImpl implements ResteasyService
{

    private static final transient Logger LOG = LoggerFactory.getLogger( ResteasyServiceImpl.class );


    protected volatile BundleContext bundleContext;

    protected volatile ConcurrentHashMap<String, ServiceRegistration> resteasyServlets;


    public ResteasyServiceImpl( BundleContext bundleContext ) throws Exception
    {

        this.bundleContext = bundleContext;
        resteasyServlets = new ConcurrentHashMap<String, ServiceRegistration>();

    }

    protected ResteasyHttpServletDispatcher getResteasyServlet( String alias )
    {
        if ( resteasyServlets.containsKey( alias ) )
        {
            return (ResteasyHttpServletDispatcher)bundleContext.getService( resteasyServlets.get( alias ).getReference() );
        }

        return createResteasyServletIfApplicable( alias );
    }


    protected synchronized ResteasyHttpServletDispatcher createResteasyServletIfApplicable( String alias )
    {

        if ( resteasyServlets.containsKey( alias ) )
        {
            return (ResteasyHttpServletDispatcher)bundleContext.getService( resteasyServlets.get( alias ).getReference() );
        }

        Dictionary<Object, Object> props;

        props = new Hashtable<Object, Object>();

        if ( null == alias || alias.trim().length() == 0 )
        {
            props.put( "alias", "/rest/*" );
        }
        else
        {
            props.put( "alias", alias );
        }

        ResteasyHttpServletDispatcher resteasyHttpServletDispatcher = new ResteasyHttpServletDispatcher();

        ServiceRegistration serviceRegistrationForResteasyHttpServletDispatcher =
            bundleContext.registerService( HttpServlet.class.getName(), resteasyHttpServletDispatcher, props );

        resteasyServlets.put( alias, serviceRegistrationForResteasyHttpServletDispatcher );

        try
        {
            resteasyHttpServletDispatcher.waitTillAvailable();
        }
        catch ( InterruptedException e )
        {
            throw new RuntimeException( e );
        }

        return resteasyHttpServletDispatcher;

    }


    @Override
    public void addSingletonResource( String alias, Object resource )
    {

        LOG.info( ResteasyServiceImpl.class.getName() + ".addSingletonResource ["
                + "alias=" + String.valueOf( alias ) + ",resource=" + String.valueOf( resource ) + "]" );

        ResteasyHttpServletDispatcher resteasyServlet = getResteasyServlet( alias );
        resteasyServlet.getDispatcher().getRegistry().addSingletonResource( resource );
    }

    @Override
    public void removeSingletonResource( String alias, Class<?> clazz )
    {
        LOG.info( ResteasyServiceImpl.class.getName() + ".removeRegistrations [" + String.valueOf( clazz.getName() ) + "]" );
        ResteasyHttpServletDispatcher resteasyServlet = getResteasyServlet( alias );
        resteasyServlet.getDispatcher().getRegistry().removeRegistrations( clazz );
    }

    public synchronized void removeResteasyServlet( String alias )
    {

        if ( null == alias || alias.trim().length() == 0 )
        {
            return;
        }


        ServiceRegistration serviceRegistrationForResteasyServlet = resteasyServlets.remove( alias );
        serviceRegistrationForResteasyServlet.unregister();

    }

    public synchronized void tearDown()
    {
        for ( Map.Entry<String, ServiceRegistration> resteasyServletEntry : resteasyServlets.entrySet() )
        {
            resteasyServletEntry.getValue().unregister();
        }

        resteasyServlets = null;
    }


}