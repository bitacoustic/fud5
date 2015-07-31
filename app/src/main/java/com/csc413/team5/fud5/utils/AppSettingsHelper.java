package com.csc413.team5.fud5.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.csc413.team5.restaurantapiwrapper.Restaurant;

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

    /**
     * If the user presses the green button, save the identifier of the last green restaurant so
     * the app can ask the user for feedback about the restaurant later.
     * @param r a Restaurant object
     */
    public static void setLastGreenRestaurant(Restaurant r) {
        userSettingsEditor.putString(Constants.LAST_GREEN_RESTAURANT, r.getBusinessName())
            .putLong(Constants.LAST_GREEN_RESTAURANT_TIMESTAMP, System.currentTimeMillis())
                .apply();
    }

    /**
     * Returns last green restaurant's ID, or empty string if the field is empty, either because the
     * green but hasn't been pressed before, or user has already responded to our prompt
     * to give feedback about the most recent green-listed restaurant
     * @return a Restaurant object containing just the ID, or "" if there is no data
     */
    public static String getLastGreenRestaurantID() {
        return userSettings.getString(Constants.LAST_GREEN_RESTAURANT, "");
    }

    /**
     * Returns the TimeinMillis that the user pressed the green button, or -1 if this information
     * is unavailable
     * @return the TimeinMillis that the user pressed the green button, or -1 if this information
     *         is unavailable
     */
    public static long getLastGreenRestaurantTimestamp() {
        return userSettings.getLong(Constants.LAST_GREEN_RESTAURANT_TIMESTAMP, -1);
    }

    public static void setLastGreenRestaurantTimestampToNow() {
        userSettingsEditor.putLong(Constants.LAST_GREEN_RESTAURANT_TIMESTAMP,
                System.currentTimeMillis()).apply();
    }

    /**
     * Clears the last green restaurant and its associated timestamp. This can be called after
     * the user has given feedback about the last visited (green-listed) restaurant.
     */
    public static void clearLastGreenRestaurant() {
        userSettingsEditor.putString(Constants.LAST_GREEN_RESTAURANT, "")
                .putLong(Constants.LAST_GREEN_RESTAURANT_TIMESTAMP, -1)
                .apply();
    }

    public static void clear() {
        userSettingsEditor.clear().commit();
    }
}
