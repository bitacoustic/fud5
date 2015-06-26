package com.csc413.team5.restaurantapiwrapper;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

/**
 * TODO Description
 * <p>
 * Created on 6/25/2015.
 *
 * @author Eric C. Black
 */
public class YelpClient {
    private final YelpApiKey key; // required

    // Search API parameters
    private String location; // required by Yelp API but defaults
                             // to "San Francisco, CA" if not specified
    private String term; // optional
    private int limit; // optional
    private int sort; // optional
    private String categoryFilter; // optional
    private int radiusFilter; // optional
    private boolean dealsFilter; // optional

    // Business API parameters
    private String id;

    // Yelp client parameters
    private static final String API_HOST = "api.yelp.com";
    private static final String SEARCH_PATH = "/v2/search";
    private static final String BUSINESS_PATH = "/v2/business";

    OAuthService service;
    Token accessToken;

    private YelpClient(Builder builder) {
        this.key = builder.key;
        this.location = builder.location;
        this.term = builder.term;
        this.sort = builder.sort;
        this.categoryFilter = builder.categoryFilter;
        this.radiusFilter = builder.radiusFilter;
        this.dealsFilter = builder.dealsFilter;
        this.id = builder.id;

        this.service = new ServiceBuilder().provider(TwoStepOAuth.class)
                .apiKey(key.getConsumerKey()).apiSecret(key.getConsumerSecret()).build();
        this.accessToken = new Token(key.getTokenKey(), key.getTokenSecret());
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getCategoryFilter() {
        return categoryFilter;
    }

    public void setCategoryFilter(String categoryFilter) {
        this.categoryFilter = categoryFilter;
    }

    public int getRadiusFilter() {
        return radiusFilter;
    }

    public void setRadiusFilter(int radiusFilter) {
        this.radiusFilter = radiusFilter;
    }

    public boolean isDealsFilter() {
        return dealsFilter;
    }

    public void setDealsFilter(boolean dealsFilter) {
        this.dealsFilter = dealsFilter;
    }

    public RestaurantList getRestaurantList() {
        // TODO
        // test -- returns empty list
        return new RestaurantList();
        // end test
    }


    /******************
     API Client methods
     ******************/

    /**
     * Creates and returns an {@link OAuthRequest} based on the API endpoint specified.
     *
     * @param type  one of enumerated YelpQueryType SEARCH, BUSINESS, or PHONE_SEARCH
     * @return <tt>OAuthRequest</tt>
     */
    /*
    private OAuthRequest createOAuthRequest(YelpQueryType type) {


        switch (type) {
            case SEARCH:

                break;
            case BUSINESS:

                break;
            case PHONE_SEARCH:
                break;
        }

        return request;
    }
    */

    /**
     * Sends an {@link OAuthRequest} and returns the {@link Response} body.
     *
     * @param request {@link OAuthRequest} corresponding to the API request
     * @return <tt>String</tt> body of API response
     */
    private String sendRequestAndGetResponse(OAuthRequest request) {
        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        return response.getBody();
    }




    public enum YelpQueryType {
        SEARCH, BUSINESS, PHONE_SEARCH
    }




    public static class Builder {
        private final YelpApiKey key;

        private String location;
        private String term;
        private int limit;
        private int sort;
        private String categoryFilter;
        private int radiusFilter;
        private boolean dealsFilter;

        private String id;

        public Builder(YelpApiKey key) {
            this.key = key;



            // default search parameters or no-search flags
            this.location = "San Francisco, CA";
            this.limit = -1; // if < 0, parameter won't be included
            this.sort = -1; // if < 0, parameter won't be included
            this.categoryFilter = "";
            this.radiusFilter = -1; // if < 0, parameter won't be included
            this.dealsFilter = false; // if false, parameter won't be included
            this.id = "ikes-place-san-francisco-4";
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder term(String term) {
            this.term = term;
            return this;
        }

        public Builder limit(int limit) {
            this.limit = limit;
            return this;
        }

        public Builder sort(int sort) {
            this.sort = sort;
            return this;
        }

        public Builder categoryFilter(String categoryFilter) {
            this.categoryFilter = categoryFilter;
            return this;
        }

        public Builder radiusFilter(int radiusFilter) {
            this.radiusFilter = radiusFilter;
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public YelpClient build() {
            return new YelpClient(this);
        }
    }
}
