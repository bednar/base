package com.github.bednar.base.api;

import javax.annotation.Nonnull;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutionException;

import com.github.bednar.base.AbstractBaseTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jakub Bednář (24/08/2013 11:08 AM)
 */
public class ApiDemonstrationTest extends AbstractBaseTest
{
    @Test
    public void ok() throws ExecutionException, InterruptedException
    {
        Response response = request("200");

        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("{\"status\":200}", response.readEntity(String.class));
    }

    @Test
    public void unAuthorized() throws ExecutionException, InterruptedException
    {
        Response response = request("401");

        Assert.assertEquals(401, response.getStatus());
        Assert.assertEquals("{\"status\":401}", response.readEntity(String.class));
    }

    @Test
    public void forbidden() throws ExecutionException, InterruptedException
    {
        Response response = request("403");

        Assert.assertEquals(403, response.getStatus());
        Assert.assertEquals("{\"status\":403}", response.readEntity(String.class));
    }

    @Test
    public void error() throws ExecutionException, InterruptedException
    {
        Response response = request("500");

        Assert.assertEquals(500, response.getStatus());
        Assert.assertEquals("{\"status\":500}", response.readEntity(String.class));
    }

    @Nonnull
    private Response request(final @Nonnull String relativePath) throws ExecutionException, InterruptedException
    {
        return ClientBuilder.newClient()
                .target(embeddedJetty.getURL() + "/api/test/" + relativePath)
                .request("application/json")
                .buildGet()
                .submit()
                .get();
    }
}
