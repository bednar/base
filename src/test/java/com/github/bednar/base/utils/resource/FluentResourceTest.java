package com.github.bednar.base.utils.resource;

import java.io.IOException;
import java.io.Reader;

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
}
