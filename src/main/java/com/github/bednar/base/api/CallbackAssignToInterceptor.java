package com.github.bednar.base.api;

import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.lang3.StringUtils;

import static org.jboss.resteasy.plugins.providers.jackson.JacksonJsonpInterceptor.jsonpCompatibleMediaTypes;

/**
 * By <strong>api/values?callbackAssignTo=window.values</strong> can assign response to variables.
 *
 * @author Jakub Bednář (28/12/2013 13:25)
 */
@Provider
@ConstrainedTo(RuntimeType.SERVER)
public class CallbackAssignToInterceptor implements WriterInterceptor
{
    private UriInfo uri;

    @Override
    public void aroundWriteTo(final WriterInterceptorContext context) throws IOException, WebApplicationException
    {
        String variable = uri.getQueryParameters().getFirst("callbackAssignTo");

        if (StringUtils.isNotBlank(variable) && !jsonpCompatibleMediaTypes.getPossible(context.getMediaType()).isEmpty())
        {
            OutputStreamWriter writer = new OutputStreamWriter(context.getOutputStream());

            //window.variable = ...;
            writer.write(variable + " = ");
            writer.flush();
            context.proceed();
            writer.write(";");
            writer.flush();
        }
        else
        {
            context.proceed();
        }
    }

    @Context
    public void setUri(UriInfo uri)
    {
        this.uri = uri;
    }
}
