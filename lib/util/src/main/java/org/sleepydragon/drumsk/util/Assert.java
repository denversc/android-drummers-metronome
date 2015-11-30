package org.sleepydragon.drumsk.util;

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

}
