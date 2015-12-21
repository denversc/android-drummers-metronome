package org.sleepydragon.drumsk.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import org.sleepydragon.drumsk.util.AnyThread;

public class MetronomeToggleButton extends Button {

    private static final String KEY_SUPER_STATE = "super_state";
    private static final String KEY_TOGGLE_STATE = "toggle_state";

    private boolean mToggleState;
    private ToggleChangeListener mToggleChangeListener;

    public MetronomeToggleButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOnClickListener(new OnClickListenerImpl());
    }

    @Override
    public Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_TOGGLE_STATE, getToggleState());
        bundle.putParcelable(KEY_SUPER_STATE, super.onSaveInstanceState());
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(final Parcelable state) {
        final Bundle bundle = (Bundle) state;
        final Parcelable superState = bundle.getParcelable(KEY_SUPER_STATE);
        super.onRestoreInstanceState(superState);

        setToggleState(bundle.getBoolean(KEY_TOGGLE_STATE));
    }

    @AnyThread
    public void setToggleChangeListener(@Nullable final ToggleChangeListener listener) {
        mToggleChangeListener = listener;
    }

    @AnyThread
    @Nullable
    public ToggleChangeListener getToggleChangeListener() {
        return mToggleChangeListener;
    }

    @MainThread
    public void notifyToggleChangeListener() {
        final ToggleChangeListener listener = getToggleChangeListener();
        if (listener != null) {
            listener.onToggleChange(this);
        }
    }

    @MainThread
    void onClick() {
        mToggleState = !mToggleState;
        notifyToggleChangeListener();
    }

    @MainThread
    public boolean getToggleState() {
        return mToggleState;
    }

    @MainThread
    public void setToggleState(final boolean toggleState) {
        mToggleState = toggleState;
    }

    public interface ToggleChangeListener {

        void onToggleChange(@NonNull MetronomeToggleButton button);

    }

    private class OnClickListenerImpl implements OnClickListener {

        @Override
        public void onClick(final View view) {
            MetronomeToggleButton.this.onClick();
        }

    }

}
