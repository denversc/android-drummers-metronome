package org.sleepydragon.drumsk;

import android.app.Application;
import android.support.annotation.NonNull;

import org.sleepydragon.drumsk.main.DevUtils;
import org.sleepydragon.drumsk.ui.api.Globals;
import org.sleepydragon.drumsk.ui.api.UiFactory;
import org.sleepydragon.drumsk.util.LifecycleLogger;
import org.sleepydragon.drumsk.util.Logger;

public class ApplicationImpl extends Application {

    @NonNull
    private final LifecycleLogger mLifecycleLogger;

    public ApplicationImpl() {
        mLifecycleLogger = new LifecycleLogger(new Logger(this));
    }

    @Override
    public void onCreate() {
        Logger.initialize("drumsk");
        mLifecycleLogger.onCreate();
        super.onCreate();
        initializeDevUtils();
        initializeUiFactory();
    }

    @Override
    public void onTerminate() {
        mLifecycleLogger.onTerminate();
        super.onTerminate();
    }

    private void initializeUiFactory() {
        final UiFactory uiFactory = new UiFactoryImpl();
        Globals.initialize(uiFactory);
    }

    private void initializeDevUtils() {
        if (BuildConfig.DEBUG) {
            DevUtils.initialize(this);
        }
    }

}
