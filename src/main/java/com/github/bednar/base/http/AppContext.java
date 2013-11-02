package com.github.bednar.base.http;

import javax.annotation.Nonnull;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import java.io.IOException;

import com.github.bednar.base.inject.Injector;
import com.google.common.base.Preconditions;

/**
 * Add {@link com.github.bednar.base.inject.Injector} to {@link ThreadLocal} variable.
 *
 * @author Jakub Bednář (02/11/2013 08:51)
 */
public class AppContext implements Filter
{
    private static final ThreadLocal<Injector> THREAD_INJECTOR = new ThreadLocal<>();

    /**
     * Warning: {@link NullPointerException} if {@code Injector} isn't set.
     *
     * @return singleton instance of injector
     */
    @Nonnull
    public static Injector getInjector()
    {
        Injector injector = THREAD_INJECTOR.get();

        Preconditions.checkNotNull(injector);

        return injector;
    }

    /**
     * Warning: {@link NullPointerException} if {@code ServletContext} or {@code Injector} is null.
     *
     * @param servletContext Servlet Context
     */
    public static void initInjector(final @Nonnull ServletContext servletContext)
    {
        Preconditions.checkNotNull(servletContext);

        Injector injector = (Injector) servletContext.getAttribute(AppBootstrap.INJECTOR_KEY);

        THREAD_INJECTOR.set(injector);
    }

    /**
     * Cleanup {@code ThreadLocal} variables.
     */
    public static void clear()
    {
        THREAD_INJECTOR.remove();
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException
    {
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException
    {
        //Set injector
        AppContext.initInjector(request.getServletContext());

        //Do filter
        chain.doFilter(request, response);

        //Clear injector
        AppContext.clear();
    }

    @Override
    public void destroy()
    {
    }
}
