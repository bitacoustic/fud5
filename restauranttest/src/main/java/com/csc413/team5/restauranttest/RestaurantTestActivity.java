package com.csc413.team5.restauranttest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.csc413.team5.restaurantapiwrapper.LocuApiKey;
import com.csc413.team5.restaurantapiwrapper.LocuExtension;
import com.csc413.team5.restaurantapiwrapper.MapBounds;
import com.csc413.team5.restaurantapiwrapper.Restaurant;
import com.csc413.team5.restaurantapiwrapper.RestaurantApiClient;
import com.csc413.team5.restaurantapiwrapper.RestaurantList;
import com.csc413.team5.restaurantapiwrapper.YelpApiKey;

import org.json.JSONException;

import java.io.IOException;


public class RestaurantTestActivity extends ActionBarActivity {
    private TextView callOutput;
    private Restaurant someRestaurant;
    private RestaurantList someRestaurantList;
    private String locuId;
    private MapBounds testMapBounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Yelp API Test");

        callOutput = (TextView) findViewById(R.id.api_out);
        callOutput.setText("Initiating call ... \n\n");

        YelpAsyncTask task = new YelpAsyncTask();
        task.execute();
    }

    private class YelpAsyncTask extends AsyncTask<String, String, String> {
        private String response;

        @Override
        protected String doInBackground(String... params)  {
            // Construct a YelpApiKey from Resource strings
            String consumerKey = getApplicationContext().getResources()
                    .getString(R.string.yelp_consumer_key);;
            String consumerSecret = getApplicationContext().getResources()
                    .getString(R.string.yelp_consumer_secret);;
            String tokenKey = getApplicationContext().getResources()
                    .getString(R.string.yelp_token);;
            String tokenSecret = getApplicationContext().getResources()
                    .getString(R.string.yelp_token_secret);;
            YelpApiKey yelpKey = new YelpApiKey(consumerKey, consumerSecret, tokenKey, tokenSecret);

            // search for an individual business by Yelp ID
            // change id field to test different businesses

            RestaurantApiClient rClient = new RestaurantApiClient.Builder(yelpKey)
                    .id("new-tsing-tao-restaurant-san-francisco").build();
            try {
                someRestaurant = rClient.getRestaurantByYelpID();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            /*
            FactualApiKey factualKey = new FactualApiKey("", "");
            FactualExtension factual = new FactualExtension(factualKey);
            String fResult = factual.match(someRestaurant);

            callOutput.append("\n\n" + fResult);
            */

            // WARNING: Locu functionality isn't yet fully implemented
            LocuApiKey locuKey = new LocuApiKey(getApplicationContext().getResources()
                    .getString(R.string.locu_key));
            LocuExtension locu = new LocuExtension(locuKey);

            // attempt a match for the restaurant in Locu and update its information if found;
            // the locu ID will be returned if a match was found, otherwise locuId's contents
            // will be "" (match not found)
            locuId = locu.update(someRestaurant);


            //

            try {
                rClient = new RestaurantApiClient.Builder(yelpKey).location("Hayward, CA")
                        //.categoryFilter("foodtrucks,restaurants")
                        .term("seafood")
                        //.radiusFilter(15000)
                        .build();
                someRestaurantList = rClient.getRestaurantList();
                testMapBounds = someRestaurantList.getMapBounds();
            //} catch (ParameterRangeException e) {
            //    e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "done";

        }

        protected void onPostExecute(String result) {
            callOutput.append("Attempt at Locu match for " + someRestaurant.getBusinessName()
                    + ":\n");
            if (locuId.compareTo("") == 0)
                callOutput.append("No match found.");
            else
                callOutput.append("Matched Locu ID: " + locuId);

            callOutput.append("\n\nResult of Yelp ID request with Locu information if a" +
                    "match was found :\n");
            if (someRestaurant == null)
                callOutput.append("null Restaurant()\n");
            else
                callOutput.append(someRestaurant.toString());


            callOutput.append("\n\nResult of search request:\n");
            if (someRestaurantList == null)
                callOutput.append("null RestaurantList()\n");
            else {
                callOutput.append("RestaurantList size: " + someRestaurantList.size());
                callOutput.append("\nMap bounds after query: " + testMapBounds);
                int rListMid = someRestaurantList.size() / 2;
                someRestaurantList.remove(rListMid);
                callOutput.append("\nRemoved 1 record. RestaurantList size: "
                        + someRestaurantList.size());
                someRestaurantList.add(someRestaurant);
                callOutput.append("\nAdded 1 record. RestaurantList size: "
                        + someRestaurantList.size());
                callOutput.append("\n\nThe contents of the" +
                        "RestaurantList is now:");
                callOutput.append(someRestaurantList.toString());
            }
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
}
