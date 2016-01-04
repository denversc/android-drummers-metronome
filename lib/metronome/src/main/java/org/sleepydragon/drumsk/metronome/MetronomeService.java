package org.sleepydragon.drumsk.metronome;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.sleepydragon.drumsk.util.LifecycleLogger;
import org.sleepydragon.drumsk.util.Logger;

import static org.sleepydragon.drumsk.util.Assert.assertNotNull;
import static org.sleepydragon.drumsk.util.Assert.assertTrue;

public class MetronomeService extends Service {

    private static final String ACTION_START = "start";
    private static final String ACTION_STOP = "stop";
    private static final String KEY_CONFIG = "config";

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
                assertTrue(intent.hasExtra(KEY_CONFIG));
                final MetronomeConfig config = intent.getParcelableExtra(KEY_CONFIG);
                mMetronome.start(config);
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

    public static int getMinBpm() {
        return MetronomeThread.BPM_MIN;
    }

    public static int getMaxBpm() {
        return MetronomeThread.BPM_MAX;
    }

    private class IMetronomeServiceImpl extends IMetronomeService.Stub {

        private final Metronome mMetronome;

        public IMetronomeServiceImpl(@NonNull final Metronome metronome) {
            mMetronome = assertNotNull(metronome);
        }

        @Override
        public void start(@NonNull final MetronomeConfig config) {
            assertNotNull(config);
            final Intent intent = new Intent(MetronomeService.this, MetronomeService.class);
            intent.setAction(ACTION_START);
            intent.putExtra(KEY_CONFIG, config);
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
        public MetronomeConfig getConfig() {
            return mMetronome.getMetronomeConfig();
        }

        @Override
        public boolean setConfig(final MetronomeConfig config) {
            return mMetronome.setMetronomeConfig(config);
        }

    }

}
