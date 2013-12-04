/**
 * Copyright (c) 2012.
 */
package org.jboss.resteasy.osgi.service.internal;

import java.util.concurrent.TimeoutException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ResteasyHttpServletDispatcher extends HttpServletDispatcher
{

    private static final transient Logger LOG = LoggerFactory.getLogger( ResteasyHttpServletDispatcher.class );

    private static final long serialVersionUID = 1L;

    protected volatile boolean isAvailable;

    public ResteasyHttpServletDispatcher()
    {
        super();
    }

    @Override
    public synchronized void init( ServletConfig servletConfig ) throws ServletException
    {
        super.init( servletConfig );
        isAvailable = true;
    }

    @Override
    public synchronized void destroy()
    {
        isAvailable = false;
        super.destroy();
    }

    public synchronized boolean isAvailable()
    {
        return isAvailable;
    }

    public void waitTillAvailable() throws InterruptedException
    {

        try
        {
            waitTillAvailable( null );
        }
        catch ( TimeoutException e )
        {
            // Should never happen
            throw new RuntimeException( e );
        }
    }

    public void waitTillAvailable( Long timeout ) throws InterruptedException, TimeoutException
    {

        final long pollingInterval = 1000;


        long stopWatch = System.currentTimeMillis();

        while ( !isAvailable )
        {
            LOG.info( ResteasyHttpServletDispatcher.class.getName() + " instance currently unavailable" );
            Thread.sleep( pollingInterval );
            long timeElapsed = System.currentTimeMillis() - stopWatch;

            if ( null != timeout && timeElapsed > timeout )
            {
                throw new TimeoutException();
            }
        }

        LOG.info( ResteasyHttpServletDispatcher.class.getName() + " instance available" );

    }
}
