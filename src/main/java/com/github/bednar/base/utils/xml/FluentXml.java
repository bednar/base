package com.github.bednar.base.utils.xml;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.github.bednar.base.utils.resource.FluentResource;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * Fluent API for XML.
 *
 * @author Jakub Bednář (03/09/2013 6:47 PM)
 */
public class FluentXml
{
    private static final Logger LOG = LoggerFactory.getLogger(FluentXml.class);

    private final Document document;
    private final XPath xpath;

    private FluentXml(final @Nullable Document document)
    {
        this.document = document;
        this.xpath = document != null ? XPathFactory.newInstance().newXPath() : null;
    }

    @Nonnull
    public static FluentXml byResource(final @Nonnull String resource)
    {
        Preconditions.checkNotNull(resource);

        InputStream resourceStream = FluentResource
                .byPath(resource)
                .asStream();

        if (resourceStream == null)
        {
            return new FluentXml(null);
        }

        return byStream(resourceStream);
    }

    @Nonnull
    public static FluentXml byStream(final @Nonnull InputStream stream)
    {
        Preconditions.checkNotNull(stream);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        try
        {
            Document document = factory.newDocumentBuilder().parse(stream);

            return new FluentXml(document);
        }
        catch (Exception e)
        {
            throw new FluentXmlException(e);
        }
        finally
        {
            try
            {
                stream.close();
            }
            catch (IOException e)
            {
                LOG.error("[cannot-close-input-stream]", e);
            }
        }
    }

    @Nonnull
    public String xpathToString(final @Nonnull String expression)
    {
        Preconditions.checkNotNull(expression);
        Preconditions.checkNotNull(xpath);

        try
        {
            return (String) this.xpath.compile(expression).evaluate(document, XPathConstants.STRING);
        }
        catch (XPathExpressionException e)
        {
            throw new FluentXmlException(e);
        }
    }

    @Nonnull
    public Properties xpathsToProperties(final @Nonnull String... keyExpressions)
    {
        Preconditions.checkNotNull(keyExpressions);
        Preconditions.checkNotNull(xpath);

        Properties results = new Properties();

        for (int i = 0; i < keyExpressions.length; i = i + 2)
        {
            String key = keyExpressions[i];
            String expression = keyExpressions[i + 1];

            results.put(key, this.xpathToString(expression));
        }

        return results;
    }

    @Nonnull
    public Boolean exists()
    {
        return document != null;
    }

    @Nonnull
    public Boolean notExists()
    {
        return !exists();
    }

    public static class FluentXmlException extends RuntimeException
    {
        public FluentXmlException(final @Nonnull Throwable cause)
        {
            super(cause);
        }
    }
}
