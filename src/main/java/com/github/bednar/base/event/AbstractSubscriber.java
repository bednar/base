package com.github.bednar.base.event;

import javax.annotation.Nonnull;

import com.mycila.event.Subscriber;

/**
 * @author Jakub Bednář (19/08/2013 1:08 PM)
 */
public abstract class AbstractSubscriber<T extends AbstractEvent> implements Subscriber<T>
{
    /**
     * @return type of event processed by subsrciber
     */
    @Nonnull
    public abstract Class<T> eventType();
}
