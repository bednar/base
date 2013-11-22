package com.github.bednar.base;

import com.github.bednar.base.http.AppContext;
import com.github.bednar.test.EmbeddedJetty;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author Jakub Bednář (19/08/2013 10:24 AM)
 */
public abstract class AbstractBaseTest
{
    protected static EmbeddedJetty embeddedJetty;

    @BeforeClass
    public static void beforeClass() throws Exception
    {
        embeddedJetty = new EmbeddedJetty().start();

        AppContext.initInjector(embeddedJetty.getServletContext());
    }

    @AfterClass
    public static void afterClass() throws Exception
    {
        AppContext.clear();

        embeddedJetty.stop();
    }
}
