package org.sleepydragon.drumsk.metronome;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

/**
 * Stores information about a metronome click.
 */
abstract class Click {

    @NonNull
    protected final Context mContext;

    public Click(@NonNull final Context context) {
        mContext = context;
    }

    @WorkerThread
    public abstract void click();

    @WorkerThread
    public abstract void open();

    @WorkerThread
    public abstract void close();

}
