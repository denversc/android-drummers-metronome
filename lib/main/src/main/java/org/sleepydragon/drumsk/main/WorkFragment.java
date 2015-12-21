package org.sleepydragon.drumsk.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import org.sleepydragon.drumsk.ui.api.MainFragment;
import org.sleepydragon.drumsk.util.LifecycleLogger;
import org.sleepydragon.drumsk.util.Logger;

public class WorkFragment extends Fragment implements MainFragment.TargetFragmentCallbacks {

    @NonNull
    private final Logger mLogger;
    @NonNull
    private final LifecycleLogger mLifecycleLogger;

    public WorkFragment() {
        mLogger = new Logger(this);
        mLifecycleLogger = new LifecycleLogger(mLogger);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        mLifecycleLogger.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setRetainInstance(false);
    }

    @Override
    public void onDestroy() {
        mLifecycleLogger.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onMetronomeToggle(@NonNull final MainFragment fragment, final boolean on) {
        mLogger.i("Metronome toggle clicked: on=%s", on);
    }

}
