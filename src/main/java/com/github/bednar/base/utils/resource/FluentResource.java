package com.github.bednar.base.utils.resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.regex.Pattern;

import com.github.bednar.base.utils.collection.ListAutoCloseable;
import com.github.bednar.base.utils.reflection.FluentReflection;
import com.github.bednar.base.utils.throwable.FluentException;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Supported prefix for {@link FluentResource#byPath(String)}:
 *
 * <ul>
 *     <li>without  - /some/path/to/file.xml - Java resource on classpath</li>
 *     <li>file     - file:///path/to/file/on/filesystem.xml - File anywhere on filesystem</li>
 *     <li>file:jar - file:jar:commons.jar!com/apache/settings.properties - File directly from specific JAR</li>
 * </ul>
 *
 * @author Jakub Bednář (19/09/2013 5:40 PM)
 */
public final class FluentResource implements AutoCloseable
{
    private static final Logger LOG = LoggerFactory.getLogger(FluentResource.class);

    private static final Pattern DIRECTORY_PATTERN = Pattern
            .compile(String.format("^(.*)%s(.*)$", File.separatorChar));

    private static final Pattern PROTOCOL_PATTERN = Pattern
            .compile("^([a-zA-Z]+:)(.*)$");

    private final URL url;

    private InputStream stream;

    private FluentResource(@Nullable final URL url)
    {
        this.url = url;
    }

    private FluentResource(@Nonnull final String path)
    {
        String resource = path.startsWith(File.separator) ? path : File.separator + path;

        this.url = this.getClass().getResource(resource);
    }

    @Nonnull
    public static FluentResource byPath(@Nonnull final String path)
    {
        Preconditions.checkNotNull(path);

        //start with protocol 'protocol:path/next' ? => use URL
        if (PROTOCOL_PATTERN.matcher(path).matches())
        {
            try
            {
                return byURL(new URL(path));
            }
            catch (MalformedURLException e)
            {
                throw FluentException.internal(e);
            }
        }
        else
        {
            return new FluentResource(path);
        }
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
    public static ListAutoCloseable<FluentResource> byPattern(@Nonnull final String pkcg, @Nonnull final String pattern)
    {
        return byPattern(pkcg, Pattern.compile(pattern));
    }


    @Nonnull
    public static ListAutoCloseable<FluentResource> byPattern(@Nonnull final Pattern pattern)
    {
        return byPattern(FluentReflection.forBasePackage(), pattern);
    }

    @Nonnull
    public static ListAutoCloseable<FluentResource> byPattern(@Nonnull final String pkcg, @Nonnull final Pattern pattern)
    {
        return byPattern(FluentReflection.forPackage(pkcg), pattern);
    }

    @Nonnull
    public static ListAutoCloseable<FluentResource> byPattern(@Nonnull final FluentReflection reflection,
                                                              @Nonnull final Pattern pattern)
    {
        Set<String> paths = reflection.getResources(pattern);

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

    @Nullable
    public URL asURL()
    {
        return url;
    }

    @Nullable
    public Path asPath()
    {
        if (url == null)
        {
            return null;
        }
        else
        {
            try
            {
                return Paths.get(url.toURI());
            }
            catch (URISyntaxException e)
            {
                throw FluentException.internal(e);
            }
        }
    }

    @Nonnull
    public Boolean isReloadable()
    {
        //noinspection SimplifiableIfStatement
        if (url == null)
        {
            return false;
        }
        else
        {
            return url.getProtocol().equals("file");
        }
    }

    @Nonnull
    public Boolean isChangeable()
    {
        //noinspection SimplifiableIfStatement
        if (url == null)
        {
            return false;
        }
        else
        {
            return url.getProtocol().equals("file");
        }
    }

    @Nonnull
    public Boolean isDeletable()
    {
        //noinspection SimplifiableIfStatement
        if (url == null)
        {
            return false;
        }
        else
        {
            return url.getProtocol().equals("file");
        }
    }

    @Nonnull
    public String path()
    {
        return url != null ? url.toExternalForm() : "";
    }

    @Nonnull
    public String directory()
    {
        String path = path();

        return DIRECTORY_PATTERN.matcher(path).replaceAll("$1") + File.separatorChar;
    }

    @Nonnull
    public FluentResource update(@Nullable final String content)
    {
        if (!isChangeable())
        {
            LOG.warn("[cannot-update][{}]", this);
        }
        else
        {
            try
            {
                //noinspection ConstantConditions
                FileUtils.write(asPath().toFile(), content, "UTF-8");

                this.stream = null;
            }
            catch (IOException e)
            {
                throw FluentException.internal(e);
            }
        }

        return this;
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
