package com.csc413.team5.fud5.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
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

        if (instance == null)
            instance = new MoreInfoDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable("restaurant", r);
        instance.setArguments(args);

        return instance;
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

        /* display restaurant information (if available)*/

        // Yelp attribution logo; according to documentation, it must appear before API content
        ImageView yelpLogo = new ImageView(mContext);
        LinearLayout.LayoutParams yelpLogoParams = new LinearLayout.LayoutParams(
                100, 50);
        yelpLogoParams.setMargins(0, 0, 0, 10);
        yelpLogo.setLayoutParams(yelpLogoParams);
        yelpLogo.setImageResource(R.drawable.yelp_logo_50x25);
        linearLayout.addView(yelpLogo);

        // distance; only display if distance is non-zero (when search is finer than city-scope)
        double distance = new BigDecimal(mRestaurant
                .getDistanceFromSearchLocation(DistanceUnit.MILES))
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
        if (distance > 0) {
            appendOutputText(mContext.getString(R.string.fragment_more_info_title_distance),
                    Color.BLACK, 15, 0);
            appendOutputText(distance + " mi");
        }

        // categories
        if (mRestaurant.hasCategories()) {
            appendOutputText(mContext.getString(R.string.fragment_more_info_title_categories),
                    Color.BLACK, 15, 10);
            StringBuilder categories = new StringBuilder("");
            for (int i = 0; i < mRestaurant.numCategories(); i++) {
                categories.append(mRestaurant.getCategories().get(i).getName());
                if (i < mRestaurant.numCategories() - 1)
                    categories.append(", ");
            }
            appendOutputText(categories.toString());
        }

        // address
        if (mRestaurant.getAddressDisplay().compareTo("") != 0) {
            appendOutputText(mContext.getString(R.string.fragment_more_info_title_address),
                    Color.BLACK, 15, 10);
            appendOutputText(mRestaurant.getAddressDisplay());
        }

        // phone number (display the number, or if device is a phone, click to display phone
        // number in dialer)
        if (mRestaurant.getPhoneDisplay().compareTo("") != 0) {
            appendOutputText(mContext.getString(R.string.fragment_more_info_title_phone_number),
                    Color.BLACK, 15, 10);
            TelephonyManager manager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
            if (manager.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE) { // if device is phone
                TextView phoneNumber = appendOutputText(mRestaurant.getPhoneDisplay(), Color
                        .BLUE, 13, 0);
                phoneNumber.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                phoneNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // display the phone number in the dialer, but don't call it
                        Intent call = new Intent(Intent.ACTION_DIAL);
                        call.setData(mRestaurant.getPhoneDialable());
                        startActivity(call);

                    }
                });
            } else {
                appendOutputText(mRestaurant.getPhoneDisplay());
            }
        }

        // SeatMe reservation link if available
//        if (mRestaurant.hasSeatMeUrl()) {
//            appendOutputText('\n' + mContext.getString(R.string.fragment_more_info_reserve_a_table),
//                    Color.BLACK, 15, 10);
//            TextView reservationUrl = appendOutputText(mContext.getString(R.string
//                    .fragment_more_info_open_seatme_link), Color.BLUE, 13, 0);
//            reservationUrl.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent openBrowser = new Intent(Intent.ACTION_VIEW);
//                    openBrowser.setData(mRestaurant.getSeatMeUrl());
//                    startActivity(openBrowser);
//                }
//            });
//        }

        // if Locu information exists, display it and the attribution logo
        if (mRestaurant.hasHours() || mRestaurant.getLocuWebsiteUrl() != null) {
            // add divider line
            ImageView divider = new ImageView(v.getContext());
            LinearLayout.LayoutParams paramsDivider = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 5);
            paramsDivider.setMargins(0, 35, 0, 10);
            divider.setLayoutParams(paramsDivider);
            divider.setBackgroundColor(Color.DKGRAY);
            linearLayout.addView(divider);

            // open hours
            if (mRestaurant.hasHours()) {
                String hoursString = mRestaurant.getHours().toString();
                // if hours string actually has hours to display
                if (hoursString.length() > 112) {
                    appendOutputText(mContext.getString(R.string.fragment_more_info_hours_title),
                            Color.BLACK, 15, 10);
                    appendOutputText(mRestaurant.getHours().toString());
                }
            }

            // website
            if (mRestaurant.getLocuWebsiteUrl() != null) {
                appendOutputText(mContext.getString(R.string.fragment_more_info_website_title)
                        , Color.BLACK, 15, 10);
                TextView website = appendOutputText(mRestaurant.getLocuWebsiteUrl().toString(),
                        Color.BLUE, 13, 0);
                website.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                website.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent displayWebsite = new Intent(Intent.ACTION_VIEW);
                        displayWebsite.setData(mRestaurant.getLocuWebsiteUrl());
                        startActivity(displayWebsite);
                    }
                });
            }

            // attribution logo; according to Locu documentation, must appear after the API content
            ImageView locuLogo = new ImageView(mContext);
            locuLogo.setImageResource(R.drawable.poweredbylocu_color);
            LinearLayout.LayoutParams locuLogoParams = new LinearLayout.LayoutParams(
                    233, 38);
            locuLogoParams.setMargins(0, 25, 0, 10);
            locuLogo.setLayoutParams(locuLogoParams);
            linearLayout.addView(locuLogo);
        }

        // if weather information exists, display it
        if (!mRestaurant.getWeather().equals("")) {
            appendOutputText(mContext.getString(R.string.fragment_more_info_title_weather),
                    Color.BLACK, 15, 10);
            String currentWeather = mRestaurant.getWeather();
            appendOutputText(currentWeather);
        }

        return v;
    }


    /**
     * Add a TextView to the LinearLayout contained within the activity's ScrollView
     * @param s      text to add
     * @param color  the color: an int or use e.g. Color.RED
     * @return   the created TextView
     */
    public TextView appendOutputText(String s, int color, float txtSize, int marginTop) {
        TextView txtOut = new TextView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, marginTop, 0, 0);
        txtOut.setTextColor(color);
        txtOut.setTextSize(txtSize);
        txtOut.setText(s);
        txtOut.setLayoutParams(params);
        linearLayout.addView(txtOut);
        return txtOut;
    }

    /**
     * Overloaded version of appendOutputText(String s, int color) that appends text with default
     * body style (dark grey, 14pt, no top margin)
     * @param s  text to add
     * @return   the created TextView
     */
    public TextView appendOutputText(String s) {
        return appendOutputText(s, Color.DKGRAY, 13, 0);
    }
}
