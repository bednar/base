package com.github.bednar.base.utils.resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Set;
import java.util.regex.Pattern;

import com.github.bednar.base.utils.collection.ListAutoCloseable;
import com.github.bednar.base.utils.reflection.FluentReflection;
import com.github.bednar.base.utils.throwable.FluentException;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jakub Bednář (19/09/2013 5:40 PM)
 */
public final class FluentResource implements AutoCloseable
{
    private static final Logger LOG = LoggerFactory.getLogger(FluentResource.class);

    private final URL url;

    private InputStream stream;

    private FluentResource(@Nullable final URL url)
    {
        this.url = url;
    }

    private FluentResource(@Nonnull final String path)
    {
        this.url = this.getClass().getResource(path);
    }

    @Nonnull
    public static FluentResource byPath(@Nonnull final String path)
    {
        Preconditions.checkNotNull(path);

        String resourcePath = path.startsWith(File.separator) ? path : File.separator + path;

        return new FluentResource(resourcePath);
    }

    @Nonnull
    public static FluentResource byURL(@Nonnull final URL url)
    {
        Preconditions.checkNotNull(url);

        return new FluentResource(url);
    }

    @Nonnull
    public static ListAutoCloseable<FluentResource> byPattern(@Nonnull final String pattern)
    {
        return byPattern(Pattern.compile(pattern));
    }

    @Nonnull
    public static ListAutoCloseable<FluentResource> byPattern(@Nonnull final Pattern pattern)
    {
        Set<String> paths = FluentReflection
                .forBasePackage()
                .getResources(pattern);

        //noinspection NullableProblems
        return FluentIterable.from(paths).transform(new Function<String, FluentResource>()
        {
            @Nullable
            @Override
            public FluentResource apply(@Nonnull final String path)
            {
                return FluentResource.byPath(path);
            }
        }).copyInto(new ListAutoCloseable<FluentResource>());
    }

    @Nullable
    public InputStream asStream()
    {
        if (url != null && stream == null)
        {
            try
            {
                stream = url.openStream();
            }
            catch (IOException e)
            {
                LOG.error("[cannot-open-stream]", e);

                return null;
            }
        }

        return stream;
    }


    @Nonnull
    public Boolean exists()
    {
        return asStream() != null;
    }

    @Nonnull
    public String asString()
    {
        try
        {
            return IOUtils.toString(asStream());
        }
        catch (IOException e)
        {
            throw FluentException.internal(e);
        }
    }

    @Nonnull
    public Reader asReader()
    {
        return new InputStreamReader(asStream());
    }

    @Nonnull
    public String path()
    {
        return url != null ? url.toExternalForm() : "";
    }

    @Override
    public void close()
    {
        if (stream != null)
        {
            try
            {
                stream.close();
            }
            catch (IOException e)
            {
                throw FluentException.internal(e);
            }
        }
    }

    @Override
    public String toString()
    {
        return String.format("[%s][path=%s]", this.getClass(), path());
    }
}
