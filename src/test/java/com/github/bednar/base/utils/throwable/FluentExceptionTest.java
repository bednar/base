package com.github.bednar.base.utils.throwable;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jakub Bednář (29/12/2013 14:31)
 */
@SuppressWarnings({"ThrowableResultOfMethodCallIgnored", "ThrowableInstanceNeverThrown"})
public class FluentExceptionTest
{
    @Test
    public void internalTypeValue()
    {
        FluentException internal = FluentException.internal();

        Assert.assertEquals(AbstractException.Type.INTERNAL, internal.getType());
    }

    @Test
    public void internalUiMessageValue()
    {
        FluentException internal = FluentException.internal("bye bye!");

        Assert.assertEquals("bye bye!", internal.getUiMessage());
    }

    @Test
    public void internalReasonValue()
    {
        NullPointerException reason = new NullPointerException();

        FluentException internal = FluentException.internal(reason);

        Assert.assertEquals(reason, internal.getReason());
    }

    @Test
    public void defaultInternalUiMessageNotNull()
    {
        FluentException internal = FluentException.internal();

        Assert.assertEquals("", internal.getUiMessage());
    }

    @Test
    public void defaultInternalReasonNull()
    {
        FluentException internal = FluentException.internal();

        Assert.assertNull(internal.getReason());
    }

    @Test
    public void formatting()
    {
        FluentException internal = FluentException.internal("{} - {}", 1, 2);

        Assert.assertNull(internal.getReason());
        Assert.assertEquals("1 - 2", internal.getUiMessage());
    }
}
