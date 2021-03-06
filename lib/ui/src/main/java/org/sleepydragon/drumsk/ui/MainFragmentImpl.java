package org.sleepydragon.drumsk.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import org.sleepydragon.drumsk.ui.api.MainFragment;

import static org.sleepydragon.drumsk.util.Assert.assertNotNull;

public class MainFragmentImpl extends MainFragment
        implements View.OnClickListener, View.OnLongClickListener,
        CheckBox.OnCheckedChangeListener {

    private BpmView mBpmView;
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
        mBpmView = (BpmView) view.findViewById(R.id.bpm);
        mBpmView.setOnClickListener(this);
        mBpmView.setOnLongClickListener(this);
        mVibrateCheckBox = (CheckBox) view.findViewById(R.id.vibrate);
        mVibrateCheckBox.setOnCheckedChangeListener(this);
        mAudioCheckBox = (CheckBox) view.findViewById(R.id.audio);
        mAudioCheckBox.setOnCheckedChangeListener(this);

        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        final AppCompatActivity activity = (AppCompatActivity) getContext();
        activity.setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            mBpmView.setBpm(100);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mTargetFragmentCallbacks.updateUiFromMetronome();
    }

    @Override
    @Nullable
    @MainThread
    public Integer getBpm() {
        return mBpmView.getBpm();
    }

    @Override
    @MainThread
    public void setBpm(final int bpm) {
        mBpmView.setBpm(bpm);
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
    public void setMetronomeRunning(final boolean running) {
        mBpmView.setPlaying(running);
    }

    @Override
    public void onClick(final View view) {
        if (view == mBpmView) {
            final Boolean running = mTargetFragmentCallbacks.onMetronomeToggle(this);
            if (running != null) {
                setMetronomeRunning(running);
            }
        } else {
            throw new IllegalArgumentException("unknown view: " + view);
        }
    }

    @Override
    public boolean onLongClick(final View view) {
        if (view == mBpmView) {
            final BpmDialogFragment dialog = new BpmDialogFragment();
            final Integer bpm = mBpmView.getBpm();
            if (bpm != null) {
                final Bundle args = new Bundle();
                args.putInt(BpmDialogFragment.ARG_BPM, bpm);
                dialog.setArguments(args);
            }
            dialog.show(getFragmentManager(), "BpmDialogFragment");
            dialog.setTargetFragment(this, 0);
            return true;
        } else {
            throw new IllegalArgumentException("unknown view: " + view);
        }
    }

    @Override
    public void onCheckedChanged(final CompoundButton view, final boolean isChecked) {
        if (view == mAudioCheckBox || view == mVibrateCheckBox) {
            mTargetFragmentCallbacks.onMetronomeConfigChange(this);
        } else {
            throw new IllegalArgumentException("unknown view: " + view);
        }
    }

    @MainThread
    void onUserSelectedBpmFromDialog(final int bpm) {
        mBpmView.setBpm(bpm);
        mTargetFragmentCallbacks.onMetronomeConfigChange(this);
    }

}
