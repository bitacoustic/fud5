package com.csc413.team5.fud5.tests;

import android.app.Activity;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csc413.team5.appdb.dbHelper;
import com.csc413.team5.fud5.R;
import com.csc413.team5.restaurantapiwrapper.Restaurant;
import com.csc413.team5.restaurantapiwrapper.RestaurantApiClient;
import com.csc413.team5.restaurantapiwrapper.RestaurantList;
import com.csc413.team5.restaurantapiwrapper.YelpApiKey;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RestaurantSelectorTestActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = "RestaurantSelectorTest";

    protected YelpApiKey mYelpApiKey;
    protected RestaurantList mRestaurantList;

    protected dbHelper db;

    protected String mAddressString;
    protected GoogleApiClient mGoogleApiClient; // client for Google API requests
    protected Location mLastLocation; // stores latitude, longitude of device's last known location
    protected Address mLastLocationAddress; // representation of lat,long as address

    protected LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_selector_test);

        // Yelp key
        String consumerKey = getApplicationContext().getResources()
                .getString(R.string.yelp_consumer_key);
        String consumerSecret = getApplicationContext().getResources()
                .getString(R.string.yelp_consumer_secret);
        String tokenKey = getApplicationContext().getResources()
                .getString(R.string.yelp_token);
        String tokenSecret = getApplicationContext().getResources()
                .getString(R.string.yelp_token_secret);
        mYelpApiKey = new YelpApiKey(consumerKey, consumerSecret, tokenKey, tokenSecret);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayoutRSTest);

        db = new dbHelper(this, null, null, 1);

        appendOutputText("Getting nearby restaurants ...");
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    public class RestaurantListTask extends AsyncTask<Void, Void, RestaurantList> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected RestaurantList doInBackground(Void... params) {
            RestaurantList result = null;
            try {
                result = new RestaurantApiClient.Builder(mYelpApiKey)
                        .location(mAddressString)
                        .cll(mLastLocation)
                        .build().getRestaurantList();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         * <p/>
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param result The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */
        @Override
        protected void onPostExecute(RestaurantList result) {
            // do some stuff after the restaurant list has been generated

            mRestaurantList = result;

            ArrayList<Integer> randoms = new ArrayList<>();
            int numRandoms = mRestaurantList.size() / 2;
            Random rand = new Random();

            appendOutputText("Selecting random restaurants to assign to the green, yellow, and " +
                    "red lists. The remaining restaurants are not assigned to any list (white " +
                    "text). NOTE: If any of these restaurants were already in the database, " +
                    "their list status may be reset. The results of this activity persist in " +
                    "the database after the activity or application is destroyed. To fully " +
                    "clear all lists on this device/emulator, uninstall the app.\n");

            for (int i = 0; i < numRandoms; i++) {
                int randomNumber;
                do {
                    randomNumber = rand.nextInt(mRestaurantList.size()); // 0..size-1
                } while (randoms.contains(randomNumber));
                randoms.add(randomNumber);
            }

            appendOutputText("Result:");
            if (mRestaurantList != null) {
                for (int i = 0; i < mRestaurantList.size(); i++) {
                    Restaurant thisRestaurant = mRestaurantList.getRestaurant(i);

                    // if this restaurant is currently in any list, make it unlisted
                    if (db.isRestaurantInList(thisRestaurant, 1))
                        db.deleteRestaurantFromList(thisRestaurant, 1);
                    if (db.isRestaurantInList(thisRestaurant, 2))
                        db.deleteRestaurantFromList(thisRestaurant, 2);
                    if (db.isRestaurantInList(thisRestaurant, 3))
                        db.deleteRestaurantFromList(thisRestaurant, 3);

                    // add the restaurant to a list if it was randomly selected; first third
                    // of the restaurants in the list of randomly selected index numbers
                    // go in green, second third in yellow, last third in red
                    if (randoms.contains(i)) {
                        if (randoms.indexOf(i) < (randoms.size() / 3)) {
                            appendOutputText(thisRestaurant.getBusinessName(), Color.GREEN);
                            // green list = 1
                            db.insertRestaurantToList(thisRestaurant, 1);
                        } else if (randoms.indexOf(i) >= (randoms.size() / 3) &&
                                randoms.indexOf(i) < (randoms.size() * 2 / 3)) {
                            appendOutputText(thisRestaurant.getBusinessName(), Color.YELLOW);
                            // yellow list = 2
                            db.insertRestaurantToList(thisRestaurant, 2);
                        } else if (randoms.indexOf(i) >= (randoms.size() * 2 / 3)) {
                            appendOutputText(thisRestaurant.getBusinessName(), Color.RED);
                            // yellow list = 3
                            db.insertRestaurantToList(thisRestaurant, 3);
                        }

                    } else {
                        appendOutputText(thisRestaurant.getBusinessName(),
                                Color.WHITE);
                    }

                }
            } else {
                appendOutputText("No results.");
            }

            RestaurantList resultList = selector(mRestaurantList, 4.0);

            appendOutputText(" ");
            appendOutputText("Result of selector is:");
            for (int i = 0; i < resultList.getSize(); i++) {
                appendOutputText(resultList.getRestaurant(i).getBusinessName());
            }

        }
    }

    // assumption: input restaurant list is sorted by distance
    public RestaurantList selector(RestaurantList rList, double minRating) {
        if (rList.getSize() < 1)
            // TODO
        if (rList.getSize() == 1)
            return rList;

//
//        RestaurantList resultList = new RestaurantList();
//
//        for (int i = 0; i < rList.getSize(); i++) {
//            if (rList.getRestaurant(i).getRating() >= minRating)
//                resultList.add(rList.getRestaurant(i));
//        }

        boolean done = false;

        while (!done) {
            for (int i = 0; i < rList.getSize(); i++) {
                if (rList.getRestaurant(i).getRating() < minRating) {
                    rList.removeRestaurant(i);
                    break;
                }
                else if (i == rList.getSize() - 1)
                    done = true;
            }
        }

        return rList;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_restaurant_selector_test, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onConnected(Bundle bundle) {

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
        if (addresses != null)
            mLastLocationAddress = addresses.get(0);
        mAddressString = RestaurantApiClient.addressToString(mLastLocationAddress);

        new RestaurantListTask().execute();
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

    public void appendOutputText(String s, int color) {
        TextView txtOut = new TextView(getBaseContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 0, 10, 0);
        txtOut.setTextColor(color);
        txtOut.setTextSize(14);
        txtOut.setText(s);
        txtOut.setLayoutParams(params);
        linearLayout.addView(txtOut);
    }

    public void appendOutputText(String s) {
        appendOutputText(s, Color.WHITE);
    }
}
