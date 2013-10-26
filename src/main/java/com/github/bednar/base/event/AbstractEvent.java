package com.github.bednar.base.event;

import javax.annotation.Nonnull;

/**
 * Base event.
 *
 * @author Jakub Bednář (19/08/2013 11:13 AM)
 */
public abstract class AbstractEvent<T>
{
    /**
     * Success response.
     *
     * @param value success value
     */
    public void success(final @Nonnull T value)
    {
    }

    /**
     * Error response.
     *
     * @param error reason
     */
    public void fail(final @Nonnull Throwable error)
    {
        throw new RuntimeException(error);
    }
}
