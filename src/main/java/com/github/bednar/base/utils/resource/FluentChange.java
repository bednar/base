package com.github.bednar.base.utils.resource;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import com.github.bednar.base.utils.throwable.FluentException;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jakub Bednář (13/01/2014 18:15)
 */
public final class FluentChange
{
    private static final Logger LOG = LoggerFactory.getLogger(FluentChange.class);

    private final Set<Path> paths;
    private final FileChangeAnnounce announce;
    private final Map<WatchKey, Path> keys = Maps.newHashMap();

    private WatchService watchService;

    private FluentChange(@Nonnull final FileChangeAnnounce announce, @Nonnull final Set<Path> paths)
    {
        this.announce = announce;
        this.paths = paths;
    }

    @Nonnull
    public static FluentChange byResource(@Nonnull final FileChangeAnnounce announce, @Nonnull final FluentResource... resources)
    {
        Set<Path> paths = Sets.newHashSet();

        for (FluentResource resource : resources)
        {
            URL url = resource.asURL();
            if (url == null)
            {
                throw FluentException.internal("[url-is-null]");
            }

            try
            {
                paths.add(Paths.get(url.toURI()));
            }
            catch (URISyntaxException e)
            {
                throw FluentException.internal(e);
            }
        }

        return new FluentChange(announce, paths);
    }

    public void watchAssync()
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
    }

    private void watch() throws IOException, InterruptedException
    {
        LOG.info("[start-watch]");

        watchService = FileSystems.getDefault().newWatchService();

        for (Path path : paths)
        {
            registerPath(path);
        }

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

                    for (Path path : paths)
                    {
                        if (path.equals(changedFile))
                        {
                            announce.modified();

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

    private void registerPath(@Nonnull final Path path) throws IOException
    {
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
