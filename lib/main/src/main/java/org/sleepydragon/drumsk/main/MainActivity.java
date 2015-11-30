package org.sleepydragon.drumsk.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import org.sleepydragon.drumsk.ui.api.Globals;
import org.sleepydragon.drumsk.ui.api.UiFactory;
import org.sleepydragon.drumsk.util.LifecycleLogger;
import org.sleepydragon.drumsk.util.Logger;

public class MainActivity extends AppCompatActivity {

    @NonNull
    private final LifecycleLogger mLifecycleLogger;
    @NonNull
    private final UiFactory mUiFactory;

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
        if (fm.findFragmentByTag("ui") == null) {
            final Fragment fragment = mUiFactory.createMainFragment();
            fm.beginTransaction().add(R.id.container, fragment, "ui").commit();
        }
    }

    @Override
    protected void onDestroy() {
        mLifecycleLogger.onDestroy();
        super.onDestroy();
    }

}
