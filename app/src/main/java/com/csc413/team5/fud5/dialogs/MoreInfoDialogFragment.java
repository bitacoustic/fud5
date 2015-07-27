package com.csc413.team5.fud5.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csc413.team5.fud5.R;
import com.csc413.team5.restaurantapiwrapper.DistanceUnit;
import com.csc413.team5.restaurantapiwrapper.Restaurant;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoreInfoDialogFragment extends DialogFragment {
    private static MoreInfoDialogFragment instance = null;
    Context mContext;

    Restaurant mRestaurant; // the restaurant for which to display the menu

    TextView txtRestaurantName;
    LinearLayout linearLayout;

    public MoreInfoDialogFragment() { // defeat instantiation
    }

    public static MoreInfoDialogFragment getInstance(Restaurant r) {
        if (r == null)
            return null;

        instance = new MoreInfoDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable("restaurant", r);
        instance.setArguments(args);

        return instance;
    }

    void dismissDialog(View v) {
        dismiss();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load the restaurant argument
        mRestaurant = (Restaurant) getArguments().getSerializable("restaurant");
        // make context available throughout the class
        mContext = getActivity();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_more_info, container, false);

        // set restaurant title
        txtRestaurantName = (TextView) v.findViewById(R.id.textViewMoreInfoTitle);
        txtRestaurantName.setText(mRestaurant.getBusinessName());

        linearLayout = (LinearLayout) v.findViewById(R.id
                .linearLayoutMoreInfoBody);

        ImageButton imageButtonDismissDialog = (ImageButton) v.findViewById(R.id
                .imageButtonMoreInfo);
        imageButtonDismissDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // display restaurant information

        appendOutputText(new BigDecimal(mRestaurant.getDistanceFromSearchLocation(DistanceUnit
                .MILES)).setScale(2, RoundingMode.HALF_UP) + " mi");

        // categories
        if (mRestaurant.hasCategories()) {
            StringBuilder categories = new StringBuilder("");
            for (int i = 0; i < mRestaurant.numCategories(); i++) {
                categories.append(mRestaurant.getCategories().get(i).getName());
                if (i < mRestaurant.numCategories() - 1)
                    categories.append(", ");
            }
            appendOutputText(categories.toString());
        }

        appendOutputText(mRestaurant.getAddressDisplay());
        appendOutputText(mRestaurant.getPhoneDisplay()); // can be made dialable

        if (mRestaurant.hasSeatMeUrl()) {
            appendOutputText('\n' + "Reserve a table", Color.BLACK, 16);
            appendOutputText(mRestaurant.getSeatMeUrl().toString()); // can be made openable
        }


        return v;
    }

    /**
     * Add a TextView to the LinearLayout contained within the activity's ScrollView
     * @param s      text to add
     * @param color  the color: an int or use e.g. Color.RED
     */
    public void appendOutputText(String s, int color, float txtSize) {
        TextView txtOut = new TextView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 0, 10, 0);
        txtOut.setTextColor(color);
        txtOut.setTextSize(txtSize);
        txtOut.setText(s);
        txtOut.setLayoutParams(params);
        linearLayout.addView(txtOut);
    }

    /**
     * Overloaded version of appendOutputText(String s, int color) that appends white text.
     * @param s  text to add
     */
    public void appendOutputText(String s) {
        appendOutputText(s, Color.DKGRAY, 14);
    }
}
