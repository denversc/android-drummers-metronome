package org.sleepydragon.drumsk.util;

import android.os.Looper;
import android.support.annotation.NonNull;

public class Assert {

    private Assert() {
    }

    @NonNull
    public static <T> T assertNotNull(@NonNull final T object) {
        //noinspection ConstantConditions
        if (object == null) {
            throw new AssertionError("object is null");
        }
        return object;
    }

    public static void assertTrue(final boolean value) {
        if (!value) {
            throw new AssertionError("value is false");
        }
    }

    public static void assertFalse(final boolean value) {
        if (value) {
            throw new AssertionError("value is true");
        }
    }

    public static void assertMainThread() {
        final Looper mainLooper = Looper.getMainLooper();
        final Looper myLooper = Looper.myLooper();
        if (myLooper != mainLooper) {
            throw new AssertionError("method must be invoked on main event thread");
        }
    }

}
