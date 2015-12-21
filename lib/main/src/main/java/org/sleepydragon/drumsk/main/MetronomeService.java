package org.sleepydragon.drumsk.main;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.sleepydragon.drumsk.util.LifecycleLogger;
import org.sleepydragon.drumsk.util.Logger;

import static org.sleepydragon.drumsk.util.Assert.assertNotNull;

public class MetronomeService extends Service {

    private static final String ACTION_START = "start";
    private static final String ACTION_STOP = "stop";

    @NonNull
    private final Logger mLogger;
    @NonNull
    private final LifecycleLogger mLifecycleLogger;
    @NonNull
    private final IBinder mBinder;
    @NonNull
    private final Metronome mMetronome;

    public MetronomeService() {
        mLogger = new Logger(this);
        mLifecycleLogger = new LifecycleLogger(mLogger);
        mMetronome = new Metronome();
        mBinder = new IMetronomeServiceImpl(mMetronome);
    }

    @Override
    public void onCreate() {
        mLifecycleLogger.onCreate();
        super.onCreate();
        mMetronome.onCreate(this);
    }

    @Override
    public void onDestroy() {
        mLifecycleLogger.onDestroy();
        mMetronome.onDestroy();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        mLifecycleLogger.onStartCommand(intent, flags, startId);

        final String action = intent.getAction();
        switch (action) {
            case ACTION_START:
                mMetronome.start();
                break;
            case ACTION_STOP:
                mMetronome.stop();
                stopSelf(startId);
                break;
            default:
                throw new IllegalArgumentException("unknown action: " + action);
        }

        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        mLifecycleLogger.onBind(intent);
        return mBinder;
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        mLifecycleLogger.onUnbind(intent);
        return true;
    }

    @Override
    public void onRebind(final Intent intent) {
        mLifecycleLogger.onRebind(intent);
    }

    void startServiceWithAction(@NonNull final String action) {
        final Intent intent = new Intent(this, this.getClass());
        intent.setAction(action);
        if (startService(intent) == null) {
            throw new RuntimeException("service not found: " + intent);
        }
    }

    private class IMetronomeServiceImpl extends IMetronomeService.Stub {

        private final Metronome mMetronome;

        public IMetronomeServiceImpl(@NonNull final Metronome metronome) {
            mMetronome = assertNotNull(metronome);
        }

        @Override
        public void setOn(final boolean on) {
            if (on) {
                startServiceWithAction(ACTION_START);
            } else {
                startServiceWithAction(ACTION_STOP);
            }
        }

    }

}
