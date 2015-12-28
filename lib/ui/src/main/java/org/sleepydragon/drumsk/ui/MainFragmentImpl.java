package org.sleepydragon.drumsk.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import org.sleepydragon.drumsk.ui.api.MainFragment;

import static org.sleepydragon.drumsk.util.Assert.assertNotNull;

public class MainFragmentImpl extends MainFragment implements View.OnClickListener {

    private MetronomeToggleButton mToggleButton;
    private EditText mBpmEditText;
    private CheckBox mVibrateCheckBox;
    private CheckBox mAudioCheckBox;
    private TargetFragmentCallbacks mTargetFragmentCallbacks;

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        mTargetFragmentCallbacks = (TargetFragmentCallbacks) getTargetFragment();
        assertNotNull(mTargetFragmentCallbacks);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        mToggleButton = (MetronomeToggleButton) view.findViewById(R.id.toggle_button);
        mToggleButton.setOnClickListener(this);
        mBpmEditText = (EditText) view.findViewById(R.id.bpm);
        mVibrateCheckBox = (CheckBox) view.findViewById(R.id.vibrate);
        mAudioCheckBox = (CheckBox) view.findViewById(R.id.audio);

        final Integer bpm = mTargetFragmentCallbacks.getCurrentBpm();
        setBpm(bpm == null ? 100 : bpm);

        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        final AppCompatActivity activity = (AppCompatActivity) getContext();
        activity.setSupportActionBar(toolbar);
    }

    @Override
    @Nullable
    @MainThread
    public Integer getBpm() {
        final Editable editable = mBpmEditText.getText();
        if (editable == null) {
            return null;
        }
        final String string = editable.toString();
        try {
            return Integer.parseInt(string.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    @MainThread
    public void setBpm(final int bpm) {
        mBpmEditText.setText(String.valueOf(bpm));
    }

    @Override
    @MainThread
    public boolean isVibrateEnabled() {
        return mVibrateCheckBox.isChecked();
    }

    @Override
    @MainThread
    public void setVibrateEnabled(final boolean enabled) {
        mVibrateCheckBox.setChecked(enabled);
    }

    @Override
    @MainThread
    public boolean isAudioEnabled() {
        return mAudioCheckBox.isChecked();
    }

    @Override
    @MainThread
    public void setAudioEnabled(final boolean enabled) {
        mAudioCheckBox.setChecked(enabled);
    }

    @Override
    public void onClick(final View view) {
        if (view == mToggleButton) {
            mTargetFragmentCallbacks.onMetronomeToggle(this);
        } else {
            throw new IllegalArgumentException("unknown view: " + view);
        }
    }

}
