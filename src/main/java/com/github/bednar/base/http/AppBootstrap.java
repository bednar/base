package com.github.bednar.base.http;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;

import com.github.bednar.base.event.Dispatcher;
import com.github.bednar.base.event.UpdateApplicationEvent;
import com.github.bednar.base.inject.Injector;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.grouplens.grapht.Module;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jakub Bednář (31/08/2013 11:59 AM)
 */
public class AppBootstrap implements ServletContextListener
{
    private static final Logger LOG = LoggerFactory.getLogger(AppBootstrap.class);

    public static final String INJECTOR_KEY = "injector";
    public static final String SYMBOL_BASE_PACKAGE = "com.github.bednar";

    @Override
    public void contextInitialized(final ServletContextEvent sce)
    {
        Injector injector = new Injector(findModule(sce));

        ServletContext servletContext = sce.getServletContext();
        servletContext.setAttribute(INJECTOR_KEY, injector);

        Dispatcher dispatcher = injector.getInstance(Dispatcher.class);
        dispatcher.publish(new UpdateApplicationEvent());
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce)
    {
    }

    @Nonnull
    public static Injector getInjector(final @Nonnull ServletContext servletContext)
    {
        return (Injector) servletContext.getAttribute(INJECTOR_KEY);
    }

    @Nonnull
    private Module[] findModule(final @Nonnull ServletContextEvent sce)
    {
        List<Module> modules = Lists.newArrayList();

        Reflections reflections = new Reflections(SYMBOL_BASE_PACKAGE);
        for (Class<? extends Module> moduleType : reflections.getSubTypesOf(Module.class))
        {
            try
            {
                Module module = newInstance(moduleType, sce.getServletContext());

                modules.add(module);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        return Iterables.toArray(modules, Module.class);
    }

    @Nonnull
    private Module newInstance(final @Nonnull Class<? extends Module> moduleType,
                               final @Nonnull ServletContext servletContext) throws InstantiationException, IllegalAccessException
    {
        try
        {
            return ConstructorUtils.invokeConstructor(moduleType, servletContext);
        }
        catch (Exception e)
        {
            LOG.debug("[{}][hasn't constructor with ServletContext parameter]", moduleType.getName());
        }

        return moduleType.newInstance();
    }
}
