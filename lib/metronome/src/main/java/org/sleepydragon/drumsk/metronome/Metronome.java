package org.sleepydragon.drumsk.metronome;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import org.sleepydragon.drumsk.util.AnyThread;
import org.sleepydragon.drumsk.util.HandlerThreadQuitRunnable;

import static android.os.Process.THREAD_PRIORITY_FOREGROUND;
import static org.sleepydragon.drumsk.util.Assert.assertFalse;
import static org.sleepydragon.drumsk.util.Assert.assertMainThread;
import static org.sleepydragon.drumsk.util.Assert.assertNotNull;
import static org.sleepydragon.drumsk.util.Assert.assertTrue;

public class Metronome {

    public static final int BPM_MIN = 1;
    public static final int BPM_MAX = 1000;

    private ClickHandler mClickHandler;
    private HandlerThread mClickHandlerThread;
    private boolean mCreated;

    private Clicker mClicker;

    @MainThread
    public void onCreate(@NonNull final Context context) {
        assertMainThread();
        assertFalse(mCreated);
        assertNotNull(context);
        mCreated = true;

        final Context appContext = context.getApplicationContext();
        mClicker = new Clicker(appContext);

        mClickHandlerThread = new HandlerThread("MetronomeClick", THREAD_PRIORITY_FOREGROUND);
        mClickHandlerThread.start();
        mClickHandler = new ClickHandler(mClickHandlerThread.getLooper(), mClicker);

        mClickHandler.sendEmptyMessage(ClickHandler.MSG_OPEN);
    }

    @MainThread
    public void onDestroy() {
        assertMainThread();
        assertTrue(mCreated);
        mCreated = false;

        stop();

        mClickHandler.sendEmptyMessage(ClickHandler.MSG_CLOSE);
        mClickHandler.post(new HandlerThreadQuitRunnable(mClickHandlerThread));
    }

    @MainThread
    public void start(@NonNull final MetronomeConfig config) {
        assertMainThread();

        mClickHandler.removeMessages(ClickHandler.MSG_CLICK);

        mClicker.setStarted(true);
        mClicker.setConfig(config);

        mClickHandler.sendEmptyMessage(ClickHandler.MSG_CLICK);
    }

    @MainThread
    public void stop() {
        assertMainThread();
        mClicker.setStarted(false);
        mClickHandler.removeMessages(ClickHandler.MSG_CLICK);
    }

    @AnyThread
    public boolean isStarted() {
        return mClicker.isStarted();
    }

    @AnyThread
    @Nullable
    public MetronomeConfig getConfig() {
        return mClicker.getConfig();
    }

    @AnyThread
    public boolean setConfig(@NonNull final MetronomeConfig config) {
        return mClicker.setConfig(config);
    }

    private static class ClickHandler extends Handler {

        public static final int MSG_OPEN = 1;
        public static final int MSG_CLOSE = 2;
        public static final int MSG_CLICK = 3;

        @NonNull
        private final Clicker mClicker;

        public ClickHandler(@NonNull final Looper looper, @NonNull final Clicker clicker) {
            super(looper);
            mClicker = clicker;
        }

        @WorkerThread
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case MSG_CLICK: {
                    final long nextTime = mClicker.click();
                    sendEmptyMessageAtTime(MSG_CLICK, nextTime);
                    break;
                }
                case MSG_OPEN: {
                    mClicker.open();
                    break;
                }
                case MSG_CLOSE: {
                    mClicker.close();
                    break;
                }
                default:
                    throw new IllegalArgumentException("unknown message: " + msg);
            }
        }

    }

    static class Clicker {

        @NonNull
        private final AudioClick mAudioClick;
        @NonNull
        private final VibrateClick mVibrateClick;

        private volatile MetronomeConfig mConfig;
        private volatile boolean mStarted;
        private long mClickPeriodMillis;
        private boolean mNextClickTimeValid;
        private long mNextClickTime;

        public Clicker(@NonNull final Context context) {
            mAudioClick = new AudioClick(context);
            mVibrateClick = new VibrateClick(context);
        }

        @AnyThread
        public boolean setConfig(@NonNull final MetronomeConfig config) {
            if (config.bpm < BPM_MIN || config.bpm > BPM_MAX) {
                throw new IllegalArgumentException("invalid bpm: " + config.bpm);
            }

            final MetronomeConfig oldConfig = mConfig;
            mConfig = assertNotNull(config);

            if (oldConfig == null || oldConfig.bpm != config.bpm) {
                mClickPeriodMillis = 60_000 / config.bpm;

                final boolean oldNextClickTimeValid = mNextClickTimeValid;
                mNextClickTimeValid = false;
                if (!oldNextClickTimeValid) {
                    return false;
                }

                final long oldNextClickTime = mNextClickTime;
                final long newNextClickTime = SystemClock.uptimeMillis() + mClickPeriodMillis;
                return (newNextClickTime < oldNextClickTime);
            } else {
                return false;
            }
        }

        @AnyThread
        @Nullable
        public MetronomeConfig getConfig() {
            return mConfig;
        }

        public boolean isStarted() {
            return mStarted;
        }

        public void setStarted(final boolean started) {
            mNextClickTimeValid = false;
            mStarted = started;
        }

        @WorkerThread
        public long click() {
            final long curTime = SystemClock.uptimeMillis();
            if (!mNextClickTimeValid) {
                mNextClickTime = curTime + mClickPeriodMillis;
                mNextClickTimeValid = true;
            } else {
                while (mNextClickTime <= curTime) {
                    mNextClickTime += mClickPeriodMillis;
                }
            }

            final MetronomeConfig config = mConfig;
            if (config != null) {
                if (config.audioEnabled) {
                    mAudioClick.click();
                }
                if (config.vibrateEnabled) {
                    mVibrateClick.click();
                }
            }

            return mNextClickTime;
        }

        @WorkerThread
        public void open() {
            mAudioClick.open();
            mVibrateClick.open();
        }

        @WorkerThread
        public void close() {
            mAudioClick.close();
            mVibrateClick.close();
        }

    }

}
