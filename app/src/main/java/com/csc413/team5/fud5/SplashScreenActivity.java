package com.csc413.team5.fud5;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

import com.csc413.team5.appdb.dbHelper;
import com.csc413.team5.fud5.dialogs.AskToUseLocationDialogFragment;
import com.csc413.team5.fud5.dialogs.EulaDialogFragment;
import com.csc413.team5.fud5.utils.AppSettingsHelper;
import com.csc413.team5.fud5.utils.Constants;
import com.csc413.team5.restaurantapiwrapper.Restaurant;

import java.sql.Timestamp;
import java.util.List;

import static android.view.WindowManager.LayoutParams;
import android.view.View;
import android.widget.ImageView;


public class SplashScreenActivity extends Activity
        implements AskToUseLocationDialogFragment.NoticeDialogListener {
    private static final String TAG = "SplashScreenActivity";

    private Handler mHandler;
    private Runnable mRunAfterWait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long mStartLoadTime = System.currentTimeMillis();

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);


        mHandler = new Handler();
        mRunAfterWait = new Runnable() {
            public void run() {
                if (!AppSettingsHelper.hasAgreedToEula())
                    checkEulaAndLocation(); // also checks on dismiss if location services enabled
                else
                    askToUseLocation(); // only prompts if GPS/network location services not enabled
            }
        };

        mHandler.postDelayed(mRunAfterWait, 3000);

        AppSettingsHelper.init(this);
        Log.i(TAG, "Loading user preferences");

        // clear stale yellow list items (those that are more than a week old)
        dbHelper db = new dbHelper(this, null, null, 1);
        long mYellowStaleCutoff = mStartLoadTime - Constants.YELLOW_STALE_INTERVAL_IN_MILLIS;
        Log.i(TAG, "Looking for stale yellow list restaurants (those having timestamp before " +
                new Timestamp(mYellowStaleCutoff).toString() + ")");
        List<String> yellowListTimestamps = db.getRestaurantTimeStampsFromList(Constants.YELLOW_LIST);
        List<String> yellowListRestaurants = db.getRestaurantNamesFromList(Constants.YELLOW_LIST);
        for (int i = 0; i < yellowListTimestamps.size(); i++) {
            Timestamp timestamp = Timestamp.valueOf(yellowListTimestamps.get(i));
            String thisRestaurantName = yellowListRestaurants.get(i);
            boolean isStale = timestamp.getTime() < mYellowStaleCutoff;
            Log.i(TAG, thisRestaurantName + " has timestamp " + timestamp + ": " +
                    (isStale ? "stale" : "OK"));
            if (isStale) { // delete this restaurant from yellow list
                Log.i(TAG, "Removed " + thisRestaurantName + " from yellow list");
                Restaurant r = new Restaurant();
                r.setRestaurantName(thisRestaurantName);
                db.deleteRestaurantFromList(r, Constants.YELLOW_LIST);
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        // if the app is still waiting, just execute the runnable
        mHandler.removeCallbacks(mRunAfterWait);
    }



    @Override
    protected void onRestart() {
        super.onRestart();
        // if EULA has been agreed to and location services are on, user is taken to the main
        // activity; otherwise, one or both dialogs are displayed
        checkEulaAndLocation();
    }



    /**
     * if EULA hasn't been agreed to (first run) display dialog; on dismiss, eula dialog checks
     * whether location has been turned on if the user just agreed to the terms of service
     */
    public void checkEulaAndLocation() {
        if (!AppSettingsHelper.hasAgreedToEula()) {
            DialogFragment eulaDialog = EulaDialogFragment.newInstance();
            eulaDialog.setCancelable(false);
            eulaDialog.show(getFragmentManager(), "EULA");
        }
        else {
            askToUseLocation();
        }
    }


    /**
     * Opens dialog AskToUseLocationDialogFragment if location services are not enabled; if they are
     * enabled, just go to main activity
     */
    public void askToUseLocation() {
        LocationManager lm = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        // check whether GPS and network providers are enabled
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) { }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) { }

        // only show dialog if location services are not enabled
        if (!gpsEnabled && !networkEnabled) {
            DialogFragment dialogAskToUseLocation = new AskToUseLocationDialogFragment();
            dialogAskToUseLocation.show(getFragmentManager(), "askToUseLocation");
        } else { // otherwise just open main activity
            openMainActivity();
        }
    }

    @Override
    public void onLocationDialogPositiveClick(DialogFragment dialog) {
        // send user to settings screen to turn on location services
        Log.i(TAG, "Sending user to Location Settings");
        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

        // when user is done with location services, onRestart() is called
    }

    @Override
    public void onLocationDialogNegativeClick(DialogFragment dialog) {
        //It'll prompt them the next time they run the app if location services are not enabled
        openMainActivity();
    }

    protected void openMainActivity(){
        finish();   // removes this activity from the back stack
        Intent intent = new Intent(this, MainActivity.class);
        Log.i(TAG, "Splash finished, sending to main activity");
        startActivity(intent);
        overridePendingTransition(0,0);
    }
    public void tapLogo(View v) {
        ((ImageView)findViewById(R.id.imgLogo)).setImageResource(R.drawable.fud5logo_lobster);
    }
}
