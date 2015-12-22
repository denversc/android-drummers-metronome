package org.sleepydragon.drumsk.main;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
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
    public static final int BPM_MAX = 300;

    private ClickHandler mClickHandler;
    private HandlerThread mClickHandlerThread;
    private boolean mCreated;

    Vibrator mVibrator;

    public Metronome() {
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

    @AnyThread
    public void start(final int bpm) {
        if (bpm < BPM_MIN || bpm > BPM_MAX) {
            throw new IllegalArgumentException("invalid bpm: " + bpm);
        }

        final int periodMillis = 60_000 / bpm;

        final Message message = mClickHandler.obtainMessage();
        message.what = ClickHandler.MSG_START_CLICK;
        message.arg1 = periodMillis;
        message.sendToTarget();
    }

    @AnyThread
    public void stop() {
        mClickHandler.sendEmptyMessage(ClickHandler.MSG_STOP_CLICK);
    }

    @AnyThread
    public boolean isStarted() {
        return mClickHandler.isStarted();
    }

    private class ClickHandler extends Handler {

        public static final int MSG_CLOSE = 1;
        public static final int MSG_START_CLICK = 10;
        public static final int MSG_STOP_CLICK = 11;

        private boolean mClosed;
        private volatile boolean mStarted;

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
                sendEmptyMessage(MSG_STOP_CLICK);
                return;
            }

            switch (msg.what) {
                case MSG_START_CLICK: {
                    mStarted = true;
                    mVibrator.vibrate(30);
                    final int delay = msg.arg1;
                    sendMessageDelayed(Message.obtain(msg), delay);
                    break;
                }
                case MSG_STOP_CLICK: {
                    mStarted = false;
                    removeMessages(MSG_START_CLICK);
                    break;
                }
                default:
                    throw new IllegalArgumentException("unknown message: " + msg);
            }
        }

        @AnyThread
        public boolean isStarted() {
            return mStarted;
        }

    }

}
