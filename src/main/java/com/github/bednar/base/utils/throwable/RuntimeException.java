package com.github.bednar.base.utils.throwable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Jakub Bednář (29/12/2013 14:25)
 */
public final class RuntimeException extends AbstractException
{
    private RuntimeException(@Nullable final String uiMessage, @Nullable final Type type, @Nullable final Throwable reason)
    {
        super(uiMessage, type, reason);
    }

    @Nonnull
    public static RuntimeException internal()
    {
        return internal("");
    }

    @Nonnull
    public static RuntimeException internal(@Nullable final String uiMessage)
    {
        return internal(uiMessage, null);
    }

    @Nonnull
    public static RuntimeException internal(@Nullable final Throwable reason)
    {
        return internal("", reason);
    }

    @Nonnull
    public static RuntimeException internal(@Nullable final String uiMessage, @Nullable final Throwable reason)
    {
        return new RuntimeException(uiMessage, Type.INTERNAL, reason);
    }
}
