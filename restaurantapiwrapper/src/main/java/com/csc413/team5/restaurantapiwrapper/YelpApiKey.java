package com.csc413.team5.restaurantapiwrapper;

/**
 * A Yelp API key. Members are inaccessible
 * <p>
 * Created on $(DATE).
 *
 * @author Eric C. Black
 */
public class YelpApiKey {
    private final String consumerKey;
    private final String consumerSecret;
    private final String tokenKey;
    private final String tokenSecret;

    public YelpApiKey(String consumerKey, String consumerSecret,
                      String tokenKey, String tokenSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.tokenKey = tokenKey;
        this.tokenSecret = tokenSecret;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public String getTokenKey() {
        return tokenKey;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }
}
