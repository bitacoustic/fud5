package com.csc413.team5.fud5;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import static android.view.WindowManager.LayoutParams;

public class SplashScreenActivity extends Activity
        implements AskToUseLocationFragment.NoticeDialogListener {
    private long mStartLoadTime;
    protected boolean mhasAgreedToEula;
    protected boolean mUserClosedEula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        mStartLoadTime = System.currentTimeMillis();

        // TODO access a stored boolean to check if user has already agreed to EULA
        mhasAgreedToEula = false;


        // tell the Android OS to open the dialog after 3 seconds (= 3000 milliseconds)
        long splashScreenTime = 3000; //Magic numbers get their own variable
        long waitTime =  ((mStartLoadTime + splashScreenTime) - System.currentTimeMillis());
        if (waitTime < 0)
            waitTime = 0;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (!mhasAgreedToEula)
                    showEula(); // also checks on dismiss if location services enabled
                else {
                    askToUseLocation(); // only prompts if GPS/network location services not enabled
                }
            }
        }, waitTime);
    }

    /*@Override
    protected void onStart() {
        super.onStart();
    }*/

    @Override
    protected void onRestart() {
        super.onRestart();
        openMainActivity();
    }

    /**
     * if EULA hasn't been agreed to (first run) display dialog
     */
    public void showEula() {
        if (!mhasAgreedToEula) {
            DialogFragment eulaDialog = EulaDialogFragment.newInstance(false);
            eulaDialog.setCancelable(false);
            eulaDialog.show(getFragmentManager(), "EULA");
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

    @Override
    public void onLocationDialogPositiveClick(DialogFragment dialog) {
        // TODO save user choice

        // send user to settings screen to turn on location services
        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

        // and proceed to the next screen
        //openMainActivity();
    }

    @Override
    public void onLocationDialogNegativeClick(DialogFragment dialog) {
        // TODO save user choice
        //It'll prompt them the next time they run the app until they say yes
        openMainActivity();
    }

    private void openMainActivity(){
        finish();   // removes this activity from the back stack
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
    }
}
