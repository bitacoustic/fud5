package com.csc413.team5.restaurantapiwrapper;

/**
 * Weather extension for {@link Restaurant} which obtains current weather for a supplied location
 * <p>
 * Created on 9/2/2015
 *
 * @author Nicu Listana
 */
public class WeatherApiKey {
    private final String key;

    public WeatherApiKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String toString() {
        return "OpenWeatherMapApiKey{(hidden)}";
    }
}
