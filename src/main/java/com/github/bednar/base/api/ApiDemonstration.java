package com.github.bednar.base.api;

import javax.annotation.Nonnull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;

/**
 * @author Jakub Bednář (24/08/2013 12:47 PM)
 */
@Path("test")
@Consumes("application/json")
@Produces("application/json")
@com.wordnik.swagger.annotations.Api(
        value = "Testing",
        description = "Demonstration response of API.",
        position = 0)
public class ApiDemonstration implements ApiResource
{
    @GET
    @Path("200")
    @ApiOperation(position = 1, value = "Success Response")
    @ApiResponse(code = 200, message = "{\"status\":200}")
    public Response ok()
    {
        return Response
                .ok(new ResponseStatus(200))
                .build();
    }

    @GET
    @Path("401")
    @ApiOperation(position = 2, value = "Unauthorized")
    @ApiResponse(code = 401, message = "{\"status\":401}")
    public Response unAuthorized()
    {
        return Response
                .status(Response.Status.UNAUTHORIZED)
                .entity(new ResponseStatus(401))
                .build();
    }

    @GET
    @Path("403")
    @ApiOperation(position = 3, value = "Forbidden")
    @ApiResponse(code = 403, message = "{\"status\":403}")
    public Response forbidden()
    {
        return Response
                .status(Response.Status.FORBIDDEN)
                .entity(new ResponseStatus(403))
                .build();
    }

    @GET
    @Path("500")
    @ApiOperation(position = 4, value = "Server Error")
    @ApiResponse(code = 500, message = "{\"status\":500}")
    public Response error()
    {
        return Response
                .serverError()
                .entity(new ResponseStatus(500))
                .build();
    }

    private class ResponseStatus
    {
        public final Integer status;

        private ResponseStatus(@Nonnull final Integer status)
        {
            this.status = status;
        }
    }
}
