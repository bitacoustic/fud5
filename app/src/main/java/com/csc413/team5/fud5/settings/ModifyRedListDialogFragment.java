package com.csc413.team5.fud5.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.csc413.team5.appdb.dbHelper;
import com.csc413.team5.fud5.R;
import com.csc413.team5.restaurantapiwrapper.Restaurant;

import java.util.ArrayList;

public class ModifyRedListDialogFragment extends DialogFragment {

    /* Member variables*/
    // local variables
    CharSequence redListItems[];
    ArrayList selList=new ArrayList();
    boolean bl[];
    String msg ="";

    // database variables
    dbHelper db_helper;

    public static ModifyRedListDialogFragment newInstance() {
        // No args needed, therefore no bundle
        return new ModifyRedListDialogFragment();
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Initialize member variables
        db_helper = new dbHelper(this.getActivity(), null, null, 1);
        db_helper.getWritableDatabase();

        // Initialize red list contents
        redListItems = new CharSequence[db_helper.getRestaurantNamesFromList(3).size()];

        // Store red list contents in a local variable to pass in dialog
        for (int i = 0; i < db_helper.getRestaurantNamesFromList(3).size(); i++) {
            redListItems[i] = db_helper.getRestaurantNamesFromList(3).get(i);
        }

        // Boolean values to pass in dialog
        bl = new boolean[redListItems.length];

        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder ad = new AlertDialog.Builder(this.getActivity());

        // If there are no items in the red list
        String dialog_message = "";

        if (db_helper.isDbExist(this.getActivity()) && db_helper.isTableExist(3) &&
                !db_helper.isTableEmpty(3)) {

            ad.setTitle(R.string.dialog_modify_red_list_title);

            // If it's not empty
            ad.setMultiChoiceItems(redListItems,bl, new DialogInterface.OnMultiChoiceClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                    if(isChecked){
                        // If user select a item then add it in selected items
                        selList.add(which);
                    }
                    else if (selList.contains(which))
                    {
                        // if the item is already selected then remove it
                        selList.remove(Integer.valueOf(which));
                    }
                }
            });

            // Removes all items that are selected
            ad.setPositiveButton("Remove", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    msg="";
                    for (int i = 0; i < selList.size(); i++) {

                        msg="\n"+ redListItems[((int) selList.get(i))];

                        // helper method
                        deleteFromRedList(redListItems[((int) selList.get(i))].toString());
                    }
//                    Toast.makeText(getActivity(),
//                            "Removed: \n" + msg, Toast.LENGTH_LONG)
//                            .show();
                }
            });

            // Cancels removal
            ad.setNegativeButton(R.string.generic_cancel_text, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    Toast.makeText(getActivity(),
//                            "Cancelled removal.", Toast.LENGTH_LONG)
//                            .show();
                }
            });
        }

        else {

            ad.setTitle(R.string.generic_info_title);

            // If database does not exist
            if (!db_helper.isDbExist(this.getActivity())) {
                dialog_message = dialog_message.concat(getString((R.string.db_does_not_exist)) + "\n");
            }

            // If database does not exist
            if (!db_helper.isTableExist(3)) {
                dialog_message = dialog_message.concat(getString(R.string.db_table_does_not_exist) + "\n");
            }

            // If table is empty
            if (db_helper.isTableEmpty(3)) {
                dialog_message = dialog_message.concat(getString(R.string.db_table_is_empty) + "\n");
            }

            // Removes all items that are selected
            ad.setPositiveButton(R.string.generic_ok_text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            ad.setMessage(dialog_message);
        }

        return ad.create();
    }

    // Helper method to remove items from the red list used by PositiveButton
    private void deleteFromRedList(String inputText) {
        Context context = this.getActivity().getApplicationContext();

        Restaurant r = new Restaurant();
        r.setRestaurantName(inputText);

        CharSequence text = "Removed " + inputText + " from ignored restaurants.";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
