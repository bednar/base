package com.github.bednar.base.api;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.util.Set;

import com.github.bednar.base.http.AppBootstrap;
import com.github.bednar.base.inject.Injector;
import com.github.bednar.base.utils.reflection.FluentReflection;
import com.google.common.collect.Sets;

/**
 * @author Jakub Bednář (24/08/2013 1:02 PM)
 */
public class Api extends Application
{
    private static Set<Object> singletons = Sets.newHashSet();

    public Api(final @Context ServletContext servletContext)
    {
        Injector injector = (Injector) servletContext.getAttribute(AppBootstrap.INJECTOR_KEY);

        for (Class resourceType : findResource())
        {
            Object resource = injector.getInstance(resourceType);

            singletons.add(resource);
        }
    }

    @Override
    public Set<Class<?>> getClasses()
    {
        return FluentReflection
                .forPackage(AppBootstrap.SYMBOL_BASE_PACKAGE)
                .getTypesAnnotatedWith(Provider.class);
    }

    @Override
    public Set<Object> getSingletons()
    {
        return singletons;
    }

    @Nonnull
    private Set<Class<? extends ApiResource>> findResource()
    {
        return FluentReflection
                .forPackage(AppBootstrap.SYMBOL_BASE_PACKAGE)
                .getSubTypesOf(ApiResource.class);
    }
}
