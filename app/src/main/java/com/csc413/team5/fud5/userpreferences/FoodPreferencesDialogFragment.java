package com.csc413.team5.fud5.userpreferences;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.csc413.team5.fud5.R;

public class FoodPreferencesDialogFragment extends DialogFragment {

    // Member variables
    private boolean location_services;
    private double minimum_star_rating;
    private boolean location_search;
    private double search_radius;


    static FoodPreferencesDialogFragment newInstance(boolean isLocationServices, double minumumStarRating,
                                                     boolean isLocationSearch, double searchRadius) {
        FoodPreferencesDialogFragment foodPreferences = new FoodPreferencesDialogFragment();

        Bundle args = new Bundle();
        args.putBoolean("location_services", isLocationServices);
        args.putDouble("minimum_star_rating", minumumStarRating);
        args.putBoolean("location_search", isLocationSearch);
        args.putDouble("search_radius", searchRadius);

        foodPreferences.setArguments(args);
        return foodPreferences;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Initializes arguments to SQL table default values
        // TODO Call SQL to grab default food preferences
        this.getArguments().getBoolean("location_services", true);
        this.getArguments().getDouble("minimum_star_rating", 3.5);
        this.getArguments().getBoolean("location_search", true);
        this.getArguments().getDouble("search_radius", .5);

        // Assigns retrieved SQL table default values to local member variables
        location_services = this.getArguments().getBoolean("location_services");
        minimum_star_rating = this.getArguments().getDouble("minimum_star_rating");
        location_search = this.getArguments().getBoolean("location_search");
        search_radius = this.getArguments().getDouble("search_radius");

        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.dialog_food_preferences_message);

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO: New settings won't be saved
            }
        });

        builder.create();
        return super.onCreateDialog(savedInstanceState);
    }
}
