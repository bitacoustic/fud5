package com.csc413.team5.fud5.userpreferences;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.csc413.team5.fud5.R;
import com.csc413.team5.fud5.utils.ToastUtil;

public class FoodPreferencesDialogFragment extends DialogFragment {

    // Member variables
    private boolean location_services;
    private double minimum_star_rating;
    private boolean location_search;
    private double search_radius;


    public static FoodPreferencesDialogFragment newInstance(boolean isLocationServices, double minimumStarRating,
                                                     boolean isLocationSearch, double searchRadius) {
        FoodPreferencesDialogFragment foodPreferences = new FoodPreferencesDialogFragment();

        Bundle args = new Bundle();
        args.putBoolean("location_services", isLocationServices);
        args.putDouble("minimum_star_rating", minimumStarRating);
        args.putBoolean("location_search", isLocationSearch);
        args.putDouble("search_radius", searchRadius);

        foodPreferences.setArguments(args);
        return foodPreferences;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Assigns retrieved SharedPreferences default values to local member variables
        location_services = this.getArguments().getBoolean("location_services");
        minimum_star_rating = this.getArguments().getDouble("minimum_star_rating");
        location_search = this.getArguments().getBoolean("location_search");
        search_radius = this.getArguments().getDouble("search_radius");

        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        /* Build AlertDialog */
        // Instantiate alert dialog
        final AlertDialog ad = new AlertDialog.Builder(this.getActivity())
        // Show title
        .setTitle(R.string.dialog_food_preferences_title)
        // Show message
        .setMessage(R.string.dialog_food_preferences_message)


        .setPositiveButton(R.string.generic_save_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ToastUtil.showShortToast(getActivity(), "Settings saved.");
            }
        })

        .setNegativeButton(R.string.generic_cancel_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ToastUtil.showShortToast(getActivity(), "Confirm cancel");
            }
        })

        // create alert dialog
        .create();

        /* End Build AlertDialog */
        ad.setCanceledOnTouchOutside(true);

        return ad;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // Confirm cancellation
        CancelDialogFragment cancelDialog = new CancelDialogFragment();
        cancelDialog.show(getFragmentManager(), "Cancel Confirmation");
    }
}
