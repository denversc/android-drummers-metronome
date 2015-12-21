package org.sleepydragon.drumsk.ui.api;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

public abstract class MainFragment extends Fragment implements MainFragmentApi {

    public interface TargetFragmentCallbacks {

        void onMetronomeToggle(@NonNull MainFragment fragment, boolean on);

    }

}
