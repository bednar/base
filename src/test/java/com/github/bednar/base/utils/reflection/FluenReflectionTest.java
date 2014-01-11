package com.github.bednar.base.utils.reflection;

import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.collect.Iterables;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jakub Bednář (23/11/2013 09:02)
 */
public class FluenReflectionTest
{
    @Test
    public void searchWholeClasspath()
    {
        Set<Class<? extends ClassLoader>> classLoaders = FluentReflection
                .forBasePackage()
                .getSubTypesOf(ClassLoader.class);

        Assert.assertNotNull(classLoaders);
        Assert.assertTrue(classLoaders.size() > 0);
    }

    @Test
    public void resourceByPattern()
    {
        Set<String> resources = FluentReflection
                .forPackage("com.github.bednar.base.utils.reflection")
                .getResources(Pattern.compile(".*\\.txt"));

        Assert.assertEquals(1, resources.size());
        Assert.assertTrue(Iterables.contains(resources, "com/github/bednar/base/utils/reflection/some-resource.txt"));
    }

    @Test
    public void resourceByPatternMatchPathNotOnlyName()
    {
        Set<String> resources = FluentReflection
                .forPackage("com.github.bednar.base.utils")
                .getResources(Pattern.compile("/resource/.*\\.txt"));

        Assert.assertEquals(3, resources.size());
        Assert.assertTrue(Iterables.contains(resources, "com/github/bednar/base/utils/resource/resource1.txt"));
        Assert.assertTrue(Iterables.contains(resources, "com/github/bednar/base/utils/resource/resource2.txt"));
        Assert.assertTrue(Iterables.contains(resources, "com/github/bednar/base/utils/resource/sub/resource3.txt"));
    }
}
