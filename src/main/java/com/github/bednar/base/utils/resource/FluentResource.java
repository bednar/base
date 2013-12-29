package com.github.bednar.base.utils.resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

/**
 * @author Jakub Bednář (19/09/2013 5:40 PM)
 */
public final class FluentResource implements AutoCloseable
{
    private final String path;

    private InputStream stream;

    private FluentResource(@Nonnull final String path)
    {
        this.path = path;
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

        return new FluentResource(url.toExternalForm());
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
        if (stream == null)
        {
            if (path.startsWith("file:"))
            {
                try
                {
                    stream = new FileInputStream(path.replaceFirst("file:", ""));
                }
                catch (FileNotFoundException e)
                {
                    throw FluentException.internal(e);
                }
            }
            else
            {
                stream = this.getClass().getResourceAsStream(path);
            }
        }

        return stream;
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
        return path;
    }

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
}
