package org.sleepydragon.drumsk.main;

import android.content.Context;
import android.support.annotation.NonNull;

public class DevUtils {

    private DevUtils() {
    }

    public static void initialize(@NonNull final Context context) {
        org.sleepydragon.drumsk.devutil.DevUtils.initialize(context);
    }

}
