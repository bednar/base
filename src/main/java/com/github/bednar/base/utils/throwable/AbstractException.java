package com.github.bednar.base.utils.throwable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Jakub Bednář (29/12/2013 14:24)
 */
public abstract class AbstractException extends Throwable
{
    private final String uiMessage;
    private final Type type;

    private final Throwable reason;

    public enum Type
    {
        /**
         * Detail not present to user.
         */
        INTERNAL
    }

    protected AbstractException(@Nullable final String uiMessage, @Nullable final Type type, @Nullable final Throwable reason)
    {
        this.uiMessage = uiMessage;
        this.type = type;

        this.reason = reason;
    }

    @Nonnull
    public String getUiMessage()
    {
        return uiMessage != null ? uiMessage : "";
    }

    @Nonnull
    public Type getType()
    {
        return type != null ? type : Type.INTERNAL;
    }

    @Nullable
    public Throwable getReason()
    {
        return reason;
    }
}
