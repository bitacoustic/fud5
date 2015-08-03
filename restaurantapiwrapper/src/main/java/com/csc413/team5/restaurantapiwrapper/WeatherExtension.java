package com.csc413.team5.restaurantapiwrapper;

/**
 * Created by niculistana on 8/2/15.
 */
public class WeatherExtension {
    // weather parsing here
    // OpenWeatherMap client parameters
    // http://api.openweathermap.org/data/2.5/weather?lat=35&lon=139
    private static final String TAG = "MenuAndHoursExtension";
    private final WeatherApiKey key;

    public WeatherExtension(WeatherApiKey key) {
        this.key = key;
    }

    public String updateRestaurantWeather(Restaurant r) {
        // http://api.openweathermap.org/data/2.5/weather?lat=35&lon=139&appid={{key}}
        String protocol = "http://";
        String host = "api.openweathermap.org";
        String path = "/data/2.5/weather?";
        String latitude = String.valueOf(r.getLocation().getLatitude());
        String longitude = String.valueOf(r.getLocation().getLongitude());
        String apiKey = key.getKey();

        String url = protocol + host + path + "?lat=" +
                latitude + "&lon="+ longitude + "&appid=" + apiKey;

        String weather = "";
        // parse for weather

        // update restaurant url
        r.weather = weather;

        return weather;
    }
}
