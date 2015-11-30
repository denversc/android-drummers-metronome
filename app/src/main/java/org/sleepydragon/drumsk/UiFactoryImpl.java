package org.sleepydragon.drumsk;

import android.support.annotation.NonNull;

import org.sleepydragon.drumsk.ui.MainFragmentImpl;
import org.sleepydragon.drumsk.ui.api.MainFragment;
import org.sleepydragon.drumsk.ui.api.UiFactory;

public class UiFactoryImpl implements UiFactory {

    @NonNull
    @Override
    public MainFragment createMainFragment() {
        return new MainFragmentImpl();
    }

}
