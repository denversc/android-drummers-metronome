package org.sleepydragon.drumsk.metronome;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.sleepydragon.drumsk.util.AnyThread;

import static org.sleepydragon.drumsk.util.Assert.assertMainThread;
import static org.sleepydragon.drumsk.util.Assert.assertNotNull;
import static org.sleepydragon.drumsk.util.Assert.assertNull;

class Metronome {

    private MetronomeThread mMetronomeThread;

    @MainThread
    public void onCreate(@NonNull final Context context) {
        assertMainThread();
        assertNull(mMetronomeThread);
        assertNotNull(context);

        final Context appContext = context.getApplicationContext();
        mMetronomeThread = new MetronomeThread(appContext);
        mMetronomeThread.start();
    }

    @MainThread
    public void onDestroy() {
        assertMainThread();
        assertNotNull(mMetronomeThread);

        mMetronomeThread.close();
        mMetronomeThread = null;
    }

    @AnyThread
    public void start(@NonNull final MetronomeConfig config) {
        assertNotNull(config);
        mMetronomeThread.setMetronomeConfig(config);
        mMetronomeThread.startClick();
    }

    @AnyThread
    public void stop() {
        mMetronomeThread.stopClick();
    }

    @AnyThread
    public boolean isStarted() {
        return mMetronomeThread.isClickStarted();
    }

    @AnyThread
    @Nullable
    public MetronomeConfig getMetronomeConfig() {
        return mMetronomeThread.getMetronomeConfig();
    }

    @AnyThread
    public boolean setMetronomeConfig(@NonNull final MetronomeConfig config) {
        return mMetronomeThread.setMetronomeConfig(config);
    }

}
