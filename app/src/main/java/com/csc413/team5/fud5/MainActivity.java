package com.csc413.team5.fud5;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity
        implements AskToUseLocationFragment.NoticeDialogListener {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    // Dialog AskToUseLocationFragment

    public void askToUseLocation() {
        DialogFragment dialogAskToUseLocation = new AskToUseLocationFragment();
        dialogAskToUseLocation.show(getFragmentManager(), "askToUseLocation");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Log.i(TAG, "User agreed to use location services.");
        // TODO save user choice and proceed to the next screen
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.i(TAG, "User did not agree to use location services.");
        // TODO save user choice and proceed to the next screen
    }
}
