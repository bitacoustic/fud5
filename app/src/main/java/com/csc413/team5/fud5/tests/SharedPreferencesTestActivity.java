package com.csc413.team5.fud5.tests;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.csc413.team5.fud5.R;

import java.util.Map;

/**
 * Displays the contents of the shared preferences file.
 */
public class SharedPreferencesTestActivity extends ActionBarActivity {
    public static final String PREFS_FILE = "UserSettings";
    private SharedPreferences userSettings;
    TextView out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_preferences_test);
        this.setTitle("Display Shared Preferences");

        out = (TextView) findViewById(R.id.textViewSharedPreferencesTestOut);

        // load user preferences
        userSettings = getSharedPreferences(PREFS_FILE, 0);
        Map<String, ?> allSettings = userSettings.getAll();

        out.setText(PREFS_FILE + ".xml contains fields:\n");

        for (Map.Entry<String, ?> entry : allSettings.entrySet()) {
            out.append(entry.getKey() + ": " + entry.getValue().toString() + '\n');
        }

    }
}
