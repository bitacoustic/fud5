package com.csc413.team5.fud5;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.csc413.team5.fud5.utils.ServiceUtil;
import com.csc413.team5.fud5.utils.ToastUtil;
import com.csc413.team5.restaurantapiwrapper.LocuApiKey;
import com.csc413.team5.restaurantapiwrapper.LocuExtension;
import com.csc413.team5.restaurantapiwrapper.Restaurant;
import com.csc413.team5.restaurantapiwrapper.RestaurantApiClient;
import com.csc413.team5.restaurantapiwrapper.RestaurantList;
import com.csc413.team5.restaurantapiwrapper.YelpApiKey;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.net.URL;

//imports for images
//TODO:remove these imports when Selector is implemented

public class ResultPageActivity extends AppCompatActivity
        implements MenuNotFoundFragment.MenuNotFoundDialogListener {
    public static final String TAG = "ResultPageActivity";

    private GoogleMap mMap;

    YelpApiKey mYelpKey;
    RestaurantList resultList;
    Restaurant firstResult;

    // user input passed from main activity
    String location;
    String searchTerm;
    int maxRadius;
    double minRating;

    TextView mTitle;
    PopupWindow popupLoadingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_result_page);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#43428A")));

        // anchor for PopupWindows
        mTitle = (TextView) findViewById(R.id.restaurantName);
        // define "Loading menus" popups
        LayoutInflater layoutInflater  = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupLoadingMenuView = layoutInflater.inflate(R.layout.popup_loading_rmenu, null);
        popupLoadingMenu = new PopupWindow(popupLoadingMenuView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        // load the search parameters entered in main activity
        Intent i = getIntent();
        location = i.getStringExtra("location");
        searchTerm = i.getStringExtra("searchTerm");
        maxRadius = i.getIntExtra("maxRadius", 805); // default to 805m (0.5 miles) if value
                                                        // not read
        minRating = i.getDoubleExtra("minRating", 0);
        Log.i(TAG, "Retrieved location: " + location);
        Log.i(TAG, "Retrieved searchTerm: " + searchTerm);
        Log.i(TAG, "Retrieved maxRadius: " + maxRadius);
        Log.i(TAG, "Retrieved minRating: " + minRating);

        // Construct a YelpApiKey from Resource strings
        mYelpKey = new YelpApiKey(
                getApplicationContext().getResources().getString(R.string.yelp_consumer_key),
                getApplicationContext().getResources().getString(R.string.yelp_consumer_secret),
                getApplicationContext().getResources().getString(R.string.yelp_token),
                getApplicationContext().getResources().getString(R.string.yelp_token_secret) );
    }

    /**
     * Dispatch onStart() to all fragments.  Ensure any created loaders are
     * now started.
     */
    @Override
    protected void onStart() {
        super.onStart();
        // start background activity to get the results
        if (ServiceUtil.isNetworkAvailable(this)) {
            new GetResultTask().execute();
            setUpMapIfNeeded();
        } else {
            // Close the results page immediately if there is no network connectivity; to the user
            // it will appear as if the results page never opened
            ToastUtil.showShortToast(this, getString(R.string.toast_network_unavailable));
            finish();
        }
    }

    public void displayNextResult(View v){
        //display restaurant info goes here.
        //restaurant image loading needs to go in yet another asynctask
        if(resultList==null)return;
        try{
            firstResult=resultList.remove(0);
            mMap.clear();
            setUpMap(firstResult);
            TextView title = (TextView)findViewById(R.id.restaurantName);
            title.setText(firstResult.getBusinessName());
            //Load the restaurant image
            LoadImageTask task = new LoadImageTask();
            String tempURL = firstResult.getImageUrl().toString();
            tempURL =  tempURL.replace("ms.jpg","o.jpg"); //this gets original image size
            URL imageURL = new URL(tempURL);
            task.execute(imageURL);
            //Load the rating image
            LoadImageRatingTask ratingTask = new LoadImageRatingTask();
            imageURL = new URL(firstResult.getRatingImgUrl().toString());
            ratingTask.execute(imageURL);

        } catch(Exception e){} //
    }

    @Override
    public void onMenuNotFoundPositiveClick(DialogFragment dialog) {

    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (popupLoadingMenu.isShowing())
            popupLoadingMenu.dismiss();
    }

    //TODO: implement the selector
    private class GetResultTask extends AsyncTask<String, Void, RestaurantList> {

        @Override
        protected RestaurantList doInBackground(String... params)  {
            try {
                return new RestaurantApiClient.Builder(mYelpKey)
                        .location(location)
                        //.categoryFilter("foodtrucks,restaurants") is included by default
                        .sort(2)                  // 0=best matched, 1=distance, 2=highest rated
                        .term(searchTerm)
                        .limit(40)
                        .radiusFilter(maxRadius)
                        .build().getRestaurantList();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(RestaurantList result) {
            resultList = result;

            // TODO: TEMP CODE which removes restaurants < minRating
            for (int i = 0; i < resultList.getSize(); ) {
                if (resultList.getRestaurant(i).getRating() < minRating) {
                    Restaurant removed = resultList.removeRestaurant(i);
                    if (removed == null) // check if restaurant was removed successfully
                        i++;
                    // otherwise don't iterate as next restaurant will be at this index
                }
                else
                    i++;
            }
            // END TEMP

            displayNextResult(findViewById(R.id.imgRestaurant));
        }
    }

    private class LoadImageTask extends AsyncTask<URL, Void, Bitmap> {
        protected void onPostExecute(Bitmap result) {
            ImageView restaurantImage = (ImageView) findViewById(R.id.imgRestaurant);
            restaurantImage.setImageBitmap(result);
        }
        @Override
        protected Bitmap doInBackground(URL... params)  {

            try {
                URL url = params[0];
                InputStream is = url.openConnection().getInputStream();
                return BitmapFactory.decodeStream(is);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }

    private class LoadImageRatingTask extends LoadImageTask {
        protected void onPostExecute(Bitmap result) {
            ImageView ratingImage = (ImageView) findViewById(R.id.imgRating);
            ratingImage.setImageBitmap(result);
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
//            if (mMap != null) {
//              //  setUpMap();
//            }
        }
    }

    private void setUpMap(Restaurant r) {
        Location resultLoc = r.getAddressMapable();

        LatLng latitudeLongitude = new LatLng(resultLoc.getLatitude(),
                resultLoc.getLongitude()); //test latitude longitude

        mMap.setMyLocationEnabled(true);
        //mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latitudeLongitude, 13));//sets the view

        mMap.addMarker(new MarkerOptions().visible(true)
                .position(latitudeLongitude) //these are called on the MarkerOptions object
                .title(r.getBusinessName())
                .snippet(r.getAddress().toString())
        ).showInfoWindow(); //this is called on the marker object


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
            if (firstResult != null) {
                if (ServiceUtil.isNetworkAvailable(this)) {
                    new DisplayMenuTask().execute(firstResult);
                    popupLoadingMenu.showAtLocation(mTitle, Gravity.CENTER, 0, 0);
                } else {
                    ToastUtil.showShortToast(this, getString(R.string.toast_network_unavailable));
                }
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Query Locu for a venue which matches the current restaurant and either display its
     * menu if available, or tell the user that a menu is unavailable.
     */
    private class DisplayMenuTask extends AsyncTask<Restaurant, Void, Restaurant> {

        @Override
        protected Restaurant doInBackground(Restaurant... params) {
            Restaurant restaurant = params[0];
            LocuApiKey locuKey = new LocuApiKey(getApplicationContext().getResources()
                    .getString(R.string.locu_key));
            Log.i(TAG, "Attempting to find a menu for " + restaurant.getBusinessName());
            new LocuExtension(locuKey).updateIfHasMenu(restaurant);
            return restaurant;
        }

        @Override
        protected void onPostExecute(Restaurant restaurant) {
            if (popupLoadingMenu.isShowing())
                popupLoadingMenu.dismiss();

            if (restaurant.hasLocuMenus()) {
                Log.i(TAG, "Found menu for " + restaurant.getBusinessName());
                DialogFragment displayRestaurantMenus = DisplayRestaurantMenusFragment
                        .newInstance(restaurant);
                displayRestaurantMenus.show(getFragmentManager(), "menus");
            } else {
                Log.i(TAG, "Could not find menu for " + restaurant.getBusinessName());
                DialogFragment menuNotFoundDialog = new MenuNotFoundFragment();
                menuNotFoundDialog.show(getFragmentManager(), "menuNotFound");
            }
        }
    }

    protected void onResume() {
        super.onResume();
    }
}
