package com.github.bednar.base.http;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jakub Bednář (01/12/2013 13:34)
 */
public class AppInitialized implements ServletContextListener
{
    private static final Logger LOG = LoggerFactory.getLogger(AppInitialized.class);

    @Override
    public void contextInitialized(final ServletContextEvent sce)
    {
        ServletContext servletContext = sce.getServletContext();

        LOG.info(
                "\n\n{}\n" +
                "    ___                ____              __       __                            ____\n" +
                "   /   |  ____  ____  / __ )____  ____  / /______/ /__________ _____  ___  ____/ / /\n" +
                "  / /| | / __ \\/ __ \\/ __  / __ \\/ __ \\/ __/ ___/ __/ ___/ __ `/ __ \\/ _ \\/ __  / / \n" +
                " / ___ |/ /_/ / /_/ / /_/ / /_/ / /_/ / /_(__  ) /_/ /  / /_/ / /_/ /  __/ /_/ /_/  \n" +
                "/_/  |_/ .___/ .___/_____/\\____/\\____/\\__/____/\\__/_/   \\__,_/ .___/\\___/\\__,_(_)   \n" +
                "      /_/   /_/                                             /_/                     \n" +
                "\n", servletContext.getServletContextName());
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce)
    {
    }
}
