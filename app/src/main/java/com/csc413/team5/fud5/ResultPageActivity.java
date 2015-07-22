package com.csc413.team5.fud5;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.csc413.team5.fud5.tests.LocuMenuTestActivity;
import com.csc413.team5.restaurantapiwrapper.Restaurant;
import com.csc413.team5.restaurantapiwrapper.RestaurantApiClient;
import com.csc413.team5.restaurantapiwrapper.RestaurantList;
import com.csc413.team5.restaurantapiwrapper.YelpApiKey;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.net.URL;

//imports for images
//TODO:remove these imports when Selector is implemented

public class ResultPageActivity extends AppCompatActivity {
    public static final String TAG = "ResultPageActivity";

    private GoogleMap mMap;
    RestaurantList resultList;

    // user input passed from main activity
    String location;
    String searchTerm;
    int maxRadius;
    double minRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_result_page);

        Intent i = getIntent();
        location = i.getStringExtra("location");
        searchTerm = i.getStringExtra("searchTerm");
        maxRadius = i.getIntExtra("maxRadius", 805); // default to 805m (0.5 miles) if value
                                                        // not read
        minRating = i.getDoubleExtra("minRating",0);
        Log.i(TAG, "Retrieved location: " + location);
        Log.i(TAG, "Retrieved searchTerm: " + searchTerm);
        Log.i(TAG, "Retrieved maxRadius: " + maxRadius);
        Log.i(TAG, "Retrieved minRating: " + minRating);

        GetResultTask task = new GetResultTask();
        task.execute();
        setUpMapIfNeeded();
    }
    public void displayNextResult(View v){
        //display restaurant info goes here.
        //restaurant image loading needs to go in yet another asynctask
        LoadImageTask task = new LoadImageTask();
        if(resultList==null)return;
        try{
            Restaurant firstResult;
            firstResult=resultList.remove(0);
            setUpMap(firstResult);
            TextView title = (TextView)findViewById(R.id.restaurantName);
            title.setText(firstResult.getBusinessName());
            task.execute(firstResult);

        } catch(IndexOutOfBoundsException e){} //
    }

    //TODO:remove when Selector implemented
    private class GetResultTask extends AsyncTask<String, Void, RestaurantList> {

        protected void onPostExecute(RestaurantList result) {
            resultList = result;
            displayNextResult(findViewById(R.id.imgRestaurant));
        }
        @Override
        protected RestaurantList doInBackground(String... params)  {
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

            try {
                RestaurantApiClient rClient = new RestaurantApiClient.Builder(yelpKey)
                        .location(location)
                        //.categoryFilter("foodtrucks,restaurants") is included by default
                        .sort(2)                  // 0=best matched, 1=distance, 2=highest rated
                        .term(searchTerm)
                        .radiusFilter(maxRadius)
                        .build();
                return rClient.getRestaurantList();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
    }

    //END remove

    private class LoadImageTask extends AsyncTask<Restaurant, Void, Bitmap> {
        protected void onPostExecute(Bitmap result) {
            ImageView restaurantImage = (ImageView) findViewById(R.id.imgRestaurant);
            restaurantImage.setImageBitmap(result);
        }
        @Override
        protected Bitmap doInBackground(Restaurant... params)  {

            try {
                String imageUrl = params[0].getImageUrl().toString();
                imageUrl =  imageUrl.replace("ms.jpg","o.jpg"); //this gets original image size
                URL url = new URL(imageUrl);
                InputStream is = url.openConnection().getInputStream();
                return BitmapFactory.decodeStream(is);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }

/*
        setContentView(R.layout.activity_result_page);
        Typeface buttonFont = Typeface.createFromAsset(getAssets(), "Chunkfive.otf");
        Button greenButton = (Button) findViewById(R.id.greenButton);
        greenButton.setTypeface(buttonFont);
        //TextView myTextView = (TextView)findViewById(R.id.greenButton);
       // myTextView.setTypeface(buttonFont);
*/

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
              //  setUpMap();
            }
        }
    }

    private void setUpMap(Restaurant r) {
        Location resultLoc = r.getAddressMapable();

        LatLng latitudeLongitude = new LatLng(resultLoc.getLatitude(), resultLoc.getLongitude()); //test latitude longitude

        mMap.setMyLocationEnabled(true);
        //mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latitudeLongitude, 13));//sets the view

        mMap.addMarker(new MarkerOptions().visible(true)
                .position(latitudeLongitude)
                .title(r.getBusinessName())).showInfoWindow();


    }

    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_menu) {
            Intent intent = new Intent(this, LocuMenuTestActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onResume() {
        super.onResume();
    }
}
