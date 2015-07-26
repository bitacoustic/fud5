package com.csc413.team5.fud5.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.csc413.team5.fud5.R;
import com.csc413.team5.fud5.utils.ToastUtil;

/**
 * A generic cancel dialog which confirms user's actions
 * Created by niculistana on 7/16/15.
 */
public class AreYouSureDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        /* Build AlertDialog */
        AlertDialog cancelDialog = new AlertDialog.Builder(getActivity())

        .setTitle(R.string.generic_are_you_sure_title)
        .setMessage(R.string.generic_cancel_text_message)

        // Don't save new settings
        .setPositiveButton(R.string.generic_back_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Go back to activity
            }
        })

        // Go back to setting up new settings
        .setNegativeButton(R.string.generic_ok_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create();

        /* End build AlertDialog */

        cancelDialog.setCanceledOnTouchOutside(true);

        return cancelDialog;
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        getActivity().finish();
        ToastUtil.showShortToast(getActivity(), "Settings cancelled");
    }
}
