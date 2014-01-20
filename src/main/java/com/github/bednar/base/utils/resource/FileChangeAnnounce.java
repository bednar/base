package com.github.bednar.base.utils.resource;

import javax.annotation.Nonnull;

/**
 * @author Jakub Bednář (13/01/2014 19:14)
 */
public interface FileChangeAnnounce
{
    /**
     * Resource was modified.
     *
     * @param changeContext modified file
     */
    void modified(@Nonnull final FileChangeContext changeContext);
}
