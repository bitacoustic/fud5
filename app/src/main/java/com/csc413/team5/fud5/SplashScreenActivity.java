package com.csc413.team5.fud5;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

import com.csc413.team5.appdb.dbHelper;
import com.csc413.team5.fud5.dialogs.AskToUseLocationDialogFragment;
import com.csc413.team5.fud5.dialogs.EulaDialogFragment;
import com.csc413.team5.fud5.utils.Constants;
import com.csc413.team5.restaurantapiwrapper.Restaurant;

import java.sql.Timestamp;
import java.util.List;

import static android.view.WindowManager.LayoutParams;

public class SplashScreenActivity extends Activity
        implements AskToUseLocationDialogFragment.NoticeDialogListener {
    private static final String TAG = "SplashScreenActivity";
    private long mStartLoadTime;
    private long mYellowStaleCutoff;

    private Handler mHandler;
    private Runnable mRunAfterWait;

    public static final String PREFS_FILE = "UserSettings";
    private SharedPreferences userSettings;
    private SharedPreferences.Editor userSettingsEditor;

    private dbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStartLoadTime = System.currentTimeMillis();

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);


        mHandler = new Handler();
        mRunAfterWait = new Runnable() {
            public void run() {
                if (!userSettings.getBoolean("hasAgreedToEula", false))
                    checkEulaAndLocation(); // also checks on dismiss if location services enabled
                else
                    askToUseLocation(); // only prompts if GPS/network location services not enabled
            }
        };

        mHandler.postDelayed(mRunAfterWait, 3000);

        Log.i(TAG, "Loading user preferences");
        // load user preferences
        userSettings = getSharedPreferences(PREFS_FILE, 0);
        // create a user settings editor for use in this activity
        userSettingsEditor = userSettings.edit();

        // clear stale yellow list items
        db = new dbHelper(this, null, null, 1);
        mYellowStaleCutoff = mStartLoadTime - 604800000; // subtract a week in milliseconds
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
            if (isStale) {
                Log.i(TAG, "Removed " + thisRestaurantName + " from yellow list");
                Restaurant r = new Restaurant();
                r.setRestaurantName(thisRestaurantName);
                db.deleteRestaurantFromList(r, Constants.YELLOW_LIST); // delete this restaurant from yellow
                // list
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Called as part of the activity lifecycle when an activity is going into
     * the background, but has not (yet) been killed.  The counterpart to
     * {@link #onResume}.
     * <p/>
     * <p>When activity B is launched in front of activity A, this callback will
     * be invoked on A.  B will not be created until A's {@link #onPause} returns,
     * so be sure to not do anything lengthy here.
     * <p/>
     * <p>This callback is mostly used for saving any persistent state the
     * activity is editing, to present a "edit in place" model to the user and
     * making sure nothing is lost if there are not enough resources to start
     * the new activity without first killing this one.  This is also a good
     * place to do things like stop animations and other things that consume a
     * noticeable amount of CPU in order to make the switch to the next activity
     * as fast as possible, or to close resources that are exclusive access
     * such as the camera.
     * <p/>
     * <p>In situations where the system needs more memory it may kill paused
     * processes to reclaim resources.  Because of this, you should be sure
     * that all of your state is saved by the time you return from
     * this function.  In general {@link #onSaveInstanceState} is used to save
     * per-instance state in the activity and this method is used to store
     * global persistent data (in content providers, files, etc.)
     * <p/>
     * <p>After receiving this call you will usually receive a following call
     * to {@link #onStop} (after the next activity has been resumed and
     * displayed), however in some cases there will be a direct call back to
     * {@link #onResume} without going through the stopped state.
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onResume
     * @see #onSaveInstanceState
     * @see #onStop
     */
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
        if (!userSettings.getBoolean("hasAgreedToEula", false)) {
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
}
