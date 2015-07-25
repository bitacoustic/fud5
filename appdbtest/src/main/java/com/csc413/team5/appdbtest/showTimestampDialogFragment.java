package com.csc413.team5.appdbtest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.EditText;

import com.csc413.team5.appdb.dbHelper;
import com.csc413.team5.restaurantapiwrapper.Restaurant;

/**
 * A dialog fragment that shows a restaurant's timestamp based on input
 * Created by niculistana on 7/21/15.
 */
public class showTimestampDialogFragment extends DialogFragment {
    dbHelper db;

    public static showTimestampDialogFragment newInstance() {
        showTimestampDialogFragment showTimestampDialog = new showTimestampDialogFragment();
        return showTimestampDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        db = new dbHelper(this.getActivity(), null, null, 1);

        EditText inputText = (EditText) this.getActivity().findViewById(R.id.restaurantInput);
        final String restaurantInput = inputText.getText().toString();

        Restaurant r = new Restaurant();
        r.setRestaurantName(restaurantInput);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(inputText.getText())
                .setMessage("Timestamp: " + db.getRestaurantTimeStampFromList(r, db.getRestaurantListColor(r)));
        return builder.create();
    }
}
