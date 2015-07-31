package com.csc413.team5.fud5.utils;

public class Constants {
    // Database
    public static final int RESTAURANT_LISTS_TABLE_ID = 1;
    public static final int GREEN_LIST = 1;
    public static final int YELLOW_LIST = 2;
    public static final int RED_LIST = 3;

    // SharedPrefs key value pairs
    public static final String PREFS_FILE = "UserSettings";
    public static final String DEFAULT_SEARCH_TERM_INPUT_KEY = "defaultSearchTerm";
    public static final String DEFAULT_RADIUS_KEY = "defaultSearchRadius";
    public static final String DEFAULT_STAR_RATING_KEY = "defaultMinStar";
    public static final String LAST_GREEN_RESTAURANT = "lastGreenRestaurant";
    public static final String LAST_GREEN_RESTAURANT_TIMESTAMP = "lastGreenRestaurantTimestamp";

    public static final int DEFAULT_GREEN_FOLLOWUP_INTERVAL = 3;

    // parameters for "No results" dialog
    public static final int NO_RESULTS = 0;
    public static final int NO_MORE_RESULTS = 1;
}
