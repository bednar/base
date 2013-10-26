package com.github.bednar.base.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author Jakub Bednář (24/08/2013 12:47 PM)
 */
@Path("/test")
public class ApiDemonstration implements ApiResource
{
    @GET
    @Path("/200")
    @Consumes("application/json")
    public Response ok()
    {
        return Response.ok().build();
    }

    @GET
    @Path("/500")
    @Consumes("application/json")
    public Response error()
    {
        return Response.serverError().build();
    }

    @GET
    @Path("/401")
    @Consumes("application/json")
    public Response unAuthorized()
    {
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
