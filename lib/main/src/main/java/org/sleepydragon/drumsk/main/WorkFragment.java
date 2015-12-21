package org.sleepydragon.drumsk.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import org.sleepydragon.drumsk.ui.api.MainFragment;
import org.sleepydragon.drumsk.util.LifecycleLogger;
import org.sleepydragon.drumsk.util.Logger;

public class WorkFragment extends Fragment
        implements MainFragment.TargetFragmentCallbacks, ServiceConnection {

    @NonNull
    private final Logger mLogger;
    @NonNull
    private final LifecycleLogger mLifecycleLogger;

    private Context mAppContext;
    private IMetronomeService mMetronomeService;

    public WorkFragment() {
        mLogger = new Logger(this);
        mLifecycleLogger = new LifecycleLogger(mLogger);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        mLifecycleLogger.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mAppContext = getContext().getApplicationContext();

        {
            final Intent intent = new Intent(mAppContext, MetronomeService.class);
            if (!mAppContext.bindService(intent, this, Context.BIND_AUTO_CREATE)) {
                throw new RuntimeException("service not found: " + intent);
            }
        }
    }

    @Override
    public void onDestroy() {
        mLifecycleLogger.onDestroy();
        mAppContext.unbindService(this);
        super.onDestroy();
    }

    @MainThread
    @Override
    public void onMetronomeToggle(@NonNull final MainFragment fragment, final boolean on) {
        mLogger.i("Metronome toggle clicked: on=%s", on);
        if (mMetronomeService != null) {
            try {
                mMetronomeService.setOn(on);
            } catch (RemoteException e) {
                mLogger.e(e, "IMetronomeService.setOn(on=%s) failed", on);
            }
        }
    }

    @Override
    @MainThread
    public void onServiceConnected(final ComponentName name, final IBinder service) {
        mLifecycleLogger.onServiceConnected(name, service);
        mMetronomeService = IMetronomeService.Stub.asInterface(service);
    }

    @Override
    @MainThread
    public void onServiceDisconnected(final ComponentName name) {
        mLifecycleLogger.onServiceDisconnected(name);
        mMetronomeService = null;
    }

}
