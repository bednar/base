package com.github.bednar.base.utils.resource;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jakub Bednář (29/12/2013 11:49)
 */
public class FluentResourceTest
{
    private FluentResource resource;

    @Before
    public void before()
    {
        resource = FluentResource.byPath("/resource.txt");
    }

    @After
    public void after()
    {
        resource.close();
    }

    @Test
    public void asString()
    {
        Assert.assertEquals("Testing Resource Content", resource.asString());
    }

    @Test
    public void asReader() throws IOException
    {
        Reader reader = resource.asReader();

        Assert.assertEquals("Testing Resource Content", IOUtils.toString(reader));
    }

    @Test
    public void byURL()
    {
        URL url = this.getClass().getResource("/resource.txt");

        try (FluentResource fluentResource = FluentResource.byURL(url))
        {
            Assert.assertEquals(resource.asString(), fluentResource.asString());
        }
    }
}
