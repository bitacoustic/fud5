package com.csc413.team5.fud5;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class UserPreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_preferences);
    }

    public void OnEULAButtonClick(View v) {
        // TODO read stored data for a boolean hasAgreedToEula
        // pass parameter "hasAgreedToEula"=true since this activity is only accessible after
        // agreeing to the EULA
        DialogFragment eulaDialog = EulaDialogFragment.newInstance(true);
        eulaDialog.show(getFragmentManager(), "EULA");
    }
}
