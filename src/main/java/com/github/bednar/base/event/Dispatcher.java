package com.github.bednar.base.event;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.util.Set;

import com.github.bednar.base.http.AppBootstrap;
import com.github.bednar.base.inject.Injector;
import com.github.bednar.base.utils.reflection.FluentReflection;
import com.mycila.event.Topic;
import org.reflections.Reflections;

/**
 * @author Jakub Bednář (19/08/2013 1:33 PM)
 */
public class Dispatcher
{
    private final com.mycila.event.Dispatcher delegate;

    /**
     * @param delegate event dispatcher
     */
    public Dispatcher(final @Nonnull com.mycila.event.Dispatcher delegate)
    {
        this.delegate = delegate;
    }

    /**
     * @param injector for create subscribers
     */
    @PostConstruct
    public void initialize(final @Nonnull Injector injector)
    {
        for (Class<? extends AbstractSubscriber> subsriber : findSubscribers())
        {
            AbstractSubscriber<?> subscriber = injector.getInstance(subsriber);

            delegate.subscribe(topic(subscriber), subscriber.eventType(), subscriber);
        }
    }

    /**
     * Publish event by dispatcher.
     *
     * @param event for publish
     *
     * @see com.mycila.event.Dispatcher#publish(com.mycila.event.Topic, Object)
     */
    public void publish(final @Nonnull AbstractEvent<?> event)
    {
        delegate.publish(topic(event), event);
    }

    @Nonnull
    private Set<Class<? extends AbstractSubscriber>> findSubscribers()
    {
        return FluentReflection
                .forPackage(AppBootstrap.SYMBOL_BASE_PACKAGE)
                .getSubTypesOf(AbstractSubscriber.class);
    }

    @Nonnull
    private Topic topic(final @Nonnull AbstractSubscriber<?> subscriber)
    {
        Class<? extends AbstractEvent> eventType = subscriber.eventType();

        return topic(eventType);
    }

    @Nonnull
    private Topic topic(final @Nonnull AbstractEvent<?> event)
    {
        Class<? extends AbstractEvent> eventType = event.getClass();

        return topic(eventType);
    }

    @Nonnull
    private Topic topic(final @Nonnull Class<? extends AbstractEvent> eventType)
    {
        //from inner class
        if (eventType.getEnclosingClass() != null)
        {
            if (eventType.getGenericSuperclass() instanceof ParameterizedType)
            {
                return topic(((Class) ((ParameterizedType) eventType.getGenericSuperclass()).getRawType()));
            }
            else
            {
                return topic(((Class) eventType.getGenericSuperclass()));
            }
        }
        else
        {
            return Topic.topic(eventType.getName());
        }
    }
}
