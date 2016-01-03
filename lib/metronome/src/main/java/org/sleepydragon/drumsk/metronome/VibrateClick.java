package org.sleepydragon.drumsk.metronome;

import android.content.Context;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

public class VibrateClick extends Click {

    private Vibrator mVibrator;

    public VibrateClick(@NonNull final Context context) {
        super(context);
    }

    @Override
    @WorkerThread
    public void open() {
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    @WorkerThread
    public void close() {
        mVibrator = null;
    }

    @Override
    @WorkerThread
    public void click() {
        final Vibrator vibrator = mVibrator;
        if (vibrator != null) {
            vibrator.vibrate(30);
        }
    }

}
