package com.csc413.team5.fud5.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.csc413.team5.fud5.R;

public class AskToUseLocationDialogFragment extends DialogFragment {
    NoticeDialogListener mListener;

    public static AskToUseLocationDialogFragment newInstance() {
        return new AskToUseLocationDialogFragment();
    }

    /**
     * @param savedInstanceState The last saved instance state of the Fragment,
     *                           or null if this is a freshly created Fragment.
     * @return Return a new Dialog instance to be displayed by the Fragment.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_location_dialog, container, false);

        Button posButton = (Button)v.findViewById(R.id.dialogLocationPositiveButton);
        Button negButton = (Button)v.findViewById(R.id.dialogLocationNegativeButton);

        // watch for button click
        posButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLocationDialogPositiveClick(AskToUseLocationDialogFragment.this);
            }
        });

        // watch for button click
        negButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLocationDialogNegativeClick(AskToUseLocationDialogFragment.this);
            }
        });

        return v;
    }

    public interface NoticeDialogListener {
        void onLocationDialogPositiveClick(DialogFragment dialog);
        void onLocationDialogNegativeClick(DialogFragment dialog);

        // Use this instance of the interface to deliver action events
    }

    // Instantiate a NoticeDialogListener and make sure the host activity implements the
    // callback interface
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }


}
