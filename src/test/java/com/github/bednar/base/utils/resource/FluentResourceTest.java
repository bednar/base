package com.github.bednar.base.utils.resource;

import javax.annotation.CheckForNull;
import javax.annotation.CheckForSigned;
import java.io.IOException;
import java.net.URL;

import com.github.bednar.base.utils.collection.ListAutoCloseable;
import com.github.bednar.base.utils.throwable.FluentException;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jakub Bednář (29/12/2013 11:49)
 */
public class FluentResourceTest
{
    @Test
    public void asString()
    {
        try (FluentResource resource = FluentResource.byPath("/resource.txt"))
        {
            Assert.assertEquals("Testing Resource Content", resource.asString());
        }
    }

    @Test
    public void asReader() throws IOException
    {
        try (FluentResource resource = FluentResource.byPath("/resource.txt"))
        {
            Assert.assertEquals("Testing Resource Content", IOUtils.toString(resource.asReader()));
        }
    }

    @Test
    public void byURL()
    {
        URL url = this.getClass().getResource("/resource.txt");

        try (FluentResource resource = FluentResource.byURL(url))
        {
            Assert.assertEquals("Testing Resource Content", resource.asString());
        }
    }

    @Test
    public void exist()
    {
        try (FluentResource resource = FluentResource.byPath("/resource.txt"))
        {
            Assert.assertTrue(resource.exists());
        }
    }

    @Test
    public void notExist()
    {
        try (FluentResource resource = FluentResource.byPath("/notexist/resource.txt"))
        {
            Assert.assertFalse(resource.exists());
        }
    }

    @Test
    public void pathForExist()
    {
        try (FluentResource resource = FluentResource.byPath("/resource.txt"))
        {
            Assert.assertTrue(resource.path().startsWith("file:"));
            Assert.assertTrue(resource.path().endsWith("target/test-classes/resource.txt"));
        }
    }

    @Test
    public void pathForNotExist()
    {
        try (FluentResource resource = FluentResource.byPath("/notexist/resource.txt"))
        {
            Assert.assertEquals("", resource.path());
        }
    }

    @Test
    public void toStringValue()
    {
        try (FluentResource resource = FluentResource.byPath("/resource.txt"))
        {
            Assert.assertTrue(resource.toString().startsWith("[class com.github.bednar.base.utils.resource.FluentResource][path=file:/"));
            Assert.assertTrue(resource.toString().endsWith("/target/test-classes/resource.txt]"));
        }
    }

    @Test
    public void directory()
    {
        try (FluentResource resource = FluentResource.byPath("/resource.txt"))
        {
            Assert.assertTrue(resource.directory().startsWith("file:"));
            Assert.assertTrue(resource.directory().endsWith("/target/test-classes/"));
        }
    }

    @Test
    public void directoryFromJar()
    {
        String path = CheckForNull.class.getName().replaceAll("\\.", "/") + ".class";

        try (FluentResource resource = FluentResource.byPath(path))
        {
            Assert.assertTrue(resource.directory().startsWith("jar:file:"));
            Assert.assertTrue(resource.directory().endsWith("!/javax/annotation/"));
        }
    }

    @Test
    public void loadByURLExternalForm()
    {
        String path = CheckForNull.class.getName().replaceAll("\\.", "/") + ".class";

        try (FluentResource resource = FluentResource.byPath(path))
        {
            path = resource.directory();
        }

        path += CheckForSigned.class.getSimpleName() + ".class";

        try (FluentResource resource = FluentResource.byPath(path))
        {
            Assert.assertTrue(resource.exists());
        }
    }

    @Test(expected = FluentException.class)
    public void loadByURLExternalFormNotSupportedProcol()
    {
        FluentResource.byPath("beer:zubr");
    }

    @Test
    public void byPatternFromPackage()
    {
        String pkcg     = "com.github.bednar.base.utils";
        String pattern  = "/resource/.*\\.txt";

        try (ListAutoCloseable<FluentResource> fluentResources = FluentResource.byPattern(pkcg, pattern))
        {
            Assert.assertEquals(3, fluentResources.size());
        }
    }
}
