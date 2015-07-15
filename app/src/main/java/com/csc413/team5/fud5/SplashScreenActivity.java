package com.csc413.team5.fud5;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import static android.view.WindowManager.LayoutParams;

public class SplashScreenActivity extends Activity
        implements AskToUseLocationFragment.NoticeDialogListener {
    private long mStartLoadTime;
    protected boolean mhasAgreedToEula;

    private Handler mHandler;
    private Runnable mRunAfterWait;

    public static final String PREFS_FILE = "UserSettings";
    private SharedPreferences userSettings;
    private SharedPreferences.Editor userSettingsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStartLoadTime = System.currentTimeMillis();

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        // tell the Android OS to open the dialog after 3 seconds (= 3000 milliseconds)
        long splashScreenTime = 3000; //Magic numbers get their own variable
        long waitTime =  ((mStartLoadTime + splashScreenTime) - System.currentTimeMillis());
        if (waitTime < 0)
            waitTime = 0;

        mHandler = new Handler();
        mRunAfterWait = new Runnable() {
            public void run() {
                if (!mhasAgreedToEula)
                    checkEulaAndLocation(); // also checks on dismiss if location services enabled
                else
                    askToUseLocation(); // only prompts if GPS/network location services not enabled
            }
        };

        mHandler.postDelayed(mRunAfterWait, waitTime);

        // load user preferences
        userSettings = getSharedPreferences(PREFS_FILE, 0);
        // read the preference file's field "hasAgreedToEula"; if the field doesn't exist,
        // default to "false"
        mhasAgreedToEula = userSettings.getBoolean("hasAgreedToEula", false);
        // create a user settings editor for use in this activity
        userSettingsEditor = userSettings.edit();
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
        if (!mhasAgreedToEula) {
            DialogFragment eulaDialog = EulaDialogFragment.newInstance(mhasAgreedToEula);
            eulaDialog.setCancelable(false);
            eulaDialog.show(getFragmentManager(), "EULA");
        }
        else {
            askToUseLocation();
        }
    }


    /**
     * Opens dialog AskToUseLocationFragment if location services are not enabled; if they are
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
            DialogFragment dialogAskToUseLocation = new AskToUseLocationFragment();
            dialogAskToUseLocation.show(getFragmentManager(), "askToUseLocation");
        } else { // otherwise just open main activity
            openMainActivity();
        }
    }

    public void setHasAgreedToEula(Boolean hasAgreed) {
        // save to persistent user settings; note that in other situations multiple edits can be
        // chained together before the final commit
        mhasAgreedToEula = hasAgreed;
        userSettingsEditor.putBoolean("hasAgreedToEula", hasAgreed).commit();
    }

    @Override
    public void onLocationDialogPositiveClick(DialogFragment dialog) {
        // send user to settings screen to turn on location services
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
        startActivity(intent);
        overridePendingTransition(0,0);
    }
}
