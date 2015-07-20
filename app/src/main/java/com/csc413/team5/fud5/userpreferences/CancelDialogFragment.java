package com.csc413.team5.fud5.userpreferences;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.csc413.team5.fud5.R;
import com.csc413.team5.fud5.utils.ToastUtil;

/**
 * Created by niculistana on 7/16/15.
 */
public class CancelDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        /* Build AlertDialog */
        AlertDialog cancelDialog = new AlertDialog.Builder(getActivity())

        .setTitle(R.string.generic_are_you_sure_title)
        .setMessage(R.string.generic_cancel_text_message)

        // Removes all items that are selected
        .setPositiveButton(R.string.generic_ok_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        })

        // Removes all items that are selected
        .setNegativeButton(R.string.generic_back_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Go back to activity
                ToastUtil.showShortToast(getActivity(), "Back stack");
            }
        }).create();
        /* End build AlertDialog */

        cancelDialog.setCanceledOnTouchOutside(true);

        return cancelDialog;
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        // TODO refresh settings
        ToastUtil.showShortToast(getActivity(), "Settings cancelled");
    }
}
