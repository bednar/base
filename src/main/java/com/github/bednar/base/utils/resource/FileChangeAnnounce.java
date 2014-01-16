package com.github.bednar.base.utils.resource;

import javax.annotation.Nonnull;
import java.nio.file.Path;

/**
 * @author Jakub Bednář (13/01/2014 19:14)
 */
public interface FileChangeAnnounce
{
    /**
     * Resource was modified.
     *
     * @param modified modified file
     */
    void modified(@Nonnull final Path modified);
}
