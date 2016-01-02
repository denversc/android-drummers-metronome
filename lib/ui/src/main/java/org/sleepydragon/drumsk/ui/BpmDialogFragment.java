package org.sleepydragon.drumsk.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import org.sleepydragon.drumsk.util.LifecycleLogger;
import org.sleepydragon.drumsk.util.Logger;

public class BpmDialogFragment extends AppCompatDialogFragment
        implements DialogInterface.OnClickListener {

    public static final String ARG_BPM = "bpm";

    @NonNull
    private final Logger mLogger;
    @NonNull
    private final LifecycleLogger mLifecycleLogger;

    private EditText mBpmEditText;

    public BpmDialogFragment() {
        mLogger = new Logger(this);
        mLifecycleLogger = new LifecycleLogger(mLogger);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        mLifecycleLogger.onCreate();
        super.onCreate(savedInstanceState);
        setCancelable(true);
    }

    @Override
    public void onDestroy() {
        mLifecycleLogger.onDestroy();
        super.onDestroy();
    }

    @NonNull
    @Override
    @SuppressLint("InflateParams")
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        final View customView = inflater.inflate(R.layout.dialog_view_bpm, null);
        mBpmEditText = (EditText) customView.findViewById(R.id.bpm_edit_text);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("BPM");
        builder.setPositiveButton(android.R.string.ok, this);
        builder.setView(customView);
        final AlertDialog dialog = builder.create();

        final Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(ARG_BPM)) {
                final int bpm = args.getInt(ARG_BPM);
                mBpmEditText.setText(String.valueOf(bpm));
            }
        }

        return dialog;
    }

    @Override
    public void onClick(final DialogInterface dialog, final int which) {
        final Editable cs = mBpmEditText.getText();
        if (cs == null) {
            return;
        }

        final String s = cs.toString().trim();
        final int bpm;
        try {
            bpm = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return;
        }

        mLogger.i("User changed BPM: %d", bpm);
        final MainFragmentImpl mainFragment = (MainFragmentImpl) getTargetFragment();
        mainFragment.onUserSelectedBpmFromDialog(bpm);
    }

}
