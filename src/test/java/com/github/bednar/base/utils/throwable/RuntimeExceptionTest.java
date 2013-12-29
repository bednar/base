package com.github.bednar.base.utils.throwable;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jakub Bednář (29/12/2013 14:31)
 */
@SuppressWarnings({"ThrowableResultOfMethodCallIgnored", "ThrowableInstanceNeverThrown"})
public class RuntimeExceptionTest
{
    @Test
    public void internalTypeValue()
    {
        RuntimeException internal = RuntimeException.internal();

        Assert.assertEquals(AbstractException.Type.INTERNAL, internal.getType());
    }

    @Test
    public void internalUiMessageValue()
    {
        RuntimeException internal = RuntimeException.internal("bye bye!");

        Assert.assertEquals("bye bye!", internal.getUiMessage());
    }

    @Test
    public void internalReasonValue()
    {
        NullPointerException reason = new NullPointerException();

        RuntimeException internal = RuntimeException.internal(reason);

        Assert.assertEquals(reason, internal.getReason());
    }

    @Test
    public void defaultInternalUiMessageNotNull()
    {
        RuntimeException internal = RuntimeException.internal();

        Assert.assertEquals("", internal.getUiMessage());
    }

    @Test
    public void defaultInternalReasonNull()
    {
        RuntimeException internal = RuntimeException.internal();

        Assert.assertNull(internal.getReason());
    }
}
