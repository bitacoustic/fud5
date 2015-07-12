package com.csc413.team5.appdbtest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.csc413.team5.appdb.dbHelper;
import com.csc413.team5.restaurantapiwrapper.Restaurant;

public class dbColorDialogFragment extends DialogFragment {
    private int dSetting;
    dbHelper db;

    public static dbColorDialogFragment newInstance(int dialogSetting) {
        dbColorDialogFragment dbDialog = new dbColorDialogFragment();

        Bundle args = new Bundle();
        args.putInt("dialogSetting", dialogSetting);

        dbDialog.setArguments(args);
        return dbDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        db = new dbHelper(this.getActivity(), null, null, 1);

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
                                case 1:
                                    insertToGreenList(restaurantInput);
                                    break;
                                case 2:
                                    viewGreenList();
                                    break;
                                case 3:
                                    migrateToGreenList(restaurantInput, 2); // yellow -> green
                                    break;
                                case 4:
                                    deleteFromGreenList(restaurantInput);
                                    break;
                            }
                        } else if (which == 1) {
                            switch (dSetting) {
                                case 1:
                                    insertToYellowList(restaurantInput);
                                    break;
                                case 2:
                                    viewYellowList();
                                    break;
                                case 3:
                                    migrateToYellowList(restaurantInput, 1); // green -> yellow
                                    break;
                                case 4:
                                    deleteFromYellowList(restaurantInput);
                                    break;
                            }
                        } else if (which == 2) {
                            switch (dSetting) {
                                case 1:
                                    insertToRedList(restaurantInput);
                                    break;
                                case 2:
                                    viewRedList();
                                    break;
                                case 3:
                                    migrateToRedList(restaurantInput, 1); // green -> red
                                    break;
                                case 4:
                                    deleteFromRedList(restaurantInput);
                                    break;
                            }
                        }
                    }
                });
        return builder.create();
    }

    // Create
    private void insertToGreenList(String inputText) {
        Context context = this.getActivity().getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Restaurant r = new Restaurant();
        r.setRestaurantName(inputText);

        db.rawInsertRestaurantToList(r, 1);
        CharSequence text = "Inserted " + inputText + " to green list.";
        Toast toast = Toast.makeText(context, text, duration);

        toast.show();

        Log.i("Insert", "Clicked");
    }
    private void insertToYellowList(String inputText) {
        Context context = this.getActivity().getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Restaurant r = new Restaurant();
        r.setRestaurantName(inputText);

        db.rawInsertRestaurantToList(r, 2);
        CharSequence text = "Inserted " + inputText + " to yellow list.";
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
    private void insertToRedList(String inputText) {
        Context context = this.getActivity().getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Restaurant r = new Restaurant();
        r.setRestaurantName(inputText);

        db.rawInsertRestaurantToList(r, 3);

        CharSequence text = "Inserted " + inputText + " to red list.";
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
    private void migrateToGreenList(String inputText, int fromList) {
        Context context = this.getActivity().getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Restaurant r = new Restaurant();
        r.setRestaurantName(inputText);

        CharSequence text;

        if (fromList == 2) {    // from yellow
            text = "Migrated " + inputText + " from yellow to green migration: " + db.migrateRestaurantListItem(r, 2, 1);
        } else if (fromList == 3){    // from red
            text = "Migrated " + inputText + " from red to green migration: " + db.migrateRestaurantListItem(r, 3, 1);
        } else {
            text = "No migration happened for " + inputText;
        }

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void migrateToYellowList(String inputText, int fromList) {
        Context context = this.getActivity().getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Restaurant r = new Restaurant();
        r.setRestaurantName(inputText);

        CharSequence text;

        if (fromList == 1) {    // from green
            text = "Migrated " + inputText + " from green to yellow migration: " + db.migrateRestaurantListItem(r, 1, 2);
        } else if (fromList == 3){    // from red
            text = "Migrated " + inputText + " from green to yellow migration: " + db.migrateRestaurantListItem(r, 3, 2);
        } else {
            text = "No migration happened for " + inputText;
        }

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void migrateToRedList(String inputText, int fromList) {
        Context context = this.getActivity().getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Restaurant r = new Restaurant();
        r.setRestaurantName(inputText);

        CharSequence text;

        if (fromList == 1) {    // from green
            text = "Migrated " + inputText + " from green to red migration: " + db.migrateRestaurantListItem(r, 1, 3);
        } else if (fromList == 2){  // from yellow
            text = "Migrated " + inputText + " from yellow to red migration: " + db.migrateRestaurantListItem(r, 1, 2);
        } else {
            text = "No migration happened for " + inputText;
        }

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    // Delete
    private void deleteFromGreenList(String inputText) {
        Context context = this.getActivity().getApplicationContext();

        Restaurant r = new Restaurant();
        r.setRestaurantName(inputText);

        CharSequence text = "Delete: " + inputText + " from green list = " + db.deleteRestaurantFromList(r, 1);
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void deleteFromYellowList(String inputText) {
        Context context = this.getActivity().getApplicationContext();

        Restaurant r = new Restaurant();
        r.setRestaurantName(inputText);

        CharSequence text = "Delete: " + inputText + " from yellow list = " + db.deleteRestaurantFromList(r, 2);
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void deleteFromRedList(String inputText) {
        Context context = this.getActivity().getApplicationContext();

        Restaurant r = new Restaurant();
        r.setRestaurantName(inputText);

        CharSequence text = "Delete: " + inputText + " from red list = " + db.deleteRestaurantFromList(r, 3);
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
