package org.sleepydragon.drumsk.metronome;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.sleepydragon.drumsk.util.LifecycleLogger;
import org.sleepydragon.drumsk.util.Logger;

import static org.sleepydragon.drumsk.util.Assert.assertNotNull;
import static org.sleepydragon.drumsk.util.Assert.assertTrue;

public class MetronomeService extends Service {

    private static final String ACTION_START = "start";
    private static final String ACTION_STOP = "stop";
    private static final String KEY_BPM = "bpm";

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
            case ACTION_START: {
                assertTrue(intent.hasExtra(KEY_BPM));
                final int bpm = intent.getIntExtra(KEY_BPM, -1);
                mMetronome.start(bpm);
                break;
            }
            case ACTION_STOP:
                mMetronome.stop();
                stopSelf(startId);
                break;
            default:
                throw new IllegalArgumentException("unknown action: " + action);
        }

        return START_NOT_STICKY;
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

    private class IMetronomeServiceImpl extends IMetronomeService.Stub {

        private final Metronome mMetronome;

        public IMetronomeServiceImpl(@NonNull final Metronome metronome) {
            mMetronome = assertNotNull(metronome);
        }

        @Override
        public void start(final int bpm) {
            final Intent intent = new Intent(MetronomeService.this, MetronomeService.class);
            intent.setAction(ACTION_START);
            intent.putExtra(KEY_BPM, bpm);
            if (startService(intent) == null) {
                throw new RuntimeException("service not found: " + intent);
            }
        }

        @Override
        public void stop() {
            final Intent intent = new Intent(MetronomeService.this, MetronomeService.class);
            intent.setAction(ACTION_STOP);
            if (startService(intent) == null) {
                throw new RuntimeException("service not found: " + intent);
            }
        }

        @Override
        public boolean isStarted() {
            return mMetronome.isStarted();
        }

        @Override
        public int getBpm() throws RemoteException {
            final Integer bpm = mMetronome.getBpm();
            return (bpm == null) ? -1 : bpm;
        }

    }

}
