package com.github.bednar.base;

import com.github.bednar.base.http.AppBootstrap;
import com.github.bednar.base.inject.Injector;
import com.github.bednar.test.EmbeddedJetty;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author Jakub Bednář (19/08/2013 10:24 AM)
 */
public abstract class AbstractBaseTest
{
    private static EmbeddedJetty embeddedJetty;

    protected static Injector injector;

    @BeforeClass
    public static void before() throws Exception
    {
        embeddedJetty = new EmbeddedJetty().start();

        injector = (Injector) embeddedJetty.getServletContext().getAttribute(AppBootstrap.INJECTOR_KEY);
    }

    @AfterClass
    public static void after() throws Exception
    {
        embeddedJetty.stop();
    }
}
