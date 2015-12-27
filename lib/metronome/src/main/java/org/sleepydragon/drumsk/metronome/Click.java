package org.sleepydragon.drumsk.metronome;

import android.content.Context;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Stores information about a metronome click.
 */
class Click {

    @NonNull
    private final Context mContext;

    private final long mPeriodMillis;

    private Vibrator mVibrator;
    private long mNextTime;

    public Click(@NonNull final Context context, final long periodMillis) {
        mContext = context;
        mPeriodMillis = periodMillis;
        mNextTime = SystemClock.uptimeMillis();
    }

    public long click() {
        final long nextTime = calculateNextTime();
        mNextTime = nextTime;

        {
            final Vibrator vibrator = getVibrator();
            if (vibrator != null) {
                vibrator.vibrate(50);
            }
        }

        return nextTime;
    }

    @Nullable
    private Vibrator getVibrator() {
        if (mVibrator == null) {
            mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        }
        return mVibrator;
    }

    public long calculateNextTime() {
        final long curTime = SystemClock.uptimeMillis();
        final long idealNextTime = mNextTime + mPeriodMillis;
        if (idealNextTime >= curTime) {
            return idealNextTime;
        } else {
            return curTime + mPeriodMillis;
        }
    }


}
