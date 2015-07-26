package com.csc413.team5.fud5.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.csc413.team5.fud5.R;
import com.csc413.team5.fud5.utils.ToastUtil;

/**
 * Displays Food Preferences
 * Created by niculistana on 7/21/15.
 */
public class FoodPreferencesActivity extends AppCompatActivity {

    // Declare user settings
    public static final String PREFS_FILE = "UserSettings";
    private SharedPreferences userSettings;
    private SharedPreferences.Editor userSettingsEditor;

    EditText locationInput;
    EditText searchTermInput;
    Spinner ratingsSpinner;
    RatingBar starRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_preferences);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        String title = getString(R.string.title_activity_food_preferences);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#43428A")));
        getSupportActionBar().setTitle(Html.fromHtml("<font color = '#ECCD7F'>" + title + "</font>"));

        /* Get shared preferences */
        userSettings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        userSettingsEditor = userSettings.edit();

        /* Initalize inputs*/
        locationInput = (EditText) findViewById(R.id.txtLocation);
        searchTermInput = (EditText) findViewById(R.id.txtLocation);

        /* Initialize radius spinner */
        // TODO: Migrate this to xml file instead
        //Spinner code from Android example
        ratingsSpinner = (Spinner) findViewById(R.id.spnRadius);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.radius_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        ratingsSpinner.setAdapter(adapter);

        /* Initialize rating bar */
        starRating = (RatingBar) findViewById(R.id.ratingBar);
    }

    //Closes keyboard if user clicks off of it
    //Code thanks to Lalit Poptani on stackoverflow.com
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    // Save button click handler
    public void onSaveButtonPress(View v) {
//        // search location
        userSettingsEditor.putString("defaultSearchLocation", locationInput.getText().toString()).apply();
        // search radius
        userSettingsEditor.putFloat("defaultSearchRadius", Float.parseFloat(ratingsSpinner.getSelectedItem().toString().substring(0,3))).apply();
//        // star rating
        userSettingsEditor.putFloat("defaultMinStar", starRating.getRating()).apply();
//        // default search term
////            userSettings.getStringSet("defaultPreferredNiches", getPreferredNiches().apply());
        ToastUtil.showShortToast(this, "Settings saved.");
        finish();
        startActivity(this.getParentActivityIntent());
    }

    // Override back button on title bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AreYouSureDialogFragment cancelDialog = new AreYouSureDialogFragment();
                cancelDialog.show(getFragmentManager(), "cancelDialog");
        }
        return true;
    }

    // Override device back button
    @Override
    public void onBackPressed() {
        AreYouSureDialogFragment cancelDialog = new AreYouSureDialogFragment();
        cancelDialog.show(getFragmentManager(), "cancelDialog");
    }
}