package com.csc413.team5.restaurantapiwrapper;

/**
 * A key for OAuthentication with the Locu API.
 * <p>
 * Created on 7/2/2015.
 *
 * @author Eric C. Black
 */
public class LocuApiKey {
    private final String key;

    public LocuApiKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    /**
     * @return a printable representation of this object.
     */
    @Override
    public String toString() {
        return "LocuApiKey{(hidden)}";
    }
}
