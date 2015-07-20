package com.csc413.team5.fud5.userpreferences;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.csc413.team5.appdb.dbHelper;
import com.csc413.team5.fud5.AskToUseLocationFragment;
import com.csc413.team5.fud5.R;
import com.csc413.team5.fud5.utils.ToastUtil;

import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class AppSettingsActivity extends AppCompatActivity {

    // Member variables
    dbHelper db_helper;

    // User Preferences
    public static final String PREFS_FILE = "UserSettings";
    private SharedPreferences userSettings;
    private SharedPreferences.Editor userSettingsEditor;

    /*

    Location services: option to turn location services on/off
    Min star: option to display restaurants based on min. star (must use Yelp stars)
    Location search: option to provide a default search location to find restaurants
    Radius: option to display restaurants based on a specified radius 5 Carnivore/Herbivore/Omnivore: Prioritize restaurants based on diet preferences (Stretch goal)
    Food niche: bunch of restaurant categories that user likes
     */

    boolean isLocationPreferences;
    float minStar;
    String searchLocation;
    float searchRadius;
    Set<String> preferredNiches;
    String appLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize member variables
        db_helper = new dbHelper(this, null, null, 1);
        db_helper.getWritableDatabase();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

        // load user preferences
        userSettings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);

        // read the preference file's fields and assign default values
       /*
        * Min star: 3.5
        * Location search: 1600 Holloway Ave.
        * Radius: 5 mi.
        * Niches: Italian, Carnivore, Hole in the wall, affordable
        * App Language: Relative to device language
         */

        minStar = userSettings.getFloat("defaultMinStar", (float) 3.5);
        searchLocation = userSettings.getString("defaultSearchLocation", "1600 Holloway Ave.");
        searchRadius = userSettings.getFloat("defaultSearchRadius", (float) 1.5);
//        preferredNiches = userSettings.getStringSet("defaultPreferredNiches", getPreferredNiches());
        appLanguage = userSettings.getString("appLanguage", Locale.getDefault().getDisplayLanguage());

        TextView t1 = (TextView) findViewById(R.id.text_location_preferences);
        TextView t2 = (TextView) findViewById(R.id.text_default_min_star);
        TextView t3 = (TextView) findViewById(R.id.text_default_search_radius);
        TextView t4 = (TextView) findViewById(R.id.text_preferred_niches);
        TextView t5 = (TextView) findViewById(R.id.text_app_language);

        t1.append(" " + isLocationPreferences);
        t2.append(" " + minStar);
        t3.append(" " + searchLocation);
//        t4.append(" " + preferredNiches);
        t5.append(" " + appLanguage);

        // create a user settings editor for use in this activity
        userSettingsEditor = userSettings.edit();
    }

    public void wipeRestaurantData (View view) {

        DialogFragment appSettings = WipeDataDialogFragment.newInstance();
        appSettings.show(getFragmentManager(), "WipeData");
    }

    public void defaultFoodPreferences (View view) {
        ToastUtil.showShortToast(this, "defaultFoodPreferences");
    }

    private Set<String> getPreferredNiches(){

        // load preferred niches from resources
        // TODO This array must be able to be modified when Food Preferences is modified
        // ie: when user deletes a niche, update resources
        TypedArray preferred_niche = getResources().obtainTypedArray(R.array.preferred_niche_array);

        Set<String> preferredNicheSet = new TreeSet<String>();

        int preferred_niche_size = getResources().obtainTypedArray(R.array.preferred_niche_array).length();

        for (int i = 0; i < preferred_niche_size; i++) {
            preferredNicheSet.add(preferred_niche.getString(i));
        }

        return preferredNicheSet;
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
        }
    }
}
