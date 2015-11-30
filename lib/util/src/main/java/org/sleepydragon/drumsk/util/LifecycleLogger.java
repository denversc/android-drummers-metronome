package org.sleepydragon.drumsk.util;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static org.sleepydragon.drumsk.util.Assert.assertNotNull;

public class LifecycleLogger {

    @NonNull
    private final Logger mLogger;

    public LifecycleLogger(@NonNull final Logger logger) {
        mLogger = assertNotNull(logger);
    }

    public void log(@NonNull final String message, @Nullable final Object... args) {
        mLogger.v(message, args);
    }

    public void onCreate() {
        log("onCreate()");
    }

    public void onCreate(final Bundle savedInstanceState) {
        log("onCreate() savedInstanceState=%s", savedInstanceState);
    }

    public void onDestroy() {
        log("onDestroy()");
    }

    public void onTerminate() {
        log("onTerminate()");
    }

    public void onResume() {
        log("onResume()");
    }

    public void onPause() {
        log("onPause()");
    }

    public void onStart() {
        log("onStart()");
    }

    public void onStop() {
        log("onStop()");
    }

}
