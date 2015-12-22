package org.sleepydragon.drumsk.util;

import android.os.HandlerThread;
import android.support.annotation.NonNull;

import static org.sleepydragon.drumsk.util.Assert.assertNotNull;

public class HandlerThreadQuitRunnable implements Runnable {

    @NonNull
    private final HandlerThread mHandlerThread;

    public HandlerThreadQuitRunnable(@NonNull final HandlerThread handlerThread) {
        mHandlerThread = assertNotNull(handlerThread);
    }

    @Override
    public void run() {
        mHandlerThread.quit();
    }

}
