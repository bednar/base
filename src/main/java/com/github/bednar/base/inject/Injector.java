package com.github.bednar.base.inject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import org.grouplens.grapht.InjectorBuilder;
import org.grouplens.grapht.Module;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jakub Bednář (19/08/2013 2:27 PM)
 */
public class Injector
{
    private static final Logger LOG = LoggerFactory.getLogger(Injector.class);

    private final org.grouplens.grapht.Injector delegate;

    private final Set<Integer> initialized = Sets.newHashSet();

    /**
     * @param modules for Inject
     */
    public Injector(final @Nonnull Module... modules)
    {
        delegate = new InjectorBuilder(modules).build();
    }

    /**
     * @see org.grouplens.grapht.Injector#getInstance(Class)
     */
    public <T> T getInstance(Class<T> type)
    {
        T instance = delegate.getInstance(type);

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (instance)
        {
            if (!initialized.contains(instance.hashCode()))
            {
                initializeInstance(instance);

                initialized.add(instance.hashCode());
            }
        }

        return instance;
    }

    private <T> void initializeInstance(final @Nonnull T instance)
    {
        LOG.info("Injector.initializeInstance[{}][start]", instance.getClass());

        //Inject fields
        Set<Field> fields = Reflections.getAllFields(instance.getClass(), new Predicate<Field>()
        {
            @Override
            public boolean apply(@Nullable final Field field)
            {
                return field != null && field.getAnnotation(Inject.class) != null;
            }
        });

        for (Field field : fields)
        {
            Class<?> type = field.getType();
            try
            {
                field.setAccessible(true);
                field.set(instance, getInstance(type));
            }
            catch (IllegalAccessException iae)
            {
                throw new RuntimeException(iae);
            }
        }

        //Post construct
        Set<Method> postConstructs = Reflections.getAllMethods(instance.getClass(), new Predicate<Method>()
        {
            @Override
            public boolean apply(final @Nullable Method method)
            {
                return method != null && method.getAnnotation(PostConstruct.class) != null;
            }
        });

        for (Method postConstruct : postConstructs)
        {
            try
            {
                postConstruct.invoke(instance, this);
            }
            catch (Throwable throwable)
            {
                throw new RuntimeException(throwable);
            }
        }

        LOG.info("Injector.initializeInstance[{}][done]", instance.getClass());
    }
}
