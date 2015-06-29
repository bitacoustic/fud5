package com.csc413.team5.fud5;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.csc413.team5.appdb.dbHelper;
import com.csc413.team5.restaurantapiwrapper.DistanceUnit;
import com.csc413.team5.restaurantapiwrapper.ParameterRangeException;
import com.csc413.team5.restaurantapiwrapper.RestaurantApiClient;
import com.csc413.team5.restaurantapiwrapper.RestaurantList;
import com.csc413.team5.restaurantapiwrapper.YelpApiKey;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;

import java.io.IOException;
import java.text.DecimalFormat;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Tag for Android event logging (viewable in the Android > logcat window in Android Studio)
    // To see the logs with this tag, create a filter (dropdown Edit Filter Configuration)
    protected final String TAG = "FuD5";

    dbHelper db;

    TextView dbTitle;

    YelpApiKey yelpKey; // A Yelp API key that can be constructed in advance of a query
    RestaurantList searchResults; // store results of restaurant API query

    protected GoogleApiClient mGoogleApiClient; // client for Google API requests
    protected Location mLastLocation; // stores latitude, longitude of device's last known location


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new dbHelper(this, null, null, 1);
        dbTitle = (TextView) findViewById(R.id.dbTitle);

        dbTitle.setText(db.getName());

        // build the GoogleApiClient which can be accessed in the current Context
        // TODO will it also be accessible to other activities?
        buildGoogleApiClient();

        // construct a Yelp API key; app's .gitignore in
        // app/src/main/res/values references yelp_api.xml -- store the key values there
        // so they won't be uploaded to the repository
        String consumerKey = getApplicationContext().getResources()
                .getString(R.string.yelp_consumer_key);;
        String consumerSecret = getApplicationContext().getResources()
                .getString(R.string.yelp_consumer_secret);;
        String tokenKey = getApplicationContext().getResources()
                .getString(R.string.yelp_token);;
        String tokenSecret = getApplicationContext().getResources()
                .getString(R.string.yelp_token_secret);;
        yelpKey = new YelpApiKey(consumerKey, consumerSecret, tokenKey, tokenSecret);

    }

    @Override
    public void onConnected(Bundle bundle) {
        // BEGIN TEST CODE

        // call LocationServices API, stores result in mLastLocation
        // TODO I think this can happen any time in this Context, say, after a button onClick()
        // as long as the connection with Google Play services has been established
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        // display the results of the location query
        dbTitle.append("\n\nLocation as of onConnected() is: latitude=" + mLastLocation.getLatitude()
                + ", longitude=" + mLastLocation.getLongitude());

        // call a background task to perform a Yelp search and display the results
        YelpAsyncTask yelpTask = new YelpAsyncTask(); // see YelpAsyncTask below
        yelpTask.execute();

        // END TEST CODE
    }

    /**
     * TEST: A test ASyncTask to make the Yelp API call. Perform the task in the background
     * and display results when complete.
     */
    private class YelpAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params)  {
            // perform the search
            // TODO we need a mechanism to get an approximate address from the device's location
            // to send as the location parameter below
            try {
                searchResults = new RestaurantApiClient.Builder(yelpKey)
                        .location("1600 Holloway Ave, San Francisco, CA")
                        .cll(mLastLocation)
                        .limit(20)
                        .categoryFilter("foodtrucks,restaurants")
                        .term("dumplings")
                        .sort(1) // by distance
                        .build()
                        .getRestaurantList();
                Log.i(TAG, "Created RestaurantList");
            } catch (ParameterRangeException e) {
                Log.i(TAG, "RestaurantApiClient received parameter outside valid range");
                e.printStackTrace();
            } catch (JSONException e) {
                Log.i(TAG, "Result of Yelp API query could not be parsed (JSON error)");
                e.printStackTrace();
            } catch (IOException e) {
                Log.i(TAG, "Result of Yelp API query could not be assembled (object error)");
                e.printStackTrace();
            }

            return "done";
        }

        protected void onPostExecute(String result) {
            // display the names of the restaurants and their distance in miles rounded
            // to 2 decimal places

            DecimalFormat df = new DecimalFormat("####.##");
            dbTitle.append("\n\nNames and distances of Yelp query results: ");
            if(!searchResults.isEmpty()) {
                for (int i = 0; i < searchResults.getSize(); i++)
                    dbTitle.append("[" + searchResults.getRestaurant(i).getBusinessName() + ", "
                            + df.format(searchResults.getRestaurant(i)
                            .getDistanceFromSearchLocation(DistanceUnit.MILES)) + "mi] ");
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect(); // connect with Google Play services
    }

    @Override
    protected void onStop() {
        super.onStop();
        // disconnect from Google Play services if necessary
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Re-establish a connection with GoogleApiClient services if the connected is suspended.
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
}
