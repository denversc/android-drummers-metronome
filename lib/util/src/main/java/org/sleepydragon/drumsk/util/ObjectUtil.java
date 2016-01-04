package org.sleepydragon.drumsk.util;

import android.support.annotation.Nullable;

public class ObjectUtil {

    private ObjectUtil() {
    }

    public static boolean nullSafeEquals(@Nullable final Object o1, @Nullable final Object o2) {
        if (o1 == o2) {
            return true;
        } else if (o1 == null || o2 == null) {
            return false;
        } else {
            return o1.equals(o2);
        }
    }
}
