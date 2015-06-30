package com.csc413.team5.fud5;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class dbColorDialogFragment extends DialogFragment {

    static dbColorDialogFragment newInstance(String dialogText) {
        dbColorDialogFragment dbDialog = new dbColorDialogFragment();

        Bundle args = new Bundle();
        args.putString("dialogText", dialogText);

        dbDialog.setArguments(args);
        return dbDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.select_db_color)
                .setItems(R.array.db_colors_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 1) {
                            onGreenList();
                        } else if (which == 2) {
                            onYellowList();
                        } else {
                            onRedList();
                        }
                    }
                });
        return builder.create();
    }

    private void onGreenList() {
        Intent intent  = new Intent(getActivity(), GreenListActivity.class);
        startActivity(intent);
    }

    private void onYellowList() {
        Intent intent  = new Intent(getActivity(), GreenListActivity.class);
        startActivity(intent);
    }

    private void onRedList() {
        Intent intent  = new Intent(getActivity(), GreenListActivity.class);
        startActivity(intent);
    }
}
