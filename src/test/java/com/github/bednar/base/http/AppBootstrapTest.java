package com.github.bednar.base.http;

import com.github.bednar.base.AbstractBaseTest;
import com.github.bednar.base.inject.Injector;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jakub Bednář (22/11/2013 09:20)
 */
public class AppBootstrapTest extends AbstractBaseTest
{
    @Test
    public void getInjector()
    {
        Injector injector = AppBootstrap.getInjector(embeddedJetty.getServletContext());

        Assert.assertNotNull(injector);
    }
}
