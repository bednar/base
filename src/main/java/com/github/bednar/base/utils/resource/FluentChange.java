package com.github.bednar.base.utils.resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import com.github.bednar.base.utils.throwable.FluentException;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jakub Bednář (13/01/2014 18:15)
 */
public final class FluentChange
{
    private static final Logger LOG = LoggerFactory.getLogger(FluentChange.class);

    private final Map<Pattern, FileChangeContext> paths = Maps.newHashMap();
    private final Map<WatchKey, Path> keys = Maps.newHashMap();
    private final FileChangeAnnounce announce;

    private final WatchService watchService;

    private FluentChange(@Nonnull final FileChangeAnnounce announce, @Nullable final FluentResource... resources)
    {
        this.announce = announce;
        try
        {
            this.watchService = FileSystems.getDefault().newWatchService();
        }
        catch (IOException e)
        {
            throw FluentException.internal(e);
        }

        addResources(resources);
    }

    @Nonnull
    public static FluentChange byResources(@Nonnull final FileChangeAnnounce announce, @Nullable final FluentResource... resources)
    {
        return new FluentChange(announce, resources);
    }

    @Nonnull
    public FluentChange watchAssync()
    {
        LOG.info("[start-assync-watch]");

        Executors
                .newCachedThreadPool()
                .execute(
                        new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                try
                                {
                                    watch();
                                }
                                catch (Exception e)
                                {
                                    throw FluentException.internal(e);
                                }
                            }
                        });

        return this;
    }

    @Nonnull
    public FluentChange addFileChangeContext(@Nullable final FileChangeContext... contexts)
    {
        if (contexts == null)
        {
            return this;
        }
        else
        {
            for (FileChangeContext context : contexts)
            {
                try
                {
                    registerPath(context);
                }
                catch (IOException e)
                {
                    throw FluentException.internal(e);
                }

            }
        }

        return this;
    }

    @Nonnull
    public FluentChange addResources(@Nullable final FluentResource... resources)
    {
        if (resources == null)
        {
            return this;
        }

        for (FluentResource resource : resources)
        {
            FileChangeContext context = FileChangeContext.byResource(resource);

            addFileChangeContext(context);
        }

        return this;
    }

    private void watch() throws IOException, InterruptedException
    {
        LOG.info("[start-watch]");

        while (true)
        {
            final WatchKey key = watchService.take();

            LOG.info("[taked-key][{}]", key);

            for (WatchEvent<?> event : key.pollEvents())
            {
                WatchEvent.Kind<?> kind = event.kind();
                Path context            = (Path) event.context();

                LOG.info("[watch-event][{}][{}]", kind, context);

                if (kind.equals(StandardWatchEventKinds.ENTRY_MODIFY))
                {
                    final Path changedFile = resolveFile(key, context);

                    for (Pattern pattern : paths.keySet())
                    {
                        if (pattern.matcher(changedFile.toString()).matches())
                        {
                            FileChangeContext changeContext = paths.get(pattern);

                            announce.modified(changeContext);

                            break;
                        }
                    }
                }

                if (kind.equals(StandardWatchEventKinds.OVERFLOW))
                {
                    break;
                }
            }

            if (!key.reset())
            {
                LOG.info("[key-is-not-valid][{}]", key);

                break;
            }
        }
    }

    private void registerPath(@Nonnull final FileChangeContext context) throws IOException
    {
        Path path       = context.getPath();
        Pattern pattern = context.getPattern();

        this.paths.put(pattern, context);

        if (Files.isDirectory(path))
        {
            registerDirectory(path);
        }
        else
        {
            registerDirectory(path.getParent());
        }
    }

    private void registerDirectory(@Nonnull final Path directory) throws IOException
    {
        WatchKey key = directory.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

        if (keys.containsKey(key))
        {
            LOG.info("[update-key][{}][{}]", directory, key);
        }
        else
        {
            LOG.info("[new-key][{}][{}]", directory, key);
        }

        keys.put(key, directory);
    }

    @Nonnull
    private Path resolveFile(@Nonnull final WatchKey key, @Nonnull final Path context)
    {
        return keys.get(key).resolve(context);
    }
}
