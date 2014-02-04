package com.github.bednar.base.utils.resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Pattern;

import com.github.bednar.base.utils.throwable.FluentException;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jakub Bednář (19/01/2014 08:32)
 */
public final class FileChangeContext
{
    private static final Logger LOG = LoggerFactory.getLogger(FileChangeContext.class);

    private Map<String, Serializable> context = Maps.newHashMap();

    private final Path path;
    private final Pattern pattern;

    private FileChangeContext(@Nonnull final Path path)
    {
        this(path, Pattern.compile(path.toString()));
    }

    private FileChangeContext(@Nonnull final Path path, @Nonnull final Pattern pattern)
    {
        this.path = path;
        this.pattern = pattern;
    }

    @Nonnull
    public static FileChangeContext byResource(@Nonnull final FluentResource resource)
    {
        Path path = resource.asPath();
        if (path == null)
        {
            throw FluentException.internal("[resource-path-is-null][{}]", resource);
        }

        if (!resource.isReloadable())
        {
            throw FluentException.internal("[resource-is-not-reloadable][{}]", resource);
        }

        return byPath(path);
    }

    @Nonnull
    public static FileChangeContext byPath(@Nonnull final Path path)
    {
        //noinspection ConstantConditions
        Preconditions.checkArgument(path != null);

        return new FileChangeContext(path);
    }

    @Nonnull
    public static FileChangeContext byPathPattern(@Nonnull final Path path, @Nonnull final Pattern pattern)
    {
        //noinspection ConstantConditions
        Preconditions.checkArgument(path != null);
        //noinspection ConstantConditions
        Preconditions.checkArgument(pattern != null);

        return new FileChangeContext(path, pattern);
    }

    @Nonnull
    public FileChangeContext addContext(@Nonnull final String key, @Nullable final Serializable value)
    {
        //noinspection ConstantConditions
        Preconditions.checkArgument(key != null);

        context.put(key, value);

        return this;
    }

    @Nonnull
    public Map<String, Serializable> getContext()
    {
        return context;
    }

    @Nonnull
    public Path getPath()
    {
        return path;
    }

    @Nonnull
    public Pattern getPattern()
    {
        return pattern;
    }
}
