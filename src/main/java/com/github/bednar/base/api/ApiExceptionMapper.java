package com.github.bednar.base.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jakub Bednář (01/09/2013 8:59 AM)
 */
@Provider
public class ApiExceptionMapper implements ExceptionMapper<Exception>
{
    private static final Logger LOG = LoggerFactory.getLogger(ApiExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception)
    {
        LOG.error("[mapping-exception]", exception);

        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(exception.getMessage())
                .build();
    }
}
