package org.sleepydragon.drumsk.devutil;

import android.content.Context;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.StrictMode.VmPolicy;
import android.support.annotation.NonNull;

import org.sleepydragon.drumsk.util.Logger;

import java.util.concurrent.atomic.AtomicReference;

import static org.sleepydragon.drumsk.util.Assert.assertNotNull;

public class DevUtils {

    private static final AtomicReference<DevUtils> sInstance = new AtomicReference<>();

    @NonNull
    private final Logger mLogger;
    @NonNull
    private final Context mContext;

    private DevUtils(@NonNull final Context context) {
        mContext = assertNotNull(context);
        mLogger = new Logger(this);
    }

    public static void initialize(@NonNull final Context context) {
        assertNotNull(context);
        final DevUtils devUtils = new DevUtils(context);
        if (!sInstance.compareAndSet(null, devUtils)) {
            throw new IllegalStateException("initialize already invoked");
        }
        devUtils.initialize();
    }

    private void initialize() {
        initializeStrictMode();
    }

    private void initializeStrictMode() {
        mLogger.i("Setting StrictMode policy");
        setThreadPolicy();
        setVmPolicy();
    }

    private static void setThreadPolicy() {
        final ThreadPolicy oldPolicy = StrictMode.getThreadPolicy();
        final ThreadPolicy.Builder builder = new ThreadPolicy.Builder(oldPolicy);
        builder.detectAll();
        builder.penaltyDropBox();
        builder.penaltyLog();
        builder.penaltyDeath();
        final ThreadPolicy newPolicy = builder.build();
        StrictMode.setThreadPolicy(newPolicy);
    }

    private static void setVmPolicy() {
        final VmPolicy oldPolicy = StrictMode.getVmPolicy();
        final VmPolicy.Builder builder = new VmPolicy.Builder(oldPolicy);
        builder.detectAll();
        builder.penaltyDropBox();
        builder.penaltyLog();
        final VmPolicy newPolicy = builder.build();
        StrictMode.setVmPolicy(newPolicy);
    }

}
