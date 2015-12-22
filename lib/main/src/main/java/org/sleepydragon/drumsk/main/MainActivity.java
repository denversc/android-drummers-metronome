package org.sleepydragon.drumsk.main;

import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import org.sleepydragon.drumsk.ui.api.Globals;
import org.sleepydragon.drumsk.ui.api.MainFragment;
import org.sleepydragon.drumsk.ui.api.UiFactory;
import org.sleepydragon.drumsk.util.LifecycleLogger;
import org.sleepydragon.drumsk.util.Logger;

public class MainActivity extends AppCompatActivity implements WorkFragment.HostCallbacks {

    @NonNull
    private final LifecycleLogger mLifecycleLogger;
    @NonNull
    private final UiFactory mUiFactory;

    private MainFragment mMainFragment;

    public MainActivity() {
        mLifecycleLogger = new LifecycleLogger(new Logger(this));
        mUiFactory = Globals.getUiFactory();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        mLifecycleLogger.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container);

        final FragmentManager fm = getSupportFragmentManager();

        final WorkFragment workFragment;
        {
            final Fragment fragment = fm.findFragmentByTag("work");
            if (fragment != null) {
                workFragment = (WorkFragment) fragment;
            } else {
                workFragment = new WorkFragment();
                fm.beginTransaction().add(workFragment, "work").commit();
            }
        }

        mMainFragment = (MainFragment) fm.findFragmentByTag("ui");
        if (mMainFragment == null) {
            mMainFragment = mUiFactory.createMainFragment();
            mMainFragment.setTargetFragment(workFragment, 0);
            fm.beginTransaction().add(R.id.container, mMainFragment, "ui").commit();
        }
    }

    @Override
    protected void onDestroy() {
        mLifecycleLogger.onDestroy();
        super.onDestroy();
    }

    @Override
    @MainThread
    public void onBpmChanged(@NonNull final WorkFragment fragment, final int bpm) {
        mMainFragment.setBpm(bpm);
    }

}
