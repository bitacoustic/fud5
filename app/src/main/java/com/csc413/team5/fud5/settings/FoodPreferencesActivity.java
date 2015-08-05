package com.csc413.team5.fud5.settings;

import android.content.Context;
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
import com.csc413.team5.fud5.utils.AppSettingsHelper;
import com.csc413.team5.fud5.utils.ToastUtil;

/**
 * Displays Food Preferences
 * Created by niculistana on 7/21/15.
 */
public class FoodPreferencesActivity extends AppCompatActivity {

    // UI Elements
    EditText locationInput;
    EditText searchTermInput;
    Spinner radiusSpinner;
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
        AppSettingsHelper.init(this);

        /* Initalize inputs*/
        locationInput = (EditText) findViewById(R.id.txtLocation);
        searchTermInput = (EditText) findViewById(R.id.txtLocation);

        /* Initialize radius spinner */
        radiusSpinner = (Spinner) findViewById(R.id.spnRadius);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.radius_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        radiusSpinner.setAdapter(adapter);

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
        // default search term
        AppSettingsHelper.setDefaultSearchTermInput(searchTermInput.getText().toString());
        // search radius
        AppSettingsHelper.setDefaultRadiusValue((
                (Spinner) findViewById(R.id.spnRadius)).getSelectedItem()
                .toString().split(" ")[0]);
        // star rating
        AppSettingsHelper.setDefaultStarRating(starRating.getRating());
        // set eula to true since user must've agreed to Eula already to use the app
        AppSettingsHelper.setEulaTrue();

        ToastUtil.showShortToast(this, "Settings Saved");
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