package com.csc413.team5.fud5.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.csc413.team5.fud5.R;

public class AskToUseLocationDialogFragment extends DialogFragment {
    NoticeDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_ask_to_use_location_title)
                .setMessage(R.string.dialog_ask_to_use_location_body)
                .setPositiveButton(R.string.dialog_ask_to_use_location_agree_button,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Send the positive button event back to the host activity
                                mListener.onLocationDialogPositiveClick(AskToUseLocationDialogFragment.this);
                            }
                        })
                .setNegativeButton(R.string.dialog_ask_to_use_location_disagree_button,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Send the negative button event back to the host activity
                                mListener.onLocationDialogNegativeClick(AskToUseLocationDialogFragment.this);
                            }
                        });
        //return super.onCreateDialog(savedInstanceState);
        return builder.create();
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
