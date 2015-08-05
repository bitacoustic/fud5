package com.csc413.team5.fud5;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.csc413.team5.appdb.dbHelper;
import com.csc413.team5.fud5.dialogs.DisplayRestaurantMenusFragment;
import com.csc413.team5.fud5.dialogs.MenuNotFoundDialogFragment;
import com.csc413.team5.fud5.dialogs.MoreInfoDialogFragment;
import com.csc413.team5.fud5.dialogs.NoResultsDialogFragment;
import com.csc413.team5.fud5.utils.AppSettingsHelper;
import com.csc413.team5.fud5.utils.Constants;
import com.csc413.team5.fud5.utils.ServiceUtil;
import com.csc413.team5.fud5.utils.TextGeneratorUtil;
import com.csc413.team5.fud5.utils.ToastUtil;
import com.csc413.team5.restaurantapiwrapper.LocuApiKey;
import com.csc413.team5.restaurantapiwrapper.MenuAndHoursExtension;
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
import java.sql.Timestamp;
import java.util.Locale;
import java.util.Random;

public class ResultPageActivity extends AppCompatActivity
        implements MenuNotFoundDialogFragment.MenuNotFoundDialogListener {
    public static final String TAG = "ResultPageActivity";
    private Context mContext;

    private GoogleMap mMap;

    boolean greenPressed;

    // user input passed from main activity
    String location, searchTerm;
    int maxRadius;
    double minRating;

    // API query
    YelpApiKey mYelpKey;
    LocuApiKey mLocuKey;
    GetResultTask getResultTask;
    RestaurantList mResultList;
    Restaurant mReadResult, mCurrentResult;
    boolean mAlreadyQueriedLocuThisResult;
    Bitmap nextImage;

    TextView mTitle;
    Button btnGreen, btnYellow, btnRed;

    // Dialogs and popups
    DisplayMenuTask displayMenuTask;
    DialogFragment mDisplayRestaurantMenus, mMenuNotFoundDialog, mMoreInfoDialog;
    PopupWindow mPopupLoadingInProgress;

    // Database
    dbHelper db;



    /* *************** */
    /* ONCLICK METHODS */
    /* *************** */

    // user presses the "Let's go!" button
    public void btnGreen(View v) {
        if(!greenPressed) { //Normal green button behavior
            // Change the green and yellow buttons as user has committed to the current restaurant
            greenPressed = true;
            btnGreen.setText("Get directions from Google Maps");
            btnGreen.setBackgroundColor(Color.parseColor("#3E8FF4")); //The blue from the Google Map logo
            btnYellow.setText("Get a lift from Über");
            btnYellow.setBackgroundColor(Color.parseColor("#3F3C4A")); //The dark gray from the Uber logo
            btnRed.setVisibility(View.GONE); //Gone = other buttons can resize

            // if the restaurant is not already in green list, add it
            if (!db.isRestaurantInList(mCurrentResult, Constants.GREEN_LIST)) {
                db.insertRestaurantToList(mCurrentResult, Constants.GREEN_LIST);
                Log.i(TAG, "Added " + mCurrentResult.getBusinessName() + " to green list");

                // save a reference to this restaurant in order to later ask the user for feedback:
                //   - liked the restaurant: confirm addition to green list
                //   - didn't like the restaurant: remove from green list and add to red instaed
                AppSettingsHelper.setLastGreenRestaurant(mCurrentResult);
            } else { // otherwise, don't add it & don't ask user for feedback later
                Log.i(TAG, mCurrentResult.getBusinessName() + " was already in green list");
            }
        } else { //When button is changed into Get Directions
            Location destLoc = mCurrentResult.getAddressMapable();
            double destLat = destLoc.getLatitude();
            double destLong = destLoc.getLongitude();
            String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f", destLat, destLong);
            Intent launchMaps = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(launchMaps);
        }
    }

    // user presses the "Maybe later..." button
    public void btnYellow(View v) {
        if(!greenPressed) { //Normal Yellow behavior
            // if restaurant is not already in yellow list, add it
            if (!db.isRestaurantInList(mCurrentResult, Constants.YELLOW_LIST)) {
                db.insertRestaurantToList(mCurrentResult, Constants.YELLOW_LIST);
                Log.i(TAG, "Added " + mCurrentResult.getBusinessName() + " to yellow list");
            } else { // otherwise, update the timestamp by deleting and re-adding it
                db.deleteRestaurantFromList(mCurrentResult, Constants.YELLOW_LIST);
                db.insertRestaurantToList(mCurrentResult, Constants.YELLOW_LIST);
                Log.i(TAG, mCurrentResult.getBusinessName() + " was already in yellow list; updated " +
                        "its timestamp");
            }

            displayNextResult(v);
        }else{  //Button changed to Uber
            Location destLoc = mCurrentResult.getAddressMapable();
            double destLat = destLoc.getLatitude();
            double destLong = destLoc.getLongitude();
            PackageManager pm = this.getPackageManager();
            try {
                pm.getPackageInfo("com.ubercab", PackageManager.GET_ACTIVITIES);
                String uri = String.format(
                        "uber://?action=setPickup&pickup=my_location&dropoff[latitude]=%f&dropoff[longitude]=%f", destLat, destLong);
                Intent launchUberApp = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(launchUberApp);
            } catch (PackageManager.NameNotFoundException e) {
                String uri = String.format(
                        "https://m.uber.com/?dropoff[latitude]=%f&dropoff[longitude]=%f", destLat, destLong);
                Intent launchUberSite = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(launchUberSite);
            }
        }
    }

    // user presses the "Always ignore" button
    public void btnRed(View v) {
        // if restaurant isn't already in red list, add it
        if (!db.isRestaurantInList(mCurrentResult, Constants.RED_LIST)) {
            ToastUtil.showShortToast(this, mCurrentResult.getBusinessName()
                    + getString(R.string.activity_result_page_toast_was_added_to_red_list));
            db.insertRestaurantToList(mCurrentResult, Constants.RED_LIST);
            Log.i(TAG, "Added " + mCurrentResult.getBusinessName() + " to red list");
        } else { // otherwise, don't do anything
            Log.i(TAG, mCurrentResult.getBusinessName() + " was already in red list");
        }

        displayNextResult(v);
    }

    /* ************************** *
    /* ACTIVITY LIFECYCLE METHODS */
    /* ************************** */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        greenPressed = false; //Keep track if the green button has been pressed once
        setContentView(R.layout.activity_result_page);
//        String title = TextGeneratorUtil.randomizeFromArray(getResources().getStringArray(R.array.results_title));

        // save this context to use anywhere in the activity
        mContext = this;

        // back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // action bar colors
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#43428A")));
//        getSupportActionBar().setTitle(Html.fromHtml("<font color = '#ECCD7F'>" + title + "</font>"));

        // anchor for PopupWindows
        mTitle = (TextView) findViewById(R.id.restaurantName);
        // define "Loading menus" popups
        LayoutInflater layoutInflater  = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupLoadingMenuView = layoutInflater.inflate(R.layout.popup_loading_rmenu, null);
        mPopupLoadingInProgress = new PopupWindow(popupLoadingMenuView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        // other activity buttons can't be pressed while popup is in focus (background activity
        // in progress)
        mPopupLoadingInProgress.setOutsideTouchable(false);
        mPopupLoadingInProgress.setFocusable(true);

        btnGreen = (Button) findViewById(R.id.greenButton);
        btnYellow = (Button) findViewById(R.id.yellowButton);
        btnRed = (Button) findViewById(R.id.redButton);

        // make the Yelp logo unobtrusively small
        ImageView yelpAttributionLogo = (ImageView) findViewById(R.id.imageViewResultPageYelpLogo);
        RelativeLayout.LayoutParams yelpLogoParams = new RelativeLayout.LayoutParams(100, 50);
        yelpAttributionLogo.setLayoutParams(yelpLogoParams);

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

        // initialize activity-scope variables
        mResultList = null;
        mReadResult = null;
        mCurrentResult = null;
        mAlreadyQueriedLocuThisResult = false;

        // start background activity to get the results
        if (ServiceUtil.isNetworkAvailable(this)) {
            getResultTask = new GetResultTask();
            getResultTask.execute();
            // display "loading..." popup after the activity window has finished rendering
            // (minus the map and image which can only be drawn after the background task is
            // complete)
            getWindow().getDecorView().post(new Runnable() {
                public void run() {
                    mPopupLoadingInProgress.showAtLocation(mTitle, Gravity.CENTER, 0, 0);
                }
            });
            setUpMapIfNeeded();
        } else {
            // Close the results page immediately if there is no network connectivity; to the user
            // it will appear as if the results page never opened
            ToastUtil.showShortToast(this, getString(R.string.toast_network_unavailable));
            finish();
        }
    }

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

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mPopupLoadingInProgress.isShowing())
            mPopupLoadingInProgress.dismiss();
    }

    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result_page, menu);
        return true;
    }

    // Define actions on selection of title bar icons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Back button
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        //Info button
        // get more information about the restaurant currently in focus by displaying a dialog
        if (id == R.id.action_info) {
            boolean networkIsAvailable = ServiceUtil.isNetworkAvailable(mContext);
            if (!mAlreadyQueriedLocuThisResult && networkIsAvailable) {
                // need to make an API call and network is available -- query Locu and display info
                new GetMoreInfoTask().execute(mReadResult);
                mPopupLoadingInProgress.showAtLocation(mTitle, Gravity.CENTER, 0, 0);
            } else if (!mAlreadyQueriedLocuThisResult) {
                // need to make an API call but network is unavailable
                ToastUtil.showShortToast(this, getString(R.string.toast_network_unavailable));
            } else { // mAlreadyQueriedLocuThisResult == true
                // just show the info dialog with the information already available
                new GetMoreInfoTask().execute(mReadResult);
                mPopupLoadingInProgress.showAtLocation(mTitle, Gravity.CENTER, 0, 0);
            }

        }

        //Menu button
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_menu && mReadResult != null) {
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
                displayMenuTask.execute(mCurrentResult);
                mPopupLoadingInProgress.showAtLocation(mTitle, Gravity.CENTER, 0, 0);
            } else {
                ToastUtil.showShortToast(this, getString(R.string.toast_network_unavailable));
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /* *********************** */
    /* ACTIVITY HELPER METHODS */
    /* *********************** */

    public void displayNextResult(View v){
        //display restaurant info goes here.
        //restaurant image loading needs to go in yet another asynctask
        if (mResultList == null)
            return;
        if (mResultList.size() <= 0) {
//            ToastUtil.showShortToast(this, "No more matches found.");
            DialogFragment noResultsDialog = NoResultsDialogFragment
                    .getInstance(Constants.NO_MORE_RESULTS);
            noResultsDialog.setCancelable(false);
            noResultsDialog.show(getFragmentManager(), "noResults");

        } else {
            //Clear the image before the next image is shown
            //Sometimes a result doesn't have an image, so it was showing the last one
            ((ImageView) findViewById(R.id.imgRestaurant)).setImageResource(0);
        }

        try {

            //Grab restaurant with largest random value.
            int largest = 0;
            for(int i=0; i<mResultList.getSize(); i++){
               if(mResultList.getRestaurant(i).getWeight() > largest){
                   largest = mResultList.getRestaurant(i).getWeight();
                   mReadResult = mResultList.getRestaurant(i);
               }
            }

            //remove a result from a list
            for(int j=0; j<mResultList.getSize(); j++){
                if(mResultList.getRestaurant(j) == mReadResult){
                   mCurrentResult = mResultList.remove(j);
                }
            }

            // Display random title bar text, e.g. "I suggest..." or "How about..."
            try {
                getSupportActionBar().setTitle(Html.fromHtml("<font color = '#ECCD7F'>"
                        + TextGeneratorUtil.randomizeFromArray(getResources()
                        .getStringArray(R.array.results_title)) +
                        "</font>"));
            } catch (NullPointerException e) { }


            //mReadResult = mResultList.remove(0);
            mAlreadyQueriedLocuThisResult = false;

            Log.i(TAG, "Drawing stars for " + mReadResult.getBusinessName() + ": " + mReadResult
                    .getRating());
            drawStars(mReadResult.getRating());
            mMap.clear();
            setUpMap(mReadResult);
            TextView title = (TextView)findViewById(R.id.restaurantName);

            // Display business name; reduce font size for long restaurant names
            int businessNameLength = mReadResult.getBusinessName().length();
            if (businessNameLength < 20) {
                title.setTextSize(28);
            } else if (businessNameLength >= 20 && businessNameLength < 29) {
                title.setTextSize(26);
            } else {
                title.setTextSize(20);
            }
            title.setText(mReadResult.getBusinessName());

            //Load the restaurant image

            if (mReadResult.getImageUrl() == null)
                ((ImageView)findViewById(R.id.imgRestaurant)).setImageResource(R.drawable.no_image);
            else if(nextImage!=null)
            {
                ImageView restaurantImage = (ImageView) findViewById(R.id.imgRestaurant);
                restaurantImage.setImageBitmap(nextImage);
                preload();
            }
            else {
                String tempURL = mReadResult.getImageUrl().toString();
                tempURL = tempURL.replace("ms.jpg", "o.jpg"); //this gets original image size
                URL imageURL = new URL(tempURL);

                LoadImageTask task = new LoadImageTask() {
                    protected void onPostExecute(Bitmap result) {
                        ImageView restaurantImage = (ImageView) findViewById(R.id.imgRestaurant);
                        restaurantImage.setImageBitmap(result);
                        preload();
                    }
                };
                task.execute(imageURL);
            }
        } catch(Exception e){}
    }


    /**
     * Checks if next result has image, and if so, loads it.
     * does nothing if a)resultList is empty, or b)result has no image.
     */
    public void preload()
    {
        nextImage = null;
        if (mResultList.isEmpty())return;
        //changed code here to make sure image updates with random selection
        int largest =0;
        for(int i=0; i<mResultList.getSize(); i++){
            if(mResultList.getRestaurant(i).getWeight() > largest){
                largest = mResultList.getRestaurant(i).getWeight();
                mReadResult = mResultList.getRestaurant(i);
            }
        }
        Restaurant nextRestaurant = mReadResult;
        //end of changes
        if(nextRestaurant.hasImageUrl())
        {   try{
            String temp = nextRestaurant.getImageUrl().toString().replace("ms.jpg", "o.jpg");
            final URL nextImageURL = new URL(temp);
            LoadImageTask task = new LoadImageTask() {
                protected void onPostExecute(Bitmap result) {
                    nextImage = result;
                }
            };
            task.execute(nextImageURL);
        } catch(Exception anyAndAllExceptions){/*and do nothing*/}}
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
        // Don't do anything
    }


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




    /* **************** */
    /* BACKGROUND TASKS */
    /* **************** */

    /**
     * 1. Query Yelp for a list of restaurants based on parameters obtained in main activity.
     * 2. Apply a selection process (randomization and weighting) to order the list.
     * 3. Display the first result.
     */
    private class GetResultTask extends AsyncTask<String, Void, RestaurantList> {

        @Override
        protected RestaurantList doInBackground(String... params) {
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
            Random rand = new Random();
            double randomNum;

            // dismiss loading popup
            if (mPopupLoadingInProgress.isShowing())
                mPopupLoadingInProgress.dismiss();

            // begin displaying results or tell user no results found
            if (mResultList == null /*|| mResultList.getSize() < 1*/) { // catch no results
                DialogFragment noResultsDialog = NoResultsDialogFragment
                        .getInstance(Constants.NO_RESULTS);
                noResultsDialog.setCancelable(false);
                noResultsDialog.show(getFragmentManager(), "noResults");
            } else { // results list is non-empty

                // remove red-listed restaurants from result list
                for (int i = 0; i < mResultList.getSize(); ) {
                    if (mResultList.getRestaurant(i).getRating() < minRating ||
                            db.isRestaurantInList(mResultList.getRestaurant(i), Constants.RED_LIST)) {
                        Restaurant removed = mResultList.removeRestaurant(i);
                        if (removed == null) // check if restaurant was removed successfully
                            i++;
                        // otherwise don't iterate as next restaurant will be at this index
                    } else
                        i++;
                }

                // Selection
                for (int i = 0; i < mResultList.getSize(); i++) {
                    randomNum = rand.nextInt((100 - 1) + 1);
                    Restaurant r = mResultList.getRestaurant(i);

                    // apply weight if current is green-listed restaurant
                    if (db.isRestaurantInList(r, Constants.GREEN_LIST)) {
                        randomNum = randomNum * 1.15;
                    }
                    // apply weight if current is yellow-listed restaurant
                    else if (db.isRestaurantInList(r, Constants.YELLOW_LIST)) {
                        Timestamp timestamp = Timestamp.valueOf(db.getRestaurantTimeStampFromList(r,
                                Constants.YELLOW_LIST));
                        long timeElapsed = System.currentTimeMillis() - timestamp.getTime();
                        // weight is linear with timestamp; if the restaurant was just added to the
                        // yellow list it receives a weight multiplier of 0.6; if it was added a
                        // week ago (the cutoff time at which it's removed from the list) it is
                        // essentially unweighted
                        randomNum = (randomNum * 0.6) + (timeElapsed * 6.6137566E-9);
                    }

                    mResultList.getRestaurant(i).setWeight((int) randomNum);
                    //helps debug
                    Log.i(TAG, "Restaurant: " + mResultList.getRestaurant(i).getBusinessName()
                            + " - Value: " + mResultList.getRestaurant(i).getWeight());
                }

                displayNextResult(findViewById(R.id.imgRestaurant));
            }
        }
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
                new MenuAndHoursExtension(mLocuKey).updateIfHasMenu(restaurant);
                mAlreadyQueriedLocuThisResult = true;
            }

            return restaurant;
        }

        @Override
        protected void onPostExecute(Restaurant restaurant) {
            if (mPopupLoadingInProgress != null && mPopupLoadingInProgress.isShowing())
                mPopupLoadingInProgress.dismiss();

            if (restaurant.hasMenus()) {
                Log.i(TAG, "Found menu for " + restaurant.getBusinessName());
                mDisplayRestaurantMenus = DisplayRestaurantMenusFragment
                        .getInstance(restaurant);
                mDisplayRestaurantMenus.show(getFragmentManager(), "menus");
            } else {
                Log.i(TAG, "Could not find menu for " + restaurant.getBusinessName());
                mMenuNotFoundDialog = new MenuNotFoundDialogFragment();
                mMenuNotFoundDialog.show(getFragmentManager(), "menuNotFound");
            }
        }
    }

    /**
     * Display information about the current restaurant. Query Locu for more information
     * if it has not already been done.
     */
    private class GetMoreInfoTask extends AsyncTask<Restaurant, Void, Restaurant> {
        @Override
        protected Restaurant doInBackground(Restaurant... params) {
            Restaurant r = params[0];

            if (!mAlreadyQueriedLocuThisResult) {
                Log.i(TAG, "Attempting to find open hours for " + r.getBusinessName());
                new MenuAndHoursExtension(mLocuKey).updateIfHasMenu(r);
                mAlreadyQueriedLocuThisResult = true;
            }

            return r;
        }

        @Override
        protected void onPostExecute(Restaurant r) {
            if (mPopupLoadingInProgress != null && mPopupLoadingInProgress.isShowing())
                mPopupLoadingInProgress.dismiss();

            if (mMoreInfoDialog != null && mMoreInfoDialog.isVisible())
                mMoreInfoDialog.dismiss();
            mMoreInfoDialog = MoreInfoDialogFragment.getInstance(mCurrentResult);
            mMoreInfoDialog.show(getFragmentManager(), "moreInfo");
        }
    }

    /**
     * Returns a bitmap image from the supplied URL
     */
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

}


//
//        setContentView(R.layout.activity_result_page);
//        Typeface buttonFont = Typeface.createFromAsset(getAssets(), "Chunkfive.otf");
//        Button greenButton = (Button) findViewById(R.id.greenButton);
//        greenButton.setTypeface(buttonFont);
//        //TextView myTextView = (TextView)findViewById(R.id.greenButton);
//       // myTextView.setTypeface(buttonFont);
//