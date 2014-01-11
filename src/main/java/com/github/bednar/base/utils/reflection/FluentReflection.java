package com.github.bednar.base.utils.reflection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 * Java Reflection Utils
 *
 * @author Jakub Bednář (02/11/2013 09:54)
 */
public final class FluentReflection
{
    private static LoadingCache<String, Reflections> cache = CacheBuilder.newBuilder().build(new Loader());

    private final String pckg;
    private final String pckgPath;
    private final Reflections reflections;

    private FluentReflection(@Nonnull final String pkcg)
    {
        Preconditions.checkNotNull(pkcg);

        this.pckg           = pkcg;
        this.pckgPath       = pckg.replaceAll("\\.", "/");
        this.reflections    = cache.getUnchecked(pkcg);
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
        return new FluentReflection("*");
    }

    /**
     * @see Reflections#getSubTypesOf(Class)
     */
    @Nonnull
    public <T> Set<Class<? extends T>> getSubTypesOf(@Nonnull final Class<T> type)
    {
        Preconditions.checkNotNull(type);

        return FluentIterable.from(reflections.getSubTypesOf(type))
                .filter(new Predicate<Class<? extends T>>()
                {
                    @Override
                    public boolean apply(@Nullable final Class<? extends T> type)
                    {
                        return type != null && !Modifier.isAbstract(type.getModifiers());
                    }
                }).toSet();
    }

    /**
     * @see Reflections#getTypesAnnotatedWith(java.lang.annotation.Annotation)
     */
    @Nonnull
    public Set<Class<?>> getTypesAnnotatedWith(@Nonnull final Class<? extends Annotation> annotation)
    {
        Preconditions.checkNotNull(annotation);

        return reflections.getTypesAnnotatedWith(annotation);
    }

    /**
     * @see Reflections#getResources(java.util.regex.Pattern)
     */
    @Nonnull
    public Set<String> getResources(@Nonnull final Pattern pattern)
    {
        Preconditions.checkNotNull(pattern);

        Multimap<String,String> resources = reflections.getStore().get(ResourcesScanner.class);
        if (resources == null)
        {
            return Sets.newHashSet();
        }

        return FluentIterable.from(resources.values()).filter(new Predicate<String>()
        {
            @Override
            public boolean apply(@Nullable final String path)
            {
                String resourcePath = path;

                if (resourcePath == null)
                {
                    return false;
                }

                //remove package prefix
                if (resourcePath.startsWith(pckgPath))
                {
                    resourcePath = resourcePath.replace(pckgPath, "");
                }

                return pattern.matcher(resourcePath).matches();
            }
        }).toSet();
    }

    private static class Loader extends CacheLoader<String, Reflections>
    {
        @Override
        public Reflections load(@Nonnull final String key) throws Exception
        {
            ConfigurationBuilder configuration;

            //search on whole classpath
            if ("*".equals(key))
            {
                configuration = new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forJavaClassPath());
            }
            else
            {
                configuration = ConfigurationBuilder.build(key);
            }

            configuration.addScanners(new ResourcesScanner());

            return new Reflections(configuration);
        }
    }
}
