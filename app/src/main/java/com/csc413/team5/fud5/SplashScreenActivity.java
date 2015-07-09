package com.csc413.team5.fud5;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import static android.view.WindowManager.*;

public class SplashScreenActivity extends Activity implements AskToUseLocationFragment.NoticeDialogListener  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);
    }

    @Override
    protected void onStart() {
        // tell the Android OS to open the dialog after 3 seconds (= 3000 milliseconds)
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                askToUseLocation();
            }
        }, 3000);
        super.onStart();
    }


    // Dialog AskToUseLocationFragment
    public void askToUseLocation() {
        DialogFragment dialogAskToUseLocation = new AskToUseLocationFragment();
        dialogAskToUseLocation.show(getFragmentManager(), "askToUseLocation");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // TODO save user choice and proceed to the next screen
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // TODO Exit app
    }
}
