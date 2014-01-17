package com.github.bednar.base.utils.resource;

import javax.annotation.CheckForNull;
import javax.annotation.CheckForSigned;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

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

    @Test
    public void asURL()
    {
        URL url = this.getClass().getResource("/resource.txt");

        try (FluentResource resource = FluentResource.byURL(url))
        {
            Assert.assertEquals(url, resource.asURL());
        }
    }

    @Test
    public void asPath() throws URISyntaxException
    {
        Path path = new File(this.getClass().getResource("/resource.txt").toURI()).toPath();

        try (FluentResource resource = FluentResource.byPath("/resource.txt"))
        {
            Assert.assertEquals(path, resource.asPath());
        }
    }

    @Test
    public void asPathForNotExist() throws URISyntaxException
    {
        try (FluentResource resource = FluentResource.byPath("/notexist/resource.txt"))
        {
            Assert.assertNull(resource.asPath());
        }
    }

    @Test
    public void reloadable()
    {
        try (FluentResource resource = FluentResource.byPath("/resource.txt"))
        {
            Assert.assertTrue(resource.isReloadable());
        }
    }

    @Test
    public void notReloadable()
    {
        String path = CheckForNull.class.getName().replaceAll("\\.", "/") + ".class";

        try (FluentResource resource = FluentResource.byPath(path))
        {
            Assert.assertFalse(resource.isReloadable());
        }
    }

    @Test
    public void reloadableForNotExist() throws URISyntaxException
    {
        try (FluentResource resource = FluentResource.byPath("/notexist/resource.txt"))
        {
            Assert.assertFalse(resource.isReloadable());
        }
    }

    @Test
    public void changeable()
    {
        try (FluentResource resource = FluentResource.byPath("/resource.txt"))
        {
            Assert.assertTrue(resource.isChangeable());
        }
    }

    @Test
    public void notChangeable()
    {
        String path = CheckForNull.class.getName().replaceAll("\\.", "/") + ".class";

        try (FluentResource resource = FluentResource.byPath(path))
        {
            Assert.assertFalse(resource.isChangeable());
        }
    }

    @Test
    public void changeableForNotExist() throws URISyntaxException
    {
        try (FluentResource resource = FluentResource.byPath("/notexist/resource.txt"))
        {
            Assert.assertFalse(resource.isChangeable());
        }
    }

    @Test
    public void update()
    {
        try (FluentResource resource = FluentResource.byPath("/resourceForChange.txt"))
        {
            Assert.assertEquals("before change", resource.asString());

            resource.update("after change");

            Assert.assertEquals("after change", resource.asString());
        }
    }

    @Test
    public void updateForNotChangable()
    {
        String path = CheckForNull.class.getName().replaceAll("\\.", "/") + ".class";

        try (FluentResource resource = FluentResource.byPath(path))
        {
            String beforeUpdate = resource.asString();

            resource.update("not apply");

            Assert.assertEquals(beforeUpdate, resource.asString());
        }
    }

    @Test
    public void deletable()
    {
        try (FluentResource resource = FluentResource.byPath("/resource.txt"))
        {
            Assert.assertTrue(resource.isDeletable());
        }
    }

    @Test
    public void notDeletable()
    {
        String path = CheckForNull.class.getName().replaceAll("\\.", "/") + ".class";

        try (FluentResource resource = FluentResource.byPath(path))
        {
            Assert.assertFalse(resource.isDeletable());
        }
    }

    @Test
    public void deletableForNotExist() throws URISyntaxException
    {
        try (FluentResource resource = FluentResource.byPath("/notexist/resource.txt"))
        {
            Assert.assertFalse(resource.isDeletable());
        }
    }

    @Test
    public void delete()
    {
        try (FluentResource resource = FluentResource.byPath("/resourceForDelete.txt"))
        {
            Assert.assertTrue(resource.exists());

            resource.delete();

            Assert.assertFalse(resource.exists());
        }
    }

    @Test
    public void deleteForNotChangable()
    {
        String path = CheckForNull.class.getName().replaceAll("\\.", "/") + ".class";

        try (FluentResource resource = FluentResource.byPath(path))
        {
            resource.delete();

            Assert.assertTrue(resource.exists());
        }
    }
}
