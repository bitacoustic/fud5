package com.csc413.team5.fud5;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csc413.team5.restaurantapiwrapper.Restaurant;


/**
 * A placeholder fragment containing a simple view.
 */

//TODO: send restaurant name, rating, picture, and address to result page
public class ResultPageActivityFragment extends Fragment {
    Restaurant mRestaurant;

    public ResultPageActivityFragment() {
        String restName = mRestaurant.getBusinessName();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result_page, container, false);
    }
}
