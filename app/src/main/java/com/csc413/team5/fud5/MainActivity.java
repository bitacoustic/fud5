package com.csc413.team5.fud5;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import com.csc413.team5.fud5.utils.ToastUtil;
import com.csc413.team5.restaurantapiwrapper.DistanceUnit;
import com.csc413.team5.restaurantapiwrapper.RestaurantApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = "MainActivity";

    protected String mAddressString;
    protected GoogleApiClient mGoogleApiClient; // client for Google API requests
    protected Location mLastLocation; // stores latitude, longitude of device's last known location
    protected Address mLastLocationAddress; // representation of lat,long as address

    protected EditText mEditLocation;

    public void btnFindLocation(View v) {
        if (islocationServicesOn()) {
            if (isNetworkAvailable()) {
                // compel location update
                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect(); // calls onConnected() to get current location

                if (mAddressString.compareTo("") != 0)
                    mEditLocation.setText(mAddressString);
            } else {
                ToastUtil.showShortToast(getApplicationContext(),
                        getString(R.string.toast_network_unavailable));
                Log.i(TAG, "Can't get last known location because the network is unavailable");
            }

        } else {
            ToastUtil.showShortToast(getApplicationContext(),
                    getString(R.string.toast_location_services_are_off));
            Log.i(TAG, "Can't get last known location because location services are off");
        }
    }

    public void btnFuDPlz(View v){

        String location = ((EditText) findViewById(R.id.txtLocation)).getText().toString();
        String searchTerm = ((EditText) findViewById(R.id.txtSearchTerm)).getText().toString();

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

        setContentView(R.layout.activity_main);

        //Spinner code from Android example
        Spinner spinner = (Spinner) findViewById(R.id.spnRadius);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.radius_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // ref to Location EditText that can be accessed throughout the activity
        mEditLocation = (EditText) findViewById(R.id.txtLocation);

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
        Intent intent = new Intent(this, UserPreferencesActivity.class);
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
        if (!islocationServicesOn() && !isNetworkAvailable())
            return;

        // Get the most recent location of the device (~ user's current location)
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        // get approximate address from location
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


    public boolean islocationServicesOn() {
        LocationManager lm = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        // check whether GPS and network providers are enabled
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) { }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) { }

        // only show dialog if location services are not enabled
        return (gpsEnabled || networkEnabled);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
