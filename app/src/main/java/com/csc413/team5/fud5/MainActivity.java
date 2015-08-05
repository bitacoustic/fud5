package com.csc413.team5.fud5;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
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

import com.csc413.team5.appdb.dbHelper;
import com.csc413.team5.fud5.dialogs.GreenFollowupDialogFragment;
import com.csc413.team5.fud5.utils.AppSettingsHelper;
import com.csc413.team5.fud5.utils.Constants;
import com.csc413.team5.fud5.utils.ServiceUtil;
import com.csc413.team5.fud5.utils.ToastUtil;
import com.csc413.team5.restaurantapiwrapper.DistanceUnit;
import com.csc413.team5.restaurantapiwrapper.Restaurant;
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

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GreenFollowupDialogFragment.GreenFollowupDialogListener {
    public static final String TAG = "MainActivity";

    protected String mAddressString;
    protected GoogleApiClient mGoogleApiClient; // client for Google API requests
    protected Location mLastLocation; // stores latitude, longitude of device's last known location
    protected Address mLastLocationAddress; // representation of lat,long as address

    // user input fields
    EditText locationInput, searchTermInput;
    Spinner radiusSpinner;
    RatingBar starRating;

    ToolTipView toolTipLocationIsEmpty;

    DialogFragment greenFollowupDialog;
    dbHelper db;

    /* *************** */
    /* ONCLICK METHODS */
    /* *************** */

    /**
     * Behavior for the crosshair button next to the location textedit field.
     * @param v calling view
     */
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

    /**
     * Behavior for the big Fud Plz button.
     * @param v calling view
     */
    public void btnFuDPlz(View v){
        // Save some preferences
        // search radius
        AppSettingsHelper.setDefaultRadiusValue((
                (Spinner) findViewById(R.id.spnRadius)).getSelectedItem()
                .toString().split(" ")[0]);
//        AppSettingsHelper.setDefaultRadiusValue(Float.parseFloat(radiusSpinner.getSelectedItem().toString().substring(0, 3)));
        // star rating
        AppSettingsHelper.setDefaultStarRating(starRating.getRating());

        String location = ((EditText) findViewById(R.id.txtLocation)).getText().toString();
        // appending a minimum search term of food; anything else that the user may enter is
        // optional

        String searchTerm;
        // if empty search term
        if (((EditText) findViewById(R.id.txtSearchTerm)).getText()
                .toString().equals("")) {
            // set to "food"
            searchTerm = "food";
        } else {
            searchTerm = ((EditText) findViewById(R.id.txtSearchTerm)).getText()
                    .toString();
        }

        //ToastUtil.showShortToast(this, searchTerm);

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
            return; // don't proceed to the results page
        }

        // max radius; calculate value in meters from the spinner choice in miles
        String[] maxRadiusArray = ((Spinner) findViewById(R.id.spnRadius)).getSelectedItem()
                .toString().split(" ");
        int maxRadius = (int) RestaurantApiClient.convertDistanceUnits(Double.parseDouble
                (maxRadiusArray[0]), DistanceUnit.MILES, DistanceUnit.METERS);

        // minimum rating
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

    /* ************************** */
    /* ACTIVITY LIFECYCLE METHODS */
    /* ************************** */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Don't show keyboard immediately
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        String title = getString(R.string.title_activity_main);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#43428A")));
        getSupportActionBar().setTitle(Html.fromHtml("<font color = '#ECCD7F'>" + title + "</font>"));

        setContentView(R.layout.activity_main);

        /* Get shared preferences */
        AppSettingsHelper.init(this);

        /* Initialize the database */
        db = new dbHelper(this, null, null, Constants.RESTAURANT_LISTS_TABLE_ID);

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

        // radius spinner
        String spinnerValue = AppSettingsHelper.getDefaultRadiusValue() + " mi";
        ArrayAdapter radiusSpinnerAdapter = (ArrayAdapter) radiusSpinner.getAdapter();
        int spinnerDefaultPosition = radiusSpinnerAdapter.getPosition(spinnerValue);
        radiusSpinner.setSelection(spinnerDefaultPosition);
        // star rating
        starRating.setRating(AppSettingsHelper.getStarRating());

        // connected with Google Location services
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient != null & ServiceUtil.isNetworkAvailable(this))
            mGoogleApiClient.reconnect();

        if (greenFollowupDialog == null || !greenFollowupDialog.isVisible()) {
            long lastGreenTime = AppSettingsHelper.getLastGreenRestaurantTimestamp();
            if (lastGreenTime > 0) {
                long greenTimerExpiry = lastGreenTime
                        + (AppSettingsHelper.getGreenFollowupInterval() * 3600000);
                if (greenTimerExpiry < System.currentTimeMillis()) {
                    String lastGreenID = AppSettingsHelper.getLastGreenRestaurantID();
                    if (lastGreenID.compareTo("") != 0) {
                        greenFollowupDialog =
                                GreenFollowupDialogFragment.getInstance(lastGreenID, lastGreenTime);
                        greenFollowupDialog.setCancelable(false);
                        greenFollowupDialog.show(getFragmentManager(), "greenFollowupDialog");
                        // button click in dialog is handled by the dialog's listener methods
                        // implemented below
                    }
                }
            }
        }

    }

    /**
     * Save state of green followup dialog fragment if it is active. This ensures it renders
     * properly and the app does not crash onResume(). This method is called by onPause().
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // save the state of the info dialog if it is visible
        if (greenFollowupDialog != null && greenFollowupDialog.isVisible())
            getFragmentManager().putFragment(outState, "greenFollowUp", greenFollowupDialog);
    }

    /**
     * Restore state of green followup dialog. This method is called by onRestore().
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // restore the state of the info dialog if it was open while the activity was paused
        // if it wasn't open this operation shouldn't affect the activity (mMoreInfoDialog == null)
        greenFollowupDialog = (DialogFragment) getFragmentManager().getFragment(savedInstanceState,
                "greenFollowUp");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Called when the gear icon is clicked.
     * @param item  the title bar icon selected
     * @return true if the button press was handled, otherwise false
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showSettingsActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSettingsActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    //Closes keyboard if user clicks off of it
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    /* ***************** */
    /* LOCATION SERVICES */
    /* ***************** */

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

    /**
     * Gets the device's last known location (effectively, the device's current location at
     * the time this method is called). Called when a connection to Google Play location services
     * is established.
     * @param bundle state/arguments
     */
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
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Google Play services connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    /* ********************************** */
    /* GREEN-LIST FOLLOWUP DIALOG BUTTONS */
    /* ********************************** */

    /**
     * Restaurant remains in green list. Clear the restaurant from user settings so we don't ask
     * about it again.
     * @param dialog the calling dialog fragment
     */
    @Override
    public void OnGreenFollowupClickedGreen(DialogFragment dialog) {
        ToastUtil.showLongToast(this, "Thanks for the feedback! We're glad you enjoyed " + AppSettingsHelper
                .getLastGreenRestaurantID() + ".");
        AppSettingsHelper.clearLastGreenRestaurant();
        Log.i(TAG, "Liked last green-listed restaurant; it remains in green list");
        greenFollowupDialog.dismiss();
    }

    /**
     * Restaurant remains in user settings so user is reprompted at the next onResume().
     * @param dialog the calling dialog fragment
     */
    @Override
    public void OnGreenFollowupClickedYellow(DialogFragment dialog) {
        ToastUtil.showLongToast(this, "Thanks for the feedback about "
                + AppSettingsHelper.getLastGreenRestaurantID() + ".");
        Restaurant r = new Restaurant();
        r.setRestaurantName(AppSettingsHelper.getLastGreenRestaurantID());
        if (db.isRestaurantInList(r, Constants.GREEN_LIST))
            db.deleteRestaurantFromList(r, Constants.GREEN_LIST);
        Log.i(TAG, "Removed " + r.getBusinessName() + " from green list");
        if (!db.isRestaurantInList(r, Constants.YELLOW_LIST))
            db.insertRestaurantToList(r, Constants.YELLOW_LIST);
        Log.i(TAG, "Added " + r.getBusinessName() + " to yellow list");
        AppSettingsHelper.clearLastGreenRestaurant();
        Log.i(TAG, "Moved the restaurant from green to yellow list");
        greenFollowupDialog.dismiss();
    }

    /**
     * Remove restaurant from green list and add it to red list.
     * @param dialog the calling dialog fragment
     */
    @Override
    public void OnGreenFollowupClickedRed(DialogFragment dialog) {
        // remove the restaurant from the green list and add it to the red list instead
        Restaurant r = new Restaurant();
        ToastUtil.showLongToast(this, "Thanks for the feedback! We won't show you " +
                AppSettingsHelper.getLastGreenRestaurantID() + " again.");
        r.setRestaurantName(AppSettingsHelper.getLastGreenRestaurantID());
        if (db.isRestaurantInList(r, Constants.GREEN_LIST))
            db.deleteRestaurantFromList(r, Constants.GREEN_LIST);
        Log.i(TAG, "Removed " + r.getBusinessName() + " from green list");
        if (!db.isRestaurantInList(r, Constants.RED_LIST))
            db.insertRestaurantToList(r, Constants.RED_LIST);
        Log.i(TAG, "Added " + r.getBusinessName() + " to red list");
        AppSettingsHelper.clearLastGreenRestaurant();
        greenFollowupDialog.dismiss();
    }
}
