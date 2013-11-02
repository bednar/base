package com.github.bednar.base.utils.reflection;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import org.reflections.Reflections;

/**
 * Java Reflection Utils
 *
 * @author Jakub Bednář (02/11/2013 09:54)
 */
public final class FluentReflection
{
    private final Reflections reflections;

    private FluentReflection(@Nonnull final String pkcg)
    {
        Preconditions.checkNotNull(pkcg);

        this.reflections = new Reflections(pkcg);
    }

    /**
     * @param pkcg for lookup
     *
     * @return new instance for lookup in {@code pkcg}
     */
    @Nonnull
    public static FluentReflection forPackage(@Nonnull final String pkcg)
    {
        Preconditions.checkNotNull(pkcg);

        return new FluentReflection(pkcg);
    }

    /**
     * @return new instance for lookup in base package
     */
    @Nonnull
    public static FluentReflection forBasePackage()
    {
        return new FluentReflection("");
    }

    /**
     * @see Reflections#getSubTypesOf(Class)
     */
    @Nonnull
    public <T> Set<Class<? extends T>> getSubTypesOf(@Nonnull final Class<T> type)
    {
        Preconditions.checkNotNull(type);

        return reflections.getSubTypesOf(type);
    }

    /**
     * @see Reflections#getResources(java.util.regex.Pattern)
     */
    @Nonnull
    public Set<String> getResources(@Nonnull final Pattern pattern)
    {
        Preconditions.checkNotNull(pattern);

        return reflections.getResources(pattern);
    }
}
