package com.csc413.team5.fud5.tests;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.csc413.team5.fud5.R;
import com.csc413.team5.restaurantapiwrapper.LocuApiKey;
import com.csc413.team5.restaurantapiwrapper.MenuAndHoursExtension;
import com.csc413.team5.restaurantapiwrapper.MapBounds;
import com.csc413.team5.restaurantapiwrapper.Restaurant;
import com.csc413.team5.restaurantapiwrapper.RestaurantApiClient;
import com.csc413.team5.restaurantapiwrapper.RestaurantList;
import com.csc413.team5.restaurantapiwrapper.YelpApiKey;

import org.json.JSONException;

import java.io.IOException;


public class RestaurantTestActivity extends ActionBarActivity {
    private TextView output;
    private Restaurant someRestaurant;
    private RestaurantList someRestaurantList;
    private String locuId;
    private MapBounds testMapBounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_test);
        setTitle("Yelp API Test");

        output = (TextView) findViewById(R.id.api_out);
        output.setText("Initiating call ... \n\n");

        YelpAsyncTask task = new YelpAsyncTask();
        task.execute();
    }

    private class YelpAsyncTask extends AsyncTask<String, String, String> {
        private String response;

        @Override
        protected String doInBackground(String... params)  {
            // Construct a YelpApiKey from Resource strings
            String consumerKey = getApplicationContext().getResources()
                    .getString(R.string.yelp_consumer_key);
            String consumerSecret = getApplicationContext().getResources()
                    .getString(R.string.yelp_consumer_secret);
            String tokenKey = getApplicationContext().getResources()
                    .getString(R.string.yelp_token);
            String tokenSecret = getApplicationContext().getResources()
                    .getString(R.string.yelp_token_secret);
            YelpApiKey yelpKey = new YelpApiKey(consumerKey, consumerSecret, tokenKey, tokenSecret);

            // search for an individual business by Yelp ID
            // change id field to test different businesses

            RestaurantApiClient rClient = new RestaurantApiClient.Builder(yelpKey)
                    .id("new-tsing-tao-restaurant-san-francisco").build();
            try {
                someRestaurant = rClient.getRestaurantByID();
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
            MenuAndHoursExtension locu = new MenuAndHoursExtension(locuKey);

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
            output.append("-----------------------------------------------------\n" +
                            "YELP SEARCH FOR BUSINESSES AROUND A PARTICULAR LOCATION\n" +
                            "-----------------------------------------------------"
            );

            output.append("\n\nResult of search request:\n");
            if (someRestaurantList == null)
                output.append("null RestaurantList()\n");
            else {
                output.append("RestaurantList size: " + someRestaurantList.size());
                output.append("\n\nThe contents of the" +
                        "RestaurantList are:");
                output.append(someRestaurantList.toString());
            }

            output.append("\n\n\n\n-----------------------------------------------------\n" +
                            "YELP BUSINESS SEARCH, ATTEMPT TO MATCH VENUE WITH " +
                            "LOCU AND PULL ADDITIONAL DATA (MENU AND OPEN HOURS)\n" +
                            "-----------------------------------------------------\n\n"
            );
            output.append("Queried Yelp for: " + someRestaurant.getBusinessName() + '\n');

            output.append("Attempted Locu match: " + ":\n");
            if (locuId.compareTo("") == 0)
                output.append("No match found.");
            else
                output.append("Matched Locu ID: " + locuId);


            output.append("\n\nResult of Yelp business search request with Locu information if a " +
                    "match was found :\n");
            if (someRestaurant == null)
                output.append("null Restaurant()\n");
            else
                output.append(someRestaurant.toString());
        }
    }
}
