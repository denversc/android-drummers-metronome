package org.sleepydragon.drumsk.ui.api;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public abstract class MainFragment extends Fragment implements MainFragmentApi {

    /**
     * Returns the user selected beats-per-minute.
     *
     * @return the user selected beats-per-minute; returns null if the user's currently-selected
     * value is not available or invalid.
     */
    @Nullable
    @MainThread
    public abstract Integer getBpm();

    public interface TargetFragmentCallbacks {

        /**
         * Invoked when the user asks to toggle the state of the metronome between started and
         * stopped.
         *
         * @param fragment the fragment from which this event is originating.
         * @return true if the metronome was started, false if it was stopped, or null if an error
         * occurred changing the state of the metronome.
         */
        @MainThread
        @Nullable
        Boolean onMetronomeToggle(@NonNull MainFragment fragment);

    }

}
