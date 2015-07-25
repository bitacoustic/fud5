package com.csc413.team5.fud5;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * TODO Description
 * <p>
 * Created on $(DATE).
 *
 * @author Eric C. Black
 */
public class MenuNotFoundFragment extends DialogFragment {
    MenuNotFoundDialogListener mListener;

    /**
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     *                           or null if this is a freshly created Fragment.
     * @return Return a new Dialog instance to be displayed by the Fragment.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_menu_not_found_text)
                .setPositiveButton(R.string.dialog_menu_not_found_ok_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mListener.onMenuNotFoundPositiveClick(MenuNotFoundFragment.this);
                            }
                        });

        return builder.create();
    }

    public interface MenuNotFoundDialogListener {
        void onMenuNotFoundPositiveClick(DialogFragment dialog);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (MenuNotFoundDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
