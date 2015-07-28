package com.csc413.team5.fud5;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.csc413.team5.appdb.dbHelper;
import com.csc413.team5.fud5.dialogs.DisplayRestaurantMenusFragment;
import com.csc413.team5.fud5.dialogs.MenuNotFoundFragment;
import com.csc413.team5.fud5.dialogs.MoreInfoDialogFragment;
import com.csc413.team5.fud5.dialogs.NoResultsDialogFragment;
import com.csc413.team5.fud5.utils.Constants;
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
    private Context mContext;

    private GoogleMap mMap;

    YelpApiKey mYelpKey;
    LocuApiKey mLocuKey;
    RestaurantList mResultList;
    Restaurant mFirstResult;
    boolean mAlreadyQueriedLocuThisResult;

    // user input passed from main activity
    String location;
    String searchTerm;
    int maxRadius;
    double minRating;

    TextView mTitle;

    DialogFragment mMoreInfoDialog;

    // Restaurant menu
    DisplayMenuTask displayMenuTask;
    DialogFragment mDisplayRestaurantMenus;
    DialogFragment mMenuNotFoundDialog;
    PopupWindow mPopupLoadingMenu;

    // Database
    dbHelper db;

    /**
     * Save all appropriate fragment state.
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // save the state of the info dialog if it is visible
        if (mMoreInfoDialog != null && mMoreInfoDialog.isVisible())
            getFragmentManager().putFragment(outState, "info", mMoreInfoDialog);
    }

    /**
     * This method is called after {@link #onStart} when the activity is
     * being re-initialized from a previously saved state, given here in
     * <var>savedInstanceState</var>.  Most implementations will simply use {@link #onCreate}
     * to restore their state, but it is sometimes convenient to do it here
     * after all of the initialization has been done or to allow subclasses to
     * decide whether to use your default implementation.  The default
     * implementation of this method performs a restore of any view state that
     * had previously been frozen by {@link #onSaveInstanceState}.
     * <p/>
     * <p>This method is called between {@link #onStart} and
     * {@link #onPostCreate}.
     *
     * @param savedInstanceState the data most recently supplied in {@link #onSaveInstanceState}.
     * @see #onCreate
     * @see #onPostCreate
     * @see #onResume
     * @see #onSaveInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // restore the state of the info dialog if it was open while the activity was paused
        // if it wasn't open this operation shouldn't affect the activity (mMoreInfoDialog == null)
        mMoreInfoDialog = (DialogFragment) getFragmentManager().getFragment(savedInstanceState,
                "info");
    }

    // user presses the "Let's go!" button
    public void btnGreen(View v) {
        // TODO define a behavior
        // if the restaurant is not already in green list, add it
        if (!db.isRestaurantInList(mFirstResult, Constants.GREEN_LIST)) {
            db.insertRestaurantToList(mFirstResult, Constants.GREEN_LIST);
            Log.i(TAG, "Added " + mFirstResult.getBusinessName() + " to green list");
        } else { // otherwise, don't add it
            Log.i(TAG, mFirstResult.getBusinessName() + " was already in green list");
        }

        // TODO: TEMP - specify green button behavior
        ToastUtil.showShortToast(this, "TODO: Green button behavior");
        // END TEMP
    }

    // user presses the "Maybe later..." button
    public void btnYellow(View v) {
        // if restaurant is not already in yellow list, add it
        if (!db.isRestaurantInList(mFirstResult, Constants.YELLOW_LIST)) {
            db.insertRestaurantToList(mFirstResult, Constants.YELLOW_LIST);
            Log.i(TAG, "Added " + mFirstResult.getBusinessName() + " to yellow list");
        } else { // otherwise, update the timestamp by deleting and re-adding it
            db.deleteRestaurantFromList(mFirstResult, Constants.YELLOW_LIST);
            db.insertRestaurantToList(mFirstResult, Constants.YELLOW_LIST);
            Log.i(TAG, mFirstResult.getBusinessName() + " was already in yellow list");
        }

        displayNextResult(v);
    }

    // user presses the "Always ignore" button
    public void btnRed(View v) {
        // if restaurant isn't already in red list, add it
        if (!db.isRestaurantInList(mFirstResult, Constants.RED_LIST)) {
            ToastUtil.showShortToast(this, mFirstResult.getBusinessName()
                    + getString(R.string.activity_result_page_toast_was_added_to_red_list));
            db.insertRestaurantToList(mFirstResult, Constants.RED_LIST);
            Log.i(TAG, "Added " + mFirstResult.getBusinessName() + " to red list");
        } else { // otherwise, don't do anything
            Log.i(TAG, mFirstResult.getBusinessName() + " was already in red list");
        }

        displayNextResult(v);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_page);
        String title = getString(R.string.title_activity_result_page);

        // save this context to use anywhere in the activity
        mContext = this;

        // back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // action bar colors
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#43428A")));
        getSupportActionBar().setTitle(Html.fromHtml("<font color = '#ECCD7F'>" + title + "</font>"));

        // anchor for PopupWindows
        mTitle = (TextView) findViewById(R.id.restaurantName);
        // define "Loading menus" popups
        LayoutInflater layoutInflater  = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupLoadingMenuView = layoutInflater.inflate(R.layout.popup_loading_rmenu, null);
        mPopupLoadingMenu = new PopupWindow(popupLoadingMenuView,
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

        // Initialize the database
        db = new dbHelper(this, null, null, 1);

        // Construct API keys from Resource strings
        mYelpKey = new YelpApiKey(
                getApplicationContext().getResources().getString(R.string.yelp_consumer_key),
                getApplicationContext().getResources().getString(R.string.yelp_consumer_secret),
                getApplicationContext().getResources().getString(R.string.yelp_token),
                getApplicationContext().getResources().getString(R.string.yelp_token_secret) );
        mLocuKey = new LocuApiKey(getApplicationContext().getResources()
                .getString(R.string.locu_key));

        mResultList = null;
        mFirstResult = null;
        mAlreadyQueriedLocuThisResult = false;

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
        if (mResultList == null)
            return;
        if (mResultList.size() <= 0) {
            ToastUtil.showShortToast(this, "No more matches found.");
        } else {
            //Clear the image before the next image is shown
            //Sometimes a result doesn't have an image, so it was showing the last one
            ((ImageView) findViewById(R.id.imgRestaurant)).setImageResource(0);
        }

        try {
            mFirstResult = mResultList.remove(0);
            mAlreadyQueriedLocuThisResult = false;

            Log.i(TAG, "Rating: " + mFirstResult.getRating());
            drawStars(mFirstResult.getRating());
            mMap.clear();
            setUpMap(mFirstResult);
            TextView title = (TextView)findViewById(R.id.restaurantName);

            // Display business name; reduce font size for long restaurant names
            int businessNameLength = mFirstResult.getBusinessName().length();
            if (businessNameLength < 20) {
                title.setTextSize(28);
            } else if (businessNameLength >= 20 && businessNameLength < 29) {
                title.setTextSize(26);
            } else {
                title.setTextSize(20);
            }
            title.setText(mFirstResult.getBusinessName());

            //Load the restaurant image
            if (mFirstResult.getImageUrl() == null)
                ((ImageView)findViewById(R.id.imgRestaurant)).setImageResource(R.drawable.no_image);
            else {
                String tempURL = mFirstResult.getImageUrl().toString();
                tempURL = tempURL.replace("ms.jpg", "o.jpg"); //this gets original image size
                URL imageURL = new URL(tempURL);

                LoadImageTask task = new LoadImageTask() {
                    protected void onPostExecute(Bitmap result) {
                        ImageView restaurantImage = (ImageView) findViewById(R.id.imgRestaurant);
                        restaurantImage.setImageBitmap(result);
                    }
                };
                task.execute(imageURL);
            }
        } catch(Exception e){}
    }
    public class LoadImageTask extends AsyncTask<URL, Void, Bitmap>{
        @Override
        protected Bitmap doInBackground (URL... imageURL)
        {
            try {
                InputStream stream = imageURL[0].openConnection().getInputStream();
                return BitmapFactory.decodeStream(stream);
            }catch(Exception e) {}
            return null;
        }
    }

    private void drawStars(double rating){
        ImageView imgRating = (ImageView) findViewById(R.id.imgRating);
        if (rating == 0.0)
            imgRating.setImageResource(R.drawable.stars0_0);
        else if (rating == 0.5)
            imgRating.setImageResource(R.drawable.stars0_5);
        else if (rating == 1.0)
            imgRating.setImageResource(R.drawable.stars1_0);
        else if (rating == 1.5)
            imgRating.setImageResource(R.drawable.stars1_5);
        else if (rating == 2.0)
            imgRating.setImageResource(R.drawable.stars2_0);
        else if (rating == 2.5)
            imgRating.setImageResource(R.drawable.stars2_5);
        else if (rating == 3.0)
            imgRating.setImageResource(R.drawable.stars3_0);
        else if (rating == 3.5)
            imgRating.setImageResource(R.drawable.stars3_5);
        else if (rating == 4.0)
            imgRating.setImageResource(R.drawable.stars4_0);
        else if (rating == 4.5)
            imgRating.setImageResource(R.drawable.stars4_5);
        else if (rating == 5.0)
            imgRating.setImageResource(R.drawable.stars5_0);
       else
            imgRating.setImageResource(R.drawable.error_stars);
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
        if (mPopupLoadingMenu.isShowing())
            mPopupLoadingMenu.dismiss();
    }

    //TODO: implement the selector
    private class GetResultTask extends AsyncTask<String, Void, RestaurantList> {

        @Override
        protected RestaurantList doInBackground(String... params)  {
            try {
                return new RestaurantApiClient.Builder(mYelpKey)
                        .location(location)
                        //.categoryFilter("foodtrucks,restaurants") is included by default
                        .sort(RestaurantApiClient.SortBy.BEST_MATCH)
                        .term(searchTerm)
                        //.limit(20) is the default
                        .radiusFilter(maxRadius)
                        .build().getRestaurantList();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(RestaurantList result) {
            mResultList = result;

            if (mResultList == null || mResultList.getSize() < 1) { // catch no results
                DialogFragment noResultsDialog = NoResultsDialogFragment.getInstance();
                noResultsDialog.show(getFragmentManager(), "noResults");
            } else {
                // TODO: TEMP CODE which removes restaurants < minRating
                for (int i = 0; i < mResultList.getSize(); ) {
                    if (mResultList.getRestaurant(i).getRating() < minRating) {
                        Restaurant removed = mResultList.removeRestaurant(i);
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latitudeLongitude, 14));//sets the view

        mMap.addMarker(new MarkerOptions().visible(true)
                        .position(latitudeLongitude) //these are called on the MarkerOptions object
                        .title(r.getBusinessName())
                        .snippet(r.getAddressDisplay().replaceAll("\n", ", "))
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

        //Back button
        if(id == android.R.id.home) {
            finish();
            return true;
        }

        //Info button
        // get more information about the restaurant currently in focus by displaying a dialog
        if (id == R.id.action_info) {
            new GetMoreInfoTask().execute(mFirstResult);
            mPopupLoadingMenu.showAtLocation(mTitle, Gravity.CENTER, 0, 0);

        }

        //Menu button
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_menu && mFirstResult != null) {
            // don't proceed if a DisplayMenuTask is already in progress
            if (displayMenuTask != null &&
                    ( displayMenuTask.getStatus() == AsyncTask.Status.RUNNING
                    || displayMenuTask.getStatus() == AsyncTask.Status.PENDING) )
                return true;

            // don't proceed if DisplayMenuTask is complete but one of the resulting dialogs
            // is rendering
            if (mDisplayRestaurantMenus != null && mDisplayRestaurantMenus.isVisible())
                return true;
            if (mMenuNotFoundDialog != null && mMenuNotFoundDialog.isVisible())
                return true;

            if (ServiceUtil.isNetworkAvailable(this)) {
                displayMenuTask = new DisplayMenuTask();
                displayMenuTask.execute(mFirstResult);
                mPopupLoadingMenu.showAtLocation(mTitle, Gravity.CENTER, 0, 0);
            } else {
                ToastUtil.showShortToast(this, getString(R.string.toast_network_unavailable));
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

            // only perform the call if the API call if the restaurant does not already have
            // menu information (e.g. the menus button has already been pressed this result)
            if (!mAlreadyQueriedLocuThisResult) {
                Log.i(TAG, "Attempting to find a menu for " + restaurant.getBusinessName());
                new LocuExtension(mLocuKey).updateIfHasMenu(restaurant);
                mAlreadyQueriedLocuThisResult = true;
            }

            return restaurant;
        }

        @Override
        protected void onPostExecute(Restaurant restaurant) {
            if (mPopupLoadingMenu != null && mPopupLoadingMenu.isShowing())
                mPopupLoadingMenu.dismiss();

            if (restaurant.hasLocuMenus()) {
                Log.i(TAG, "Found menu for " + restaurant.getBusinessName());
                mDisplayRestaurantMenus = DisplayRestaurantMenusFragment
                        .getInstance(restaurant);
                mDisplayRestaurantMenus.show(getFragmentManager(), "menus");
            } else {
                Log.i(TAG, "Could not find menu for " + restaurant.getBusinessName());
                mMenuNotFoundDialog = new MenuNotFoundFragment();
                mMenuNotFoundDialog.show(getFragmentManager(), "menuNotFound");
            }
        }
    }

    private class GetMoreInfoTask extends AsyncTask<Restaurant, Void, Restaurant> {
        @Override
        protected Restaurant doInBackground(Restaurant... params) {
            Restaurant r = params[0];

            if (!mAlreadyQueriedLocuThisResult) {
                Log.i(TAG, "Attempting to find open hours for " + r.getBusinessName());
                new LocuExtension(mLocuKey).updateIfHasMenu(r);
                mAlreadyQueriedLocuThisResult = true;
            }

            return r;
        }

        @Override
        protected void onPostExecute(Restaurant r) {
            if (mPopupLoadingMenu != null && mPopupLoadingMenu.isShowing())
                mPopupLoadingMenu.dismiss();

            if (mMoreInfoDialog != null && mMoreInfoDialog.isVisible())
                mMoreInfoDialog.dismiss();
            mMoreInfoDialog = MoreInfoDialogFragment.getInstance(mFirstResult);
            mMoreInfoDialog.show(getFragmentManager(), "moreInfo");
        }
    }

}
