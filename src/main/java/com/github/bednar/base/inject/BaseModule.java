package com.github.bednar.base.inject;

import javax.inject.Provider;

import com.github.bednar.base.event.Dispatcher;
import com.mycila.event.Dispatchers;
import com.mycila.event.ErrorHandlers;
import org.grouplens.grapht.Context;
import org.grouplens.grapht.Module;

/**
 * Base Depenency Injection module.
 *
 * @author Jakub Bednář (19/08/2013 10:18 AM)
 */
public class BaseModule implements Module
{
    @Override
    public void configure(final Context context)
    {
        context
                .bind(Dispatcher.class)
                .toProvider(
                        new Provider<Dispatcher>()
                        {
                            @Override
                            public Dispatcher get()
                            {
                                com.mycila.event.Dispatcher delegate = Dispatchers.synchronousSafe(ErrorHandlers.rethrow());

                                return new Dispatcher(delegate);
                            }
                        });
    }
}
