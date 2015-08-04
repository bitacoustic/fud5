package com.csc413.team5.fud5.tests;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.csc413.team5.fud5.R;
import com.csc413.team5.fud5.utils.ServiceUtil;
import com.csc413.team5.restaurantapiwrapper.DistanceUnit;
import com.csc413.team5.restaurantapiwrapper.RestaurantApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SelectorDemoActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "SelectorDemo";
    RatingBar starRating;
    TextView txtDebug;
    EditText editLocation, editSearchTerm, editMaxRadius;
    Button btnFoodPlz;

    protected String mAddressString;
    protected GoogleApiClient mGoogleApiClient; // client for Google API requests
    protected Location mLastLocation; // stores latitude, longitude of device's last known location
    protected Address mLastLocationAddress; // representation of lat,long as address

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector_demo);

        editLocation = (EditText) findViewById(R.id.editTextRSDLocation);
        editSearchTerm = (EditText) findViewById(R.id.editTextRSDSearchTerm);
        editMaxRadius = (EditText) findViewById(R.id.editTextRSDMaxRadius);
        starRating = (RatingBar) findViewById(R.id.ratingBarRSD);

        btnFoodPlz = (Button) findViewById(R.id.buttonRSDFuzPlz);
        btnFoodPlz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickButtonFudPlz();
            }
        });

        txtDebug = (TextView) findViewById(R.id.textViewRSDDebug);

        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    /**
     * Check if location services are active. If so, get the current location then call
     * the RestaurantListTask background task to perform the Yelp API query.
     */
    @Override
    public void onConnected(Bundle bundle) {
        if (!ServiceUtil.isLocationServicesOn(this)) {
            txtDebug.setText("Location services are off");
        } else {
            // get approximate address from location
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(this, Locale.getDefault());
            try {
                // the last parameter specifies max locations turn return; we just need 1
                addresses = geocoder
                        .getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null) {
                mLastLocationAddress = addresses.get(0);
                mAddressString = RestaurantApiClient.addressToString(mLastLocationAddress);
                editLocation.setText(mAddressString);
            } else {
                txtDebug.setText("Network is unavailable");
            }
        }
    }

    /**
     * Re-establish a connection with GoogleApiClient services if the connected is suspended.
     *
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Google Play services connection suspended. Reconnecting...");
        mGoogleApiClient.connect();
    }

    /**
     * See ConnectionResult documentation for possible error codes.
     *
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Google Play services connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    /*****************************
     * Activity's helper methods *
     *****************************/

    /**
     * Builds a GoogleApiClient, adds API LocationServices.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void onClickButtonFudPlz() {
        String location = editLocation.getText().toString();
        if (location.compareTo("") == 0) {
            txtDebug.setText("Location field must be non-empty");
            return;
        }

        String searchTerm = editSearchTerm.getText().toString();
        if (searchTerm.compareTo("") == 0)
            searchTerm = "food";

        if (editMaxRadius.getText().toString().compareTo("") == 0) {
            txtDebug.setText("Max Radius field must be non-empty");
            return;
        }
        int maxRadius = (int) RestaurantApiClient
                .convertDistanceUnits(Double.parseDouble(editMaxRadius.getText().toString()),
                        DistanceUnit.MILES, DistanceUnit.METERS);
        if (maxRadius > 40000) {
            txtDebug.setText("Max radius allowed is 24.85 miles");
            return;
        }

        double minRating = starRating.getRating();

        Intent intent = new Intent(this, SelectorDemoResultsActivity.class);
        intent.putExtra("location", location);
        intent.putExtra("searchTerm", searchTerm);
        intent.putExtra("maxRadius", maxRadius);
        intent.putExtra("minRating", minRating);
        startActivity(intent);
    }
}