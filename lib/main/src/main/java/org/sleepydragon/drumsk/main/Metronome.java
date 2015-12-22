package org.sleepydragon.drumsk.main;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import org.sleepydragon.drumsk.util.AnyThread;
import org.sleepydragon.drumsk.util.HandlerThreadQuitRunnable;
import org.sleepydragon.drumsk.util.Logger;

import static android.os.Process.THREAD_PRIORITY_FOREGROUND;
import static org.sleepydragon.drumsk.util.Assert.assertFalse;
import static org.sleepydragon.drumsk.util.Assert.assertMainThread;
import static org.sleepydragon.drumsk.util.Assert.assertNotNull;
import static org.sleepydragon.drumsk.util.Assert.assertTrue;

public class Metronome {

    public static final int BPM_MIN = 1;
    public static final int BPM_MAX = 300;

    @NonNull
    final Logger mLogger;

    private ClickHandler mClickHandler;
    private HandlerThread mClickHandlerThread;
    private boolean mCreated;
    private ClickInfo mClickInfo;

    Vibrator mVibrator;

    public Metronome() {
        mLogger = new Logger(this);
    }

    @MainThread
    public void onCreate(@NonNull final Context context) {
        assertMainThread();
        assertFalse(mCreated);
        assertNotNull(context);
        mCreated = true;
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        mClickHandlerThread = new HandlerThread("Metronome", THREAD_PRIORITY_FOREGROUND);
        mClickHandlerThread.start();
        final Looper clickLooper = mClickHandlerThread.getLooper();
        mClickHandler = new ClickHandler(clickLooper);
    }

    @MainThread
    public void onDestroy() {
        assertMainThread();
        assertTrue(mCreated);
        mCreated = false;
        mClickHandler.sendEmptyMessage(ClickHandler.MSG_CLOSE);
        mClickHandler.post(new HandlerThreadQuitRunnable(mClickHandlerThread));
    }

    @MainThread
    public void start(final int bpm) {
        assertMainThread();
        if (bpm < BPM_MIN || bpm > BPM_MAX) {
            throw new IllegalArgumentException("invalid bpm: " + bpm);
        }

        stop();

        final long periodMillis = 60_000 / bpm;
        final ClickInfo clickInfo = new ClickInfo(bpm, periodMillis);
        clickInfo.nextTime = SystemClock.uptimeMillis();

        final Message message = mClickHandler.obtainMessage();
        message.what = ClickHandler.MSG_START_CLICK;
        message.obj = clickInfo;
        message.sendToTarget();

        mClickInfo = clickInfo;
    }

    @MainThread
    public void stop() {
        assertMainThread();
        mClickHandler.sendEmptyMessage(ClickHandler.MSG_STOP_CLICK);
        mClickInfo = null;
    }

    @AnyThread
    public boolean isStarted() {
        return mClickHandler.isStarted();
    }

    @AnyThread
    public Integer getBpm() {
        final ClickInfo clickInfo = mClickInfo;
        return (clickInfo == null) ? null : clickInfo.bpm;
    }

    private class ClickHandler extends Handler {

        public static final int MSG_CLOSE = 1;
        public static final int MSG_START_CLICK = 10;
        public static final int MSG_STOP_CLICK = 11;
        public static final int MSG_CLICK = 12;

        private boolean mClosed;
        private volatile ClickInfo mCurClick;

        public ClickHandler(@NonNull final Looper looper) {
            super(looper);
        }

        @WorkerThread
        @Override
        public void handleMessage(final Message msg) {
            if (mClosed) {
                return;
            } else if (msg.what == MSG_CLOSE) {
                mClosed = true;
                removeMessages(MSG_START_CLICK);
                removeMessages(MSG_STOP_CLICK);
                removeMessages(MSG_CLICK);
                return;
            }

            switch (msg.what) {
                case MSG_START_CLICK: {
                    final ClickInfo clickInfo = (ClickInfo) msg.obj;
                    mClickInfo = clickInfo;
                    removeMessages(MSG_CLICK);
                    mLogger.i("Starting metronome at %d BPM", clickInfo.bpm);
                    sendEmptyMessage(MSG_CLICK);
                    break;
                }
                case MSG_STOP_CLICK: {
                    if (mClickInfo != null) {
                        mLogger.i("Stopping metronome at %d BPM", mClickInfo.bpm);
                    }
                    removeMessages(MSG_CLICK);
                    mClickInfo = null;
                    break;
                }
                case MSG_CLICK: {
                    final long nextTime = mClickInfo.calculateNextTime();
                    mClickInfo.nextTime = nextTime;
                    sendEmptyMessageAtTime(MSG_CLICK, nextTime);
                    mVibrator.vibrate(40);
                    break;
                }
                default:
                    throw new IllegalArgumentException("unknown message: " + msg);
            }
        }

        @AnyThread
        public boolean isStarted() {
            return (mClickInfo != null);
        }

    }

    private static class ClickInfo {

        public final int bpm;
        public final long periodMillis;
        public long nextTime;

        public ClickInfo(final int bpm, final long periodMillis) {
            this.bpm = bpm;
            this.periodMillis = periodMillis;
        }

        public long calculateNextTime() {
            final long curTime = SystemClock.uptimeMillis();
            final long idealNextTime = nextTime + periodMillis;
            if (idealNextTime >= curTime) {
                return idealNextTime;
            } else {
                return curTime + periodMillis;
            }
        }

    }

}
