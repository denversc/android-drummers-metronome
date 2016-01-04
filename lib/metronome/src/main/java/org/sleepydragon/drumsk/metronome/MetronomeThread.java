package org.sleepydragon.drumsk.metronome;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.sleepydragon.drumsk.util.Logger;

import static org.sleepydragon.drumsk.util.ObjectUtil.nullSafeEquals;

class MetronomeThread extends Thread {

    public static final int BPM_MIN = 1;
    public static final int BPM_MAX = 1000;

    @NonNull
    private final Logger mLogger;
    @NonNull
    private final Object mLock;
    @NonNull
    private final AudioClick mAudioClick;
    @NonNull
    private final VibrateClick mVibrateClick;

    private volatile MetronomeConfig mConfig;
    private volatile boolean mClosed;
    private volatile boolean mIsClicking;

    public MetronomeThread(@NonNull final Context context) {
        mLogger = new Logger(this);
        mLock = new Object();
        mAudioClick = new AudioClick(context);
        mVibrateClick = new VibrateClick(context);
        setName("MetronomeThread");
        setPriority(MAX_PRIORITY);
    }

    public void close() {
        synchronized (mLock) {
            stopClick();
            mClosed = true;
            mLock.notifyAll();
        }
        interrupt();
    }

    public boolean isClosed() {
        return mClosed;
    }

    @Nullable
    public MetronomeConfig getMetronomeConfig() {
        return mConfig;
    }

    public boolean setMetronomeConfig(@Nullable final MetronomeConfig newConfig) {
        synchronized (mLock) {
            final MetronomeConfig oldConfig = mConfig;
            if (nullSafeEquals(oldConfig, newConfig)) {
                return false;
            } else {
                if (newConfig != null) {
                    if (newConfig.bpm < BPM_MIN) {
                        throw new IllegalArgumentException("BPM too small: " + newConfig.bpm);
                    } else if (newConfig.bpm > BPM_MAX) {
                        throw new IllegalArgumentException("BPM too large: " + newConfig.bpm);
                    }
                }
                mConfig = newConfig;
                return (oldConfig == null || newConfig == null || oldConfig.bpm != newConfig.bpm);
            }
        }
    }

    public void startClick() {
        setClickStarted(true);
    }

    public void stopClick() {
        setClickStarted(false);
    }

    private void setClickStarted(final boolean started) {
        synchronized (mLock) {
            if (mClosed) {
                throw new IllegalStateException("close() has been invoked");
            }
            mIsClicking = started;
            mLock.notifyAll();
        }
    }

    public boolean isClickStarted() {
        return mIsClicking;
    }

    @Override
    public void run() {
        mLogger.d("starting");
        mAudioClick.open();
        mVibrateClick.open();
        try {
            loop();
        } catch (InterruptedException e) {
            mLogger.e(e, "loop() failed");
        } finally {
            mLogger.d("exiting");
            mVibrateClick.close();
            mAudioClick.close();
        }
    }

    private void loop() throws InterruptedException {
        while (true) {
            synchronized (mLock) {
                if (isClosed()) {
                    break;
                }
                if (!isClickStarted()) {
                    mLock.wait();
                }
                if (isClosed()) {
                    break;
                }
            }

            final long startTime = SystemClock.uptimeMillis();
            click();

            synchronized (mLock) {
                if (isClosed()) {
                    break;
                }
                if (mConfig != null) {
                    final long periodMillis = 60_000L / mConfig.bpm;
                    final long nextClickTime = startTime + periodMillis;
                    final long curTime = SystemClock.uptimeMillis();
                    final long waitTime = nextClickTime - curTime;
                    if (waitTime > 0) {
                        mLock.wait(waitTime);
                    }
                }
            }
        }
    }

    private void click() {
        final MetronomeConfig config = getMetronomeConfig();
        if (config == null) {
            return;
        }

        if (config.audioEnabled) {
            mAudioClick.click();
        }
        if (config.vibrateEnabled) {
            mVibrateClick.click();
        }
    }

}
