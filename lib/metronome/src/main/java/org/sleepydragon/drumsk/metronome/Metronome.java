package org.sleepydragon.drumsk.metronome;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
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

    private Context mContext;
    private ClickHandler mClickHandler;
    private HandlerThread mClickHandlerThread;
    private boolean mCreated;
    private Integer mBpm;

    public Metronome() {
        mLogger = new Logger(this);
    }

    @MainThread
    public void onCreate(@NonNull final Context context) {
        assertMainThread();
        assertFalse(mCreated);
        assertNotNull(context);
        mCreated = true;

        mContext = context;
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
        final Click click = new Click(mContext, periodMillis);

        final Message message = mClickHandler.obtainMessage();
        message.what = ClickHandler.MSG_START_CLICK;
        message.obj = click;
        message.sendToTarget();

        mBpm = bpm;
    }

    @MainThread
    public void stop() {
        assertMainThread();
        mClickHandler.sendEmptyMessage(ClickHandler.MSG_STOP_CLICK);
        mBpm = null;
    }

    @AnyThread
    public boolean isStarted() {
        return (mBpm != null);
    }

    @AnyThread
    public Integer getBpm() {
        return mBpm;
    }

    private static class ClickHandler extends Handler {

        public static final int MSG_CLOSE = 1;
        public static final int MSG_START_CLICK = 10;
        public static final int MSG_STOP_CLICK = 11;
        public static final int MSG_CLICK = 12;

        @NonNull
        private final Logger mLogger;

        private boolean mClosed;

        public ClickHandler(@NonNull final Looper looper) {
            super(looper);
            mLogger = new Logger(this);
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
                    final Click click = (Click) msg.obj;
                    mLogger.i("Starting metronome");
                    removeMessages(MSG_CLICK);
                    final Message clickMessage = obtainMessage(MSG_CLICK, click);
                    sendMessage(clickMessage);
                    break;
                }
                case MSG_STOP_CLICK: {
                    mLogger.i("Stopping metronome");
                    removeMessages(MSG_CLICK);
                    break;
                }
                case MSG_CLICK: {
                    final Click click = (Click) msg.obj;
                    final long nextTime = click.click();
                    final Message clickMessage = obtainMessage(MSG_CLICK, click);
                    sendMessageAtTime(clickMessage, nextTime);
                    break;
                }
                default:
                    throw new IllegalArgumentException("unknown message: " + msg);
            }
        }

    }

}
