package com.csc413.team5.fud5.tests;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csc413.team5.appdb.dbHelper;
import com.csc413.team5.fud5.R;
import com.csc413.team5.fud5.utils.Constants;
import com.csc413.team5.fud5.utils.ServiceUtil;
import com.csc413.team5.fud5.utils.ToastUtil;
import com.csc413.team5.restaurantapiwrapper.DistanceUnit;
import com.csc413.team5.restaurantapiwrapper.Restaurant;
import com.csc413.team5.restaurantapiwrapper.RestaurantApiClient;
import com.csc413.team5.restaurantapiwrapper.RestaurantList;
import com.csc413.team5.restaurantapiwrapper.YelpApiKey;

import java.sql.Timestamp;
import java.util.Random;

public class SelectorDemoResultsActivity extends AppCompatActivity {
    public static final String TAG = "SelectorDemo";
    Context mContext;

    YelpApiKey mYelpKey;
    dbHelper db;

    LinearLayout mLinearLayoutResults;
    TextView loading;

    String location, searchTerm;
    int maxRadius;
    double minRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector_demo_results);

        mContext = this;

        mYelpKey = new YelpApiKey(
                getApplicationContext().getResources().getString(R.string.yelp_consumer_key),
                getApplicationContext().getResources().getString(R.string.yelp_consumer_secret),
                getApplicationContext().getResources().getString(R.string.yelp_token),
                getApplicationContext().getResources().getString(R.string.yelp_token_secret) );

        db = new dbHelper(this, null, null, 1);

        mLinearLayoutResults = (LinearLayout) findViewById(R.id.linearLayoutRSDResults);
        loading = new TextView(this);
        loading.setTextSize(16);
        loading.setText("Getting results & rendering output ...");
        mLinearLayoutResults.addView(loading);

        Intent i = getIntent();
        location = i.getStringExtra("location");
        Log.i(TAG, "Location: " + location);
        searchTerm = i.getStringExtra("searchTerm");
        Log.i(TAG, "Search terms: " + searchTerm);
        maxRadius = i.getIntExtra("maxRadius", 805);
        Log.i(TAG, "Max Radius: " + maxRadius);
        minRating = i.getDoubleExtra("minRating", 0);
        Log.i(TAG, "Min Rating: " + minRating);

        if (!ServiceUtil.isNetworkAvailable(this)) {
            ToastUtil.showShortToast(mContext, "Network is unavailable");
            finish();
        }

        new GetListTask().execute();
    }

    public class GetListTask extends AsyncTask<Void, Void, RestaurantList> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
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
            try {
                return new RestaurantApiClient.Builder(mYelpKey)
                        .location(location)
                        .sort(RestaurantApiClient.SortBy.BEST_MATCH)
                        .term(searchTerm)
                        .radiusFilter(maxRadius)
                        .build().getRestaurantList();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         * <p/>
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param mResultList The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */
        @Override
        protected void onPostExecute(RestaurantList mResultList) {
            Random rand = new Random();
            double randomNum;

            mLinearLayoutResults.removeView(loading);

            appendOutputHeading("Search parameters");
            appendOutputText("Location: " + location);
            appendOutputText("Search term(s): " + searchTerm);
            appendOutputText("Max radius: " + maxRadius + " m ("
                    + RestaurantApiClient.convertDistanceUnits((double)maxRadius,
                    DistanceUnit.METERS, DistanceUnit.MILES) + " miles)");
            appendOutputText("Min rating: " + minRating + " stars");


            if (mResultList == null) {
                appendOutputText("No results");
                return;
            }

            appendOutputHeading("Step 0: Yelp query result");
            displayList(mResultList);

            appendOutputHeading("Step 1: Remove from results any red-listed restaurants or " +
                    "restaurants below the user-specified minimum rating");
            for (int i = 0; i < mResultList.getSize(); ) {
                if (db.isRestaurantInList(mResultList.getRestaurant(i), Constants.RED_LIST)
                        || mResultList.getRestaurant(i).getRating() < minRating) {
                    Restaurant removed = mResultList.removeRestaurant(i);
                    if (removed == null) // check if restaurant was removed successfully
                        i++;
                    // otherwise don't iterate as next restaurant will be at this index
                } else
                    i++;
            }
            displayList(mResultList);

            appendOutputHeading("Step 2: Assign random number to all restaurants");
            appendOutputText("Yellow-listed restaurants are demoted linearly with timestamp");
            appendOutputText("Green-listed restaurants are promoted by a constant factor");
            for (int i = 0; i < mResultList.getSize(); i++) {
                double oldnum;
                oldnum = randomNum = rand.nextInt((100 - 1) + 1);
                Restaurant r = mResultList.getRestaurant(i);

                // apply weight if current is green-listed restaurant
                if (db.isRestaurantInList(mResultList.getRestaurant(i), Constants.GREEN_LIST)) {
                    randomNum = randomNum * 1.15;
                }
                // apply weight if current is yellow-listed restaurant
                else if (db.isRestaurantInList(r, Constants.YELLOW_LIST)) {
                    Timestamp timestamp = Timestamp.valueOf(db.getRestaurantTimeStampFromList(r,
                            Constants.YELLOW_LIST));
                    long timeElapsed = System.currentTimeMillis() - timestamp.getTime();
                    // weight is linear with timestamp; if the restaurant was just added to the
                    // yellow list it receives a weight multiplier of 0.6; if it was added a week
                    // ago (the cutoff time at which it's removed from the list) it is
                    // essentially unweighted
                    randomNum = (randomNum * 0.6) + (timeElapsed * 6.6137566E-9);
                }

                mResultList.getRestaurant(i).setWeight((int) randomNum);

                // show change in weight
                StringBuilder result = new StringBuilder("[" + i + "] " + r.getBusinessName() );

                if (db.isRestaurantInList(r, Constants.RED_LIST)) {
                    appendOutputText(result.toString(), Color.parseColor("#73001B"), 14, 0);
                } else if (db.isRestaurantInList(r, Constants.GREEN_LIST)) {
                    result.append(" (weight: " + (int)oldnum + "->" + (int)randomNum  + ")");
                    appendOutputText(result.toString(), Color.parseColor("#197300"), 14, 0);
                } else if (db.isRestaurantInList(r, Constants.YELLOW_LIST)) {
                    result.append(" (weight: " + (int)oldnum + "->" + (int)randomNum + ", " +
                            "timestamp: "
                            + db.getRestaurantTimeStampFromList(r, Constants.YELLOW_LIST)
                            + ")");
                    appendOutputText(result.toString(), Color.parseColor("#736B00"), 14, 0);
                } else {
                    result.append(" (weight: " + (int)randomNum + ")");
                    appendOutputText(result.toString(), Color.DKGRAY, 14, 0);
                }
            }

            appendOutputHeading("Step 3: Restaurants are removed from the list and displayed to " +
                    "the user by weight in non-increasing order");

            for (int i = 0; mResultList != null & mResultList.getSize() > 0; i++) {
                int largestIndex = 0;

                for (int j = 0, largest = 0; j < mResultList.getSize(); j++) {
                    int randomValue = mResultList.getRestaurant(j).getWeight();
                    if (randomValue > largest) {
                        largest = randomValue;
                        largestIndex = j;
                    }
                }

                appendOutputWithRestaurant(mResultList.removeRestaurant(largestIndex), i);
            }

        }
    }

    protected void displayList(RestaurantList rList) {
        if (rList == null) {
            appendOutputText("No results");
            return;
        }

        for (int i = 0; i < rList.getSize(); i++) {
            appendOutputWithRestaurant(rList.getRestaurant(i), i);
        }
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
        mLinearLayoutResults.addView(txtOut);
        return txtOut;
    }

    /**
     * Overloaded version of appendOutputText(String s, int color) that appends text with default
     * body style (dark grey, 14pt, no top margin)
     * @param s  text to add
     * @return   the created TextView
     */
    public TextView appendOutputText(String s) {
        return appendOutputText(s, Color.DKGRAY, 14, 0);
    }

    public TextView appendOutputHeading(String s) {
        return appendOutputText(s, Color.BLACK, 16, 20);
    }

    public TextView appendOutputWithRestaurant(Restaurant r, int index) {
        StringBuilder result = new StringBuilder("[" + index + "] " + r.getBusinessName()
                + " (weight: " + (r.getWeight() > 0 ? r.getWeight() : "n/a"));

        if (db.isRestaurantInList(r, Constants.RED_LIST)) {
            result.append(")");
            return appendOutputText(result.toString(), Color.parseColor("#73001B"), 14, 0);
        } else if (db.isRestaurantInList(r, Constants.GREEN_LIST)) {
            result.append(")");
            return appendOutputText(result.toString(), Color.parseColor("#197300"), 14, 0);
        } else if (db.isRestaurantInList(r, Constants.YELLOW_LIST)) {
            result.append(", timestamp: "
                    + db.getRestaurantTimeStampFromList(r, Constants.YELLOW_LIST)
                    + ")");
            return appendOutputText(result.toString(), Color.parseColor("#736B00"), 14, 0);
        } else {
            result.append(")");
            return appendOutputText(result.toString(), Color.DKGRAY, 14, 0);
        }
    }
}
