package com.github.bednar.base.api;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutionException;

import com.github.bednar.base.AbstractBaseTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jakub Bednář (28/12/2013 13:33)
 */
public class CallbackAssignToInterceptorTest extends AbstractBaseTest
{
    @Test
    public void assign() throws ExecutionException, InterruptedException
    {
        Response response = ClientBuilder.newClient()
                .target(embeddedJetty.getURL() + "/api/test/200")
                .queryParam("callbackAssignTo", "window.customValues")
                .request("application/json")
                .buildGet()
                .submit()
                .get();

        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("window.customValues = {\"status\":200};", response.readEntity(String.class));
    }
}
