package com.csc413.team5.fud5;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class dbColorDialogFragment extends DialogFragment {
    int dSetting;

    public static dbColorDialogFragment newInstance(int dialogSetting) {
        dbColorDialogFragment dbDialog = new dbColorDialogFragment();

        Bundle args = new Bundle();
        args.putInt("dialogSetting", dialogSetting);

        dbDialog.setArguments(args);
        return dbDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        dSetting = getArguments().getInt("dialogSetting");
        EditText inputText = (EditText) this.getActivity().findViewById(R.id.restaurantInput);
        final String restaurantInput = inputText.getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.select_db_color)
                .setItems(R.array.db_colors_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            switch (dSetting) {
                                case 1: insertToGreenList(restaurantInput); break;
                                case 2: viewGreenList(); break;
                                case 3: migrateToGreenList(restaurantInput); break;
                                case 4: deleteFromGreenList(restaurantInput); break;
                            }
                        } else if (which == 1) {
                            switch (dSetting) {
                                case 1: insertToYellowList(restaurantInput); break;
                                case 2: viewYellowList(); break;
                                case 3: migrateToYellowList(restaurantInput); break;
                                case 4: deleteFromYellowList(restaurantInput); break;
                            }
                        } else if (which == 2){
                            switch (dSetting) {
                                case 1: insertToRedList(restaurantInput); break;
                                case 2: viewRedList(); break;
                                case 3: migrateToRedList(restaurantInput); break;
                                case 4: deleteFromRedList(restaurantInput); break;
                            }
                        }
                    }
                });
        return builder.create();
    }

    // Create
    private void insertToGreenList(String inputText) {
        Context context = this.getActivity().getApplicationContext();
        CharSequence text = "Inserted " + inputText + " to green list.";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);



        toast.show();
    }
    private void insertToYellowList(String inputText) {
        Context context = this.getActivity().getApplicationContext();
        CharSequence text = "Inserted " + inputText + " to yellow list.";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
    private void insertToRedList(String inputText) {
        Context context = this.getActivity().getApplicationContext();
        CharSequence text = "Inserted " + inputText + " to red list.";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    // Read
    private void viewGreenList() {
        Intent intent  = new Intent(getActivity(), GreenListActivity.class);
        startActivity(intent);
    }

    private void viewYellowList() {
        Intent intent  = new Intent(getActivity(), YellowListActivity.class);
        startActivity(intent);
    }

    private void viewRedList() {
        Intent intent  = new Intent(getActivity(), RedListActivity.class);
        startActivity(intent);
    }

    // Update
    private void migrateToGreenList(String inputText) {
        Context context = this.getActivity().getApplicationContext();
        CharSequence text = "Migrated " + inputText + " to green list.";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void migrateToYellowList(String inputText) {
        Context context = this.getActivity().getApplicationContext();
        CharSequence text = "Migrated " + inputText + " to yellow list.";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void migrateToRedList(String inputText) {
        Context context = this.getActivity().getApplicationContext();
        CharSequence text = "Migrated " + inputText + " to red list.";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    // Delete
    private void deleteFromGreenList(String inputText) {
        Context context = this.getActivity().getApplicationContext();
        CharSequence text = "Deleted " + inputText + " from green list.";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void deleteFromYellowList(String inputText) {
        Context context = this.getActivity().getApplicationContext();
        CharSequence text = "Deleted " + inputText + " from yellow list.";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void deleteFromRedList(String inputText) {
        Context context = this.getActivity().getApplicationContext();
        CharSequence text = "Deleted " + inputText + " from red list.";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
