package com.csc413.team5.fud5.utils;

import android.content.Context;
import android.content.SharedPreferences;

/** Contains methods that link to SharedPreferences
 * Created by niculistana on 7/28/15.
 */
public class AppSettingsHelper {

    private static SharedPreferences userSettings;
    private static SharedPreferences.Editor userSettingsEditor;

    public static AppSettingsHelper init(Context context) {
        /* Get shared preferences */
        userSettings = context.getSharedPreferences(Constants.PREFS_FILE, Context.MODE_PRIVATE);
        userSettingsEditor = userSettings.edit();
        return null;
    }

    public static String getDefaultRadiusValue() {
        return String.valueOf(userSettings.getFloat(Constants.DEFAULT_RADIUS_KEY, 3.0f)).concat(" mi");
    }

    public static float getStarRating() {
        return userSettings.getFloat(Constants.DEFAULT_STAR_RATING_KEY, 3.5f);
    }

    public static void setDefaultSearchTermInput(String searchTerm) {
        userSettingsEditor.putString(Constants.DEFAULT_SEARCH_TERM_INPUT_KEY, searchTerm).apply();
    }

    public static void setDefaultRadiusValue(Float radiusValue) {
        userSettingsEditor.putFloat(Constants.DEFAULT_RADIUS_KEY, radiusValue).apply();
    }

    public static void setDefaultStarRating(Float starRating) {
        userSettingsEditor.putFloat(Constants.DEFAULT_STAR_RATING_KEY, starRating).apply();
    }

    public static void setEulaTrue() {
        userSettingsEditor.putBoolean("hasAgreedToEula", true).apply();
    }

    public static void clear() {
        userSettingsEditor.clear().commit();
    }
}
