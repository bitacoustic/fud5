package com.csc413.team5.restauranttest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.csc413.team5.restaurantapiwrapper.ParameterRangeException;
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
                    .id("proper-food-san-francisco-6").build();
            try {
                someRestaurant = rClient.getRestaurantByYelpID();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //

            try {
                rClient = new RestaurantApiClient.Builder(yelpKey).location("Pacifica, CA")
                        .categoryFilter("foodtrucks,restaurants")
                        .term("burgers")
                        .radiusFilter(15000)
                        .build();
                someRestaurantList = rClient.getRestaurantList();
            } catch (ParameterRangeException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "done";

        }

        protected void onPostExecute(String result) {
            callOutput.append("\nResult of Yelp ID request:\n");
            if (someRestaurant == null)
                callOutput.append("null Restaurant()\n");
            else
                callOutput.append(someRestaurant.toString());

            callOutput.append("\n\nResult of search request:\n");
            if (someRestaurantList == null)
                callOutput.append("null RestaurantList()\n");
            else
                callOutput.append(someRestaurantList.toString());
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
