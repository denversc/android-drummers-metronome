package org.sleepydragon.drumsk.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sleepydragon.drumsk.ui.api.MainFragment;

import static org.sleepydragon.drumsk.util.Assert.assertNotNull;

public class MainFragmentImpl extends MainFragment implements View.OnClickListener {

    private MetronomeToggleButton mToggleButton;
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
    }

    @Override
    public int getBpm() {
        return 100;
    }

    @Override
    public void onClick(final View view) {
        mTargetFragmentCallbacks.onMetronomeToggle(this);
    }

}
