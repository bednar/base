package com.github.bednar.base.utils.cache;

import javax.cache.Cache;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jakub Bednář (06/01/2014 19:54)
 */
public class FluentCacheTest
{
    @Test
    public void managerNotNull()
    {
        Assert.assertNotNull(FluentCache.manager());
    }

    @Test
    public void managerStatic()
    {
        Assert.assertEquals(FluentCache.manager(), FluentCache.manager());
    }

    @Test
    public void cacheNotNull()
    {
        Assert.assertNotNull(FluentCache.cache("not-null", String.class, String.class));
    }

    @Test
    public void cacheSingleton()
    {
        Cache<String, String> cache1 = FluentCache.cache("cache-singleton", String.class, String.class);
        Cache<String, String> cache2 = FluentCache.cache("cache-singleton", String.class, String.class);

        Assert.assertEquals(cache1, cache2);
    }

    @Test
    public void canPutToCache()
    {
        Cache<Integer, String> cache = FluentCache.cache("cache-put-to-cache", Integer.class, String.class);

        cache.put(0, "test");

        Assert.assertEquals("test", cache.get(0));
        Assert.assertNull(cache.get(1));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = ClassCastException.class)
    public void cacheIsGeneric()
    {
        Cache cache = FluentCache.cache("cache-is-generic", Integer.class, String.class);

        cache.put("test", 1L);
    }
}
