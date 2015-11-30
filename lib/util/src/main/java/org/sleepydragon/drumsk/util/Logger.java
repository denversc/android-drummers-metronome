package org.sleepydragon.drumsk.util;

import android.support.annotation.NonNull;

import static org.sleepydragon.drumsk.util.Assert.assertNotNull;

public class Logger {

    private static String sTag;

    @NonNull
    private final String mSubTag;

    public Logger(@NonNull final String subTag) {
        mSubTag = assertNotNull(subTag);
    }

    public Logger(@NonNull final Object subTag) {
        this(getSubTag(subTag));
    }

    public static void initialize(@NonNull final String tag) {
        sTag = tag;
    }

    @NonNull
    private static String getSubTag(@NonNull final Object object) {
        return getSubTag(object.getClass());
    }

    @NonNull
    private static String getSubTag(@NonNull final Class<?> cls) {
        return cls.getSimpleName();
    }

}
