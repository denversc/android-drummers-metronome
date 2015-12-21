package org.sleepydragon.drumsk.main;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import org.sleepydragon.drumsk.util.AnyThread;

import static org.sleepydragon.drumsk.util.Assert.assertFalse;
import static org.sleepydragon.drumsk.util.Assert.assertMainThread;
import static org.sleepydragon.drumsk.util.Assert.assertNotNull;
import static org.sleepydragon.drumsk.util.Assert.assertTrue;

public class Metronome {

    @NonNull
    private final ClickHandler mClickHandler;

    private boolean mCreated;

    Vibrator mVibrator;

    public Metronome() {
        mClickHandler = new ClickHandler(Looper.getMainLooper());
    }

    @MainThread
    public void onCreate(@NonNull final Context context) {
        assertMainThread();
        assertFalse(mCreated);
        assertNotNull(context);
        mCreated = true;
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @MainThread
    public void onDestroy() {
        assertMainThread();
        assertTrue(mCreated);
        mCreated = false;
        mClickHandler.sendEmptyMessage(ClickHandler.MSG_CLOSE);
    }

    @AnyThread
    public void start() {
        final Message message = mClickHandler.obtainMessage();
        message.what = ClickHandler.MSG_CLICK;
        message.arg1 = 800;
        message.sendToTarget();
    }

    @AnyThread
    public void stop() {
        mClickHandler.removeMessages(ClickHandler.MSG_CLICK);
    }

    private class ClickHandler extends Handler {

        public static final int MSG_CLOSE = 1;
        public static final int MSG_CLICK = 10;

        private boolean mClosed;

        public ClickHandler(@NonNull final Looper looper) {
            super(looper);
        }

        @WorkerThread
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case MSG_CLICK: {
                    if (mClosed) {
                        break;
                    }
                    mVibrator.vibrate(50);
                    final int delay = msg.arg1;
                    sendMessageDelayed(Message.obtain(msg), delay);
                    break;
                }
                case MSG_CLOSE:
                    mClosed = true;
                    break;
                default:
                    throw new IllegalArgumentException("unknown message: " + msg);
            }
        }
    }

}
