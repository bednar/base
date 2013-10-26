package com.github.bednar.base.api;

import javax.annotation.Nonnull;
import javax.ws.rs.client.ClientBuilder;
import java.util.concurrent.ExecutionException;

import com.github.bednar.base.AbstractBaseTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jakub Bednář (24/08/2013 11:08 AM)
 */
public class ApiInitializationTest extends AbstractBaseTest
{
    @Test
    public void demonstrationOk() throws ExecutionException, InterruptedException
    {
        int status = request("200");

        Assert.assertEquals(200, status);
    }

    @Test
    public void demonstrationError() throws ExecutionException, InterruptedException
    {
        int status = request("500");

        Assert.assertEquals(500, status);
    }

    @Test
    public void demonstrationUnAuthorized() throws ExecutionException, InterruptedException
    {
        int status = request("401");

        Assert.assertEquals(401, status);
    }

    @Nonnull
    private Integer request(final @Nonnull String relativePath) throws ExecutionException, InterruptedException
    {
        return ClientBuilder.newClient()
                .target("http://localhost:8090/api/test/" + relativePath)
                .request("application/json")
                .buildGet()
                .submit()
                .get()
                .getStatus();
    }
}
