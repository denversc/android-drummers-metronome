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

    /**
     * Sets the beats-per-minute to display in the UI, which the user can subsequently change.
     *
     * @param bpm the beats-per-minute to display in the UI.
     */
    @MainThread
    public abstract void setBpm(final int bpm);

    /**
     * Returns the user selected value of audio being enabled.
     *
     * @return true if the user has selected that audio should be enabled; false otherwise.
     */
    @MainThread
    public abstract boolean isAudioEnabled();

    /**
     * Sets whether or not the checkbox for showing if vibrate is enabled should be checked.
     *
     * @param enabled true if enabled, false if not.
     */
    @MainThread
    public abstract void setAudioEnabled(final boolean enabled);

    /**
     * Returns the user selected value of vibrate being enabled.
     *
     * @return true if the user has selected that vibrate should be enabled; false otherwise.
     */
    @MainThread
    public abstract boolean isVibrateEnabled();

    /**
     * Sets whether or not the checkbox for showing if vibrate is enabled should be checked.
     *
     * @param enabled true if enabled, false if not.
     */
    @MainThread
    public abstract void setVibrateEnabled(final boolean enabled);

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

        /**
         * Invoked when the user changes the configuration of the metronome.
         *
         * @param fragment the fragment from which this event is originating.
         */
        @MainThread
        void onMetronomeConfigChange(@NonNull MainFragment fragment);

        /**
         * Gets the current beats-per-minute to display in the user interface.
         *
         * @return the beats-per-minute at which the metronome is running, or null if the metronome
         * is not running.
         */
        @MainThread
        @Nullable
        Integer getCurrentBpm();

        /**
         * Gets the current "audio enabled" setting to display in the user interface.
         *
         * @return true or false if the metronome is running with audio enabled,
         * or null if the metronome is not running.
         */
        @MainThread
        @Nullable
        Boolean getCurrentAudioEnabled();


        /**
         * Gets the current "vibrate enabled" setting to display in the user interface.
         *
         * @return true or false if the metronome is running with vibrate enabled,
         * or null if the metronome is not running.
         */
        @MainThread
        @Nullable
        Boolean getCurrentVibrateEnabled();
    }

}
