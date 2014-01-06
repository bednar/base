package com.github.bednar.base.utils.cache;

import javax.annotation.Nonnull;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;

/**
 * @author Jakub Bednář (06/01/2014 19:51)
 */
public final class FluentCache
{
    private static final CacheManager CACHE_MANAGER = Caching.getCachingProvider().getCacheManager();

    private FluentCache()
    {
    }

    @Nonnull
    public static CacheManager manager()
    {
        return CACHE_MANAGER;
    }

    @Nonnull
    public static <K, V> Cache<K, V> cacheByClass(@Nonnull final Class forType,
                                                  @Nonnull Class<K> keyType,
                                                  @Nonnull Class<V> valueType)
    {
        return cacheByName(forType.getName(), keyType, valueType);
    }

    @Nonnull
    public static <K, V> Cache<K, V> cacheByName(@Nonnull final String name,
                                                 @Nonnull Class<K> keyType,
                                                 @Nonnull Class<V> valueType)
    {
        Cache<K, V> cache = manager().getCache(name, keyType, valueType);
        if (cache != null)
        {
            return cache;
        }

        return createCache(name, keyType, valueType);
    }

    @Nonnull
    private synchronized static <K, V> Cache<K, V> createCache(@Nonnull final String name,
                                                               @Nonnull Class<K> keyType,
                                                               @Nonnull Class<V> valueType)
    {
        Cache<K, V> cache = manager().getCache(name);
        if (cache != null)
        {
            return cache;
        }

        MutableConfiguration<K, V> config = new MutableConfiguration<>();
        config
                .setStoreByValue(true)
                .setTypes(keyType, valueType)
                .setStatisticsEnabled(true);

        return manager().createCache(name, config);
    }
}
