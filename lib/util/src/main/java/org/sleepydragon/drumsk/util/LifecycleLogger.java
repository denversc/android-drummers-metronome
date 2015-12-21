package org.sleepydragon.drumsk.util;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
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

    public void onBind(final Intent intent) {
        log("onBind() intent=%s", intent);
    }

    public void onUnbind(final Intent intent) {
        log("onUnbind() intent=%s", intent);
    }

    public void onRebind(final Intent intent) {
        log("onRebind() intent=%s", intent);
    }

    public void onStartCommand(final Intent intent, final int flags, final int startId) {
        log("onStartCommand() intent=%s flags=%s startId=%s", intent, flags, startId);
    }

    public void onServiceConnected(final ComponentName name, final IBinder service) {
        log("onServiceConnected() name=%s service=%s", name, service);
    }

    public void onServiceDisconnected(final ComponentName name) {
        log("onServiceDisconnected() name=%s", name);
    }

}
