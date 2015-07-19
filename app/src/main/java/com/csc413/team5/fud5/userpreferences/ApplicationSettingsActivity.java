package com.csc413.team5.fud5.userpreferences;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.csc413.team5.appdb.dbHelper;
import com.csc413.team5.fud5.R;

public class ApplicationSettingsActivity extends ActionBarActivity
        implements ApplicationSettingsFragment.ApplicationSettingsConfirmListener {
    private static final String TAG = "AppSettings";

    protected CheckBox mCheckBoxUserSettings;
    protected CheckBox mCheckBoxRestaurantHistory;
    protected Button mBtnReset;
    protected Button mBtnLocationServices;

    protected boolean isUserSettingsChecked;
    protected boolean isRestaurantHistoryChecked;

    public static final String PREFS_FILE = "UserSettings";
    private SharedPreferences userSettings;
    private SharedPreferences.Editor userSettingsEditor;
    private dbHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_settings);

        this.setTitle(getApplicationContext().getResources()
                .getString(R.string.activity_application_settings_title));

        mCheckBoxUserSettings = (CheckBox) findViewById(R.id.checkBoxAppSettingsUser);

        userSettings = getSharedPreferences(PREFS_FILE, 0);
        userSettingsEditor = userSettings.edit();

        db = new dbHelper(this, null, null, 1);

        isUserSettingsChecked = false;
        mCheckBoxUserSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUserSettingsChecked = !isUserSettingsChecked;
                Log.i(TAG, "user settings checkbox was " +
                        (isUserSettingsChecked ? "checked" : "unchecked"));
            }
        });

        mCheckBoxRestaurantHistory = (CheckBox) findViewById(R.id.checkBoxAppSettingsRestaurant);
        isRestaurantHistoryChecked = false;
        mCheckBoxRestaurantHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRestaurantHistoryChecked = !isRestaurantHistoryChecked;
                Log.i(TAG, "restaurant history checkbox was " +
                        (isRestaurantHistoryChecked ? "checked" : "unchecked"));
            }
        });

        mBtnReset = (Button) findViewById(R.id.buttonAppSettingsReset);
        mBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserSettingsChecked || isRestaurantHistoryChecked) {
                    DialogFragment confirmDialog = ApplicationSettingsFragment
                            .newInstance(isUserSettingsChecked, isRestaurantHistoryChecked);
                    confirmDialog.show(getFragmentManager(), "ConfirmResetSettings");
                }
            }
        });

        mBtnLocationServices = (Button) findViewById(R.id.buttonManageLocationServices);
        mBtnLocationServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.provider.Settings
                        .ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
    }

    public boolean isUserSettingsChecked() {
        return isUserSettingsChecked;
    }

    public boolean isRestaurantHistoryChecked() {
        return isRestaurantHistoryChecked;
    }

    public void resetAppSettings() {
        if (isUserSettingsChecked) {
            userSettingsEditor.clear().commit();
            Log.i(TAG, "cleared user settings");
            Toast.makeText(this, "Cleared User Settings", Toast.LENGTH_SHORT).show();
        }

        if (isRestaurantHistoryChecked) {
            db.wipeRestaurantList(1);
            Log.i(TAG, "wiped green list (1)");
            db.wipeRestaurantList(2);
            Log.i(TAG, "wiped yellow list (2)");
            db.wipeRestaurantList(3);
            Log.i(TAG, "wiped red list (3)");
            Toast.makeText(this, "Cleared Restaurant History", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onApplicationSettingsConfirmPositiveClick(DialogFragment dialog) {
        resetAppSettings();
    }

    @Override
    public void onApplicationSettingsConfirmNegativeClick(DialogFragment dialog) {
        Toast.makeText(this, "Settings were not changed", Toast.LENGTH_SHORT).show();
    }
}
