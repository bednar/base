package com.github.bednar.base.utils.throwable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

/**
 * @author Jakub Bednář (29/12/2013 14:25)
 */
public final class FluentException extends AbstractException
{
    private FluentException(@Nullable final String uiMessage, @Nullable final Type type, @Nullable final Throwable reason)
    {
        super(uiMessage, type, reason);
    }

    @Nonnull
    public static FluentException internal()
    {
        return internal("");
    }

    @Nonnull
    public static FluentException internal(@Nullable final String uiMessage)
    {
        return internal(uiMessage, null);
    }

    @Nonnull
    public static FluentException internal(@Nullable final Throwable reason)
    {
        return internal("", reason);
    }

    @Nonnull
    public static FluentException internal(@Nullable final String uiMessage, @Nullable final Throwable reason)
    {
        return new FluentException(uiMessage, Type.INTERNAL, reason);
    }

    @Nonnull
    public static FluentException internal(@Nullable final String uiMessage, @Nullable final Object... reason)
    {
        FormattingTuple formattingTuple = MessageFormatter.arrayFormat(uiMessage, reason);

        return new FluentException(formattingTuple.getMessage(), Type.INTERNAL, formattingTuple.getThrowable());
    }
}
