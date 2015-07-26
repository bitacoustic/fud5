package com.csc413.team5.fud5;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.csc413.team5.fud5.utils.ServiceUtil;
import com.csc413.team5.fud5.utils.ToastUtil;
import com.csc413.team5.restaurantapiwrapper.DistanceUnit;
import com.csc413.team5.restaurantapiwrapper.RestaurantApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = "MainActivity";
    // Declare user settings
    public static final String PREFS_FILE = "UserSettings";
    private SharedPreferences userSettings;
    private SharedPreferences.Editor userSettingsEditor;

    protected String mAddressString;
    protected GoogleApiClient mGoogleApiClient; // client for Google API requests
    protected Location mLastLocation; // stores latitude, longitude of device's last known location
    protected Address mLastLocationAddress; // representation of lat,long as address

    EditText locationInput;
    EditText searchTermInput;
    Spinner radiusSpinner;
    RatingBar starRating;

    ToolTipView toolTipLocationIsEmpty;

    public void btnFindLocation(View v) {
        if (ServiceUtil.isLocationServicesOn(this)) {
            if (ServiceUtil.isNetworkAvailable(this)) {
                // compel location update; result is shown in the location EditText
                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect(); // calls onConnected() to get current location
                if (mAddressString.compareTo("") != 0) {
                    // remove tooltip if it is active and address was obtained
                    if (toolTipLocationIsEmpty != null)
                        toolTipLocationIsEmpty.remove();
                    // set focus on search term
                    searchTermInput.requestFocus();
                }
            } else { // if network is unavailable
                ToastUtil.showShortToast(getApplicationContext(),
                        getString(R.string.toast_network_unavailable));
                Log.i(TAG, "Can't get last known location because the network is unavailable");
            }

        } else { // if location services are inactive
            ToastUtil.showShortToast(getApplicationContext(),
                    getString(R.string.toast_location_services_are_off));
            Log.i(TAG, "Can't get last known location because location services are off");
        }
    }

    public void btnFuDPlz(View v){
        // Save some preferences
        // search radius
        userSettingsEditor.putFloat("defaultSearchRadius",
                Float.parseFloat(((Spinner)findViewById(R.id.spnRadius)).getSelectedItem().toString().substring(0,3))).apply();
//        // star rating
        userSettingsEditor.putFloat("defaultMinStar", starRating.getRating()).apply();

        String location = ((EditText) findViewById(R.id.txtLocation)).getText().toString();
        String searchTerm = ((EditText) findViewById(R.id.txtSearchTerm)).getText().toString();

        // Check for empty location field (location services are likely off). Show tooltip with
        // help text
        if (location.compareTo("") == 0) {
//            ToastUtil.showShortToast(this, "Enter a location");
            Log.i(TAG, "Location field cannot be empty");
            if (toolTipLocationIsEmpty != null)
                toolTipLocationIsEmpty.remove();
            ToolTipRelativeLayout tooltipLocationView
                    = (ToolTipRelativeLayout) findViewById(R.id.tooltipTxtLocation);
            ToolTip tooltipLocation = new ToolTip()
                    .withText(getString(R.string.tooltip_location_field_is_blank))
                    .withColor(Color.parseColor("#ECCD7F"))
                    .withShadow();
            toolTipLocationIsEmpty = tooltipLocationView
                    .showToolTipForView(tooltipLocation, findViewById(R.id.txtLocation));
            return;
        }

        String[] maxRadiusArray = ((Spinner) findViewById(R.id.spnRadius)).getSelectedItem()
                .toString().split(" ");
        int maxRadius = (int) RestaurantApiClient.convertDistanceUnits(Double.parseDouble
                (maxRadiusArray[0]), DistanceUnit.MILES, DistanceUnit.METERS);

        double minRating = ((RatingBar) findViewById(R.id.ratingBar)).getRating();

        Log.i(TAG, "location: " + location);
        Log.i(TAG, "searchTerm: " + searchTerm);
        Log.i(TAG, "maxRadius: " + maxRadius);
        Log.i(TAG, "minRating:" + minRating);

        Intent intent = new Intent(this, ResultPageActivity.class);
        intent.putExtra("location", location);
        intent.putExtra("searchTerm", searchTerm);
        intent.putExtra("maxRadius", maxRadius);
        intent.putExtra("minRating", minRating);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Don't show keyboard immediately
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        String title = getString(R.string.title_activity_main);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#43428A")));
        getSupportActionBar().setTitle(Html.fromHtml("<font color = '#ECCD7F'>" + title + "</font>"));

        setContentView(R.layout.activity_main);

        /* Get shared preferences */
        userSettings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        userSettingsEditor = userSettings.edit();

        /* Initalize inputs*/
        locationInput = (EditText) findViewById(R.id.txtLocation);
        locationInput.setOnClickListener(new View.OnClickListener() {
            @Override
            // If user clicks on the location field and it is non-empty, it is cleared.
            // Also, clears the tooltip if it is active.
            public void onClick(View v) {
                locationInput.setText("");
                if (toolTipLocationIsEmpty != null)
                    toolTipLocationIsEmpty.remove();
            }
        });

        searchTermInput = (EditText) findViewById(R.id.txtSearchTerm);

        /* Initialize radius spinner */
        // TODO: Migrate this to xml file instead
        //Spinner code from Android example
        radiusSpinner = (Spinner) findViewById(R.id.spnRadius);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.radius_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        radiusSpinner.setAdapter(adapter);

        /* Initialize rating bar */
        starRating = (RatingBar) findViewById(R.id.ratingBar);

                /* Set default field values */
        // location input
//        locationInput.setText(userSettings.getString("defaultSearchLocation", "1600 Holloway Ave San Francisco"));

        // radius spinner
        String spinnerValue = String.valueOf(userSettings.getFloat("defaultSearchRadius", 3.0f)).concat(" mi");
        ArrayAdapter radiusSpinnerAdapter = (ArrayAdapter) radiusSpinner.getAdapter();
        int spinnerDefaultPosition = radiusSpinnerAdapter.getPosition(spinnerValue);
        radiusSpinner.setSelection(spinnerDefaultPosition);
        // star rating
        starRating.setRating(userSettings.getFloat("defaultMinStar", 3.5f));
        // TODO search term input
//        searchTermInput.setText(userSettings.getString("defaultSearchLocation", locationInput.getText().toString()));

        // connected with Google Location services
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showUserPreferencesMenu();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showUserPreferencesMenu(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    //Closes keyboard if user clicks off of it
    //Code thanks to Lalit Poptani on stackoverflow.com
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    /*********************
     * Location services *
     *********************/

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

    @Override
    public void onConnected(Bundle bundle) {
        if (!ServiceUtil.isNetworkAvailable(this) || !ServiceUtil.isLocationServicesOn(this))
            return;

        // Get the most recent location of the device (~ user's current location)
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        // get approximate address from location
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            // the last parameter specifies max locations returned; we just need 1
            addresses = geocoder
                    .getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
        } catch (IOException | NullPointerException e ) {
            e.printStackTrace();
        }

        // if Geocoder returned valid address(es), get a 1-line String representation of the first
        // address, which is assumed to be the best approximation; otherwise, set address to be
        // an empty String
        if (addresses != null) {
            mLastLocationAddress = addresses.get(0);
            mAddressString = RestaurantApiClient.addressToString(mLastLocationAddress);
            locationInput.setText(mAddressString);
        } else {
            mAddressString = "";
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
}
