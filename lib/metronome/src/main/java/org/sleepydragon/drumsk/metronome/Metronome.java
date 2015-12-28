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

    private Integer mBpm;
    private Boolean mAudioEnabled;
    private Boolean mVibrateEnabled;
    private AudioClick mAudioClick;
    private VibrateClick mVibrateClick;

    @MainThread
    public void onCreate(@NonNull final Context context) {
        assertMainThread();
        assertFalse(mCreated);
        assertNotNull(context);
        mCreated = true;

        mClickHandlerThread = new HandlerThread("MetronomeClick", THREAD_PRIORITY_FOREGROUND);
        mClickHandlerThread.start();
        mClickHandler = new ClickHandler(mClickHandlerThread.getLooper());

        final Context appContext = context.getApplicationContext();
        mVibrateClick = new VibrateClick(appContext);
        mAudioClick = new AudioClick(appContext);
        mClickHandler.postClickOpen(mAudioClick);
        mClickHandler.postClickOpen(mVibrateClick);
    }

    @MainThread
    public void onDestroy() {
        assertMainThread();
        assertTrue(mCreated);
        stop();
        mCreated = false;

        mClickHandler.postClickClose(mAudioClick);
        mClickHandler.postClickClose(mVibrateClick);
        mClickHandler.post(new HandlerThreadQuitRunnable(mClickHandlerThread));
        mClickHandler.close();
    }

    @MainThread
    public void start(final int bpm, final boolean audioEnabled, final boolean vibrateEnabled) {
        assertMainThread();
        if (bpm < BPM_MIN || bpm > BPM_MAX) {
            throw new IllegalArgumentException("invalid bpm: " + bpm);
        }

        stop();

        final long periodMillis = 60_000 / bpm;
        final long nextClickTime = SystemClock.uptimeMillis();
        mAudioClick.setNextTime(nextClickTime);
        mAudioClick.setPeriodMillis(periodMillis);
        mVibrateClick.setNextTime(nextClickTime + 50L);
        mVibrateClick.setPeriodMillis(periodMillis);

        if (audioEnabled) {
            mClickHandler.postClickClick(mAudioClick);
        }
        if (vibrateEnabled) {
            mClickHandler.postClickClick(mVibrateClick);
        }

        mBpm = bpm;
        mAudioEnabled = audioEnabled;
        mVibrateEnabled = vibrateEnabled;
    }

    @MainThread
    public void stop() {
        assertMainThread();
        mClickHandler.removeMessages(ClickHandler.MSG_CLICK_CLICK);
        mBpm = null;
        mAudioEnabled = null;
        mVibrateEnabled = null;
    }

    @AnyThread
    public boolean isStarted() {
        return (mBpm != null);
    }

    @AnyThread
    @Nullable
    public Integer getBpm() {
        return mBpm;
    }

    @AnyThread
    @Nullable
    public Boolean isAudioEnabled() {
        return mAudioEnabled;
    }

    @AnyThread
    @Nullable
    public Boolean isVibrateEnabled() {
        return mVibrateEnabled;
    }

    private static class ClickHandler extends Handler {

        public static final int MSG_CLICK_OPEN = 1;
        public static final int MSG_CLICK_CLOSE = 2;
        public static final int MSG_CLICK_CLICK = 3;

        private volatile boolean mClosed;

        public ClickHandler(@NonNull final Looper looper) {
            super(looper);
        }

        @AnyThread
        public void close() {
            mClosed = true;
            removeMessages(MSG_CLICK_OPEN);
            removeMessages(MSG_CLICK_CLOSE);
        }

        @WorkerThread
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case MSG_CLICK_CLICK: {
                    if (mClosed) {
                        break;
                    }
                    final Click click = (Click) msg.obj;
                    click.click();
                    postClickClick(click);
                    break;
                }
                case MSG_CLICK_OPEN: {
                    if (mClosed) {
                        break;
                    }
                    final Click click = (Click) msg.obj;
                    click.open();
                    break;
                }
                case MSG_CLICK_CLOSE: {
                    final Click click = (Click) msg.obj;
                    click.close();
                    break;
                }
                default:
                    throw new IllegalArgumentException("unknown message: " + msg);
            }
        }

        public void postClickOpen(@NonNull final Click click) {
            final Message message = Message.obtain();
            message.what = MSG_CLICK_OPEN;
            message.obj = click;
            sendMessage(message);
        }

        public void postClickClose(@NonNull final Click click) {
            final Message message = Message.obtain();
            message.what = MSG_CLICK_CLOSE;
            message.obj = click;
            sendMessage(message);
        }

        public void postClickClick(@NonNull final Click click) {
            final Message message = Message.obtain();
            message.what = MSG_CLICK_CLICK;
            message.obj = click;
            final long time = click.getNextTime();
            sendMessageAtTime(message, time);
        }

    }

}
