package com.csc413.team5.fud5;

import android.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;


public class UserPreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_preferences);
    }

    public void OnEULAButtonClick(View v) {
        DialogFragment eulaDialog = EulaDialogFragment.newInstance(false);
        eulaDialog.show(getFragmentManager(), "EULA");
    }
}
