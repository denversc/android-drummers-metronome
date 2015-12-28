package org.sleepydragon.drumsk.metronome;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

/**
 * Stores information about a metronome click.
 */
abstract class Click {

    @NonNull
    protected final Context mContext;

    private long mPeriodMillis;
    private long mNextTime;

    public Click(@NonNull final Context context) {
        mContext = context;
        mNextTime = SystemClock.uptimeMillis();
    }

    public void setNextTime(final long nextTime) {
        mNextTime = nextTime;
    }

    public long getNextTime() {
        return mNextTime;
    }

    public void setPeriodMillis(final long periodMillis) {
        mPeriodMillis = periodMillis;
    }

    public long getPeriodMillis() {
        return mPeriodMillis;
    }

    private long calculateNextTime() {
        final long curTime = SystemClock.uptimeMillis();
        final long idealNextTime = mNextTime + mPeriodMillis;
        if (idealNextTime >= curTime) {
            return idealNextTime;
        } else {
            return curTime + mPeriodMillis;
        }
    }

    private long updateNextTime() {
        final long nextTime = calculateNextTime();
        setNextTime(nextTime);
        return nextTime;
    }

    @WorkerThread
    public final long click() {
        final long nextTime = updateNextTime();
        doClick();
        return nextTime;
    }

    @WorkerThread
    public abstract void doClick();

    @WorkerThread
    public abstract void open();

    @WorkerThread
    public abstract void close();

}
