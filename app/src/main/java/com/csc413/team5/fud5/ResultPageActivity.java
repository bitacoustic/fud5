package com.csc413.team5.fud5;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class ResultPageActivity extends AppCompatActivity {
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_result_page);
        setUpMapIfNeeded();
    }
/*
        setContentView(R.layout.activity_result_page);
        Typeface buttonFont = Typeface.createFromAsset(getAssets(), "Chunkfive.otf");
        Button greenButton = (Button) findViewById(R.id.greenButton);
        greenButton.setTypeface(buttonFont);
        //TextView myTextView = (TextView)findViewById(R.id.greenButton);
       // myTextView.setTypeface(buttonFont);
*/

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

        LatLng sfsu = new LatLng(37.782458, -122.392828); //test latitude longitude

        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sfsu, 13));//sets the view

        mMap.addMarker(new MarkerOptions()
                .position(sfsu)
                .title("New Location"));


    }

    protected void onResume() {
        super.onResume();
    }


}
