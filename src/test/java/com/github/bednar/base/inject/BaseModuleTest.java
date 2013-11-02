package com.github.bednar.base.inject;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import com.github.bednar.base.AbstractBaseTest;
import com.github.bednar.base.event.Dispatcher;
import com.github.bednar.base.http.AppContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jakub Bednář (19/08/2013 10:21 AM)
 */
public class BaseModuleTest extends AbstractBaseTest
{
    private Injector injector;

    @Before
    public void before()
    {
        injector = AppContext.getInjector();
    }

    @Test
    public void dispatcherNotNull()
    {
        Dispatcher dispatcher = injector.getInstance(Dispatcher.class);

        Assert.assertNotNull(dispatcher);
    }

    @Test
    public void sameInstance()
    {
        Dispatcher dispatcher1 = injector.getInstance(Dispatcher.class);
        Dispatcher dispatcher2 = injector.getInstance(Dispatcher.class);

        Assert.assertEquals(dispatcher1.hashCode(), dispatcher2.hashCode());
    }

    @Test
    public void instanceWithoutProvider()
    {
        TypeWithoutExplicitProvider withoutProvider = injector.getInstance(TypeWithoutExplicitProvider.class);
        Dispatcher dispatcher = injector.getInstance(Dispatcher.class);

        Assert.assertNotNull(withoutProvider);
        Assert.assertNotNull(withoutProvider.getDispatcher());

        Assert.assertEquals(withoutProvider.getDispatcher().hashCode(), dispatcher.hashCode());
    }

    public static class TypeWithoutExplicitProvider
    {
        @Inject
        private Dispatcher dispatcher;

        @Nonnull
        public Dispatcher getDispatcher()
        {
            return dispatcher;
        }
    }
}
