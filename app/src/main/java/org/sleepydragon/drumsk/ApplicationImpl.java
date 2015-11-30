package org.sleepydragon.drumsk;

import android.app.Application;

import org.sleepydragon.drumsk.ui.api.Globals;
import org.sleepydragon.drumsk.ui.api.UiFactory;
import org.sleepydragon.drumsk.util.Logger;

public class ApplicationImpl extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initializeUiFactory();
    }

    private void initializeUiFactory() {
        final UiFactory uiFactory = new UiFactoryImpl();
        Globals.initialize(uiFactory);
    }

    private void initializeLogger() {
        Logger.initialize("drumsk");
    }

}
