/**
 * Copyright (c) 2012.
 */
package org.jboss.resteasy.osgi.service.api;


public interface ResteasyService
{


    /**
     * Add a SingletonResource bound instiad the path /rest/*
     *
     * @param resource
     */
    void addSingletonResource( String alias, Object resource );



    /**
     * Remove a SingletonResource.
     *
     * @param clazz
     */
    void removeSingletonResource( String alias, Class<?> clazz );

}