package org.sleepydragon.drumsk.metronome;

import android.content.Context;
import android.media.SoundPool;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import org.sleepydragon.drumsk.util.compat.SoundPoolCompat;

import java.util.concurrent.atomic.AtomicInteger;

public class AudioClick extends Click {

    @NonNull
    private final AtomicInteger mNextPriority;

    private int mSoundId;
    private SoundPool mSoundPool;

    public AudioClick(@NonNull final Context context) {
        super(context);
        mNextPriority = new AtomicInteger(0);
    }

    @Override
    @WorkerThread
    public void open() {
        mSoundPool = SoundPoolCompat.createSoundPool(2);
        mSoundId = mSoundPool.load(mContext, R.raw.click, 1);
    }

    @Override
    @WorkerThread
    public void close() {
        final SoundPool soundPool = mSoundPool;
        mSoundPool = null;
        if (soundPool != null) {
            soundPool.release();
        }
    }

    @Override
    @WorkerThread
    public void doClick() {
        final SoundPool soundPool = mSoundPool;
        if (soundPool != null) {
            final int priority = mNextPriority.getAndIncrement();
            soundPool.play(mSoundId, 1.0f, 1.0f, priority, 0, 1.0f);
        }
    }

}
