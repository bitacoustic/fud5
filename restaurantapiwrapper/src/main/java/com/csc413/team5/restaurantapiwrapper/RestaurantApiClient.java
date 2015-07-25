package com.csc413.team5.restaurantapiwrapper;

import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * A client wrapper for Yelp. Create a new instance for each call.
 * <p>
 * Usage examples:
 * <p>Perform a search with multiple results: All parameters are optional, but it is recommended
 * to specify at least a location parameter. Since the Yelp API requires a location, it will
 * default to "San Francisco, CA" if that parameter is not explicitly defined.
 * <p><pre>
 *     YelpApiKey yelpKey = new YelpApiKey(consumerKey, consumerSecret, tokenKey, tokenSecret);
 *     RestaurantList results = new RestaurantApiClient.Builder(yelpKey)
 *         .term("italian").sort(0).radiusFilter(1000).build().getRestaurantList();
 * </pre>
 * <p>Get an individual Restaurant by Yelp ID: The {@link RestaurantApiClient#id} parameter is
 * required.
 * <p><pre>
 *     YelpApiKey yelpKey = new YelpApiKey(consumerKey, consumerSecret, tokenKey, tokenSecret);
 *     Restaurant result = new RestaurantApiClient.Builder(yelpKey)
 *         .id("ikes-place-san-francisco-4").build().getRestaurantByYelpID();
 * </pre>
 * <p>
 * References:
 * <ul>
 *     <a href="https://www.yelp.com/developers/documentation">Yelp API 2.0 documentation</a>
 * </ul>
 * Created on 6/25/2015.
 *
 * @author Eric C. Black
 */
public class RestaurantApiClient {
    private static final String TAG = "RestaurantApiClient";

    private final YelpApiKey key; // required parameter of RestaurantApiClient.Builder()

    // Search API parameters
    private String location; // required for Yelp API query but defaults
                             // to "San Francisco, CA" if not specified
    private Location cll; // optional
    private String term; // optional
    private int limit; // optional
    private int offset; // optional
    private int sort; // optional
    private String categoryFilter; // optional
    private int radiusFilter; // optional; must be in range 1-40000 (meters)
    private boolean dealsFilter; // optional

    // Business API parameters
    private String id;

    // Yelp client parameters
    private static final String YELP_API_HOST = "api.yelp.com";
    private static final String YELP_SEARCH_PATH = "/v2/search";
    private static final String YELP_BUSINESS_PATH = "/v2/business";
    private static final String YELP_PHONE_SEARCH_PATH = "/v2/phone_search";

    OAuthService service;
    Token accessToken;


    /**
     * Internal constructor; use
     * {@link com.csc413.team5.restaurantapiwrapper.RestaurantApiClient.Builder}
     * to instantiate the object.
     *
     * @param builder  results of RestaurantApiClient().Builder().build()
     */
    public RestaurantApiClient(Builder builder) {
        this.key = builder.key;
        this.location = builder.location;
        this.cll = builder.cll;
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


    /* Methods to build Restaurant or RestaurantList objects */

    /**
     * Returns a {@link RestaurantList} object using the results of a search using any of the
     * following parameters passed to
     * {@link com.csc413.team5.restaurantapiwrapper.RestaurantApiClient.Builder}:
     * <p>
     * <ul>
     *     <li>{@link #location} - Defaults to "San Francisco, CA" if no explicit
     *         location was passed
     *     <li>{@link #cll} - a {@link Location} specifying a latitude and
     *         longitude; if passed, this will be sent along with {@link #location} in order
     *         to obtain more localized search results
     *     <li>{@link #term} - Search terms
     *     <li>{@link #limit} - Maximum number of desired results
     *     <li>{@link #offset} - Start results at a particular index. In combination with
     *         {@link #limit}, this is useful for paginating results.
     *     <li>{@link #sort} - Sort mode: 0=Best matched (default), 1=Distance, 2=Highest Rated
     *         (see <a href="https://www.yelp.com/developers/documentation/v2/search_api">Yelp
     *         Search API documentation</a> for more information)
     *     <li>{@link #categoryFilter} - A list of comma-delimited categories. See
     *         <a href="https://www.yelp.com/developers/documentation/v2/all_category_list">
     *         Yelp Category List</a> for a master list. "food" or "foodtrucks,restaurants"
     *         are probably good choices in this context.
     *     <li>{@link #radiusFilter} - a range from the search location (a {@link Location} or
     *         the center of the search area) in meters; must be in range 1 - 40,0000
     *     <li>{@link #dealsFilter} - set to true to only display results with Yelp deals; by
     *         default this parameter is not sent with the request
     * </ul>
     * <p>
     * If the search produced no results, returns null.
     *
     * @return a {@link RestaurantList} object containing the results of the search, or null
     *         if the search produced no results
     * @throws IOException   if JSON text can't be parsed into JSONObject or JSONArray
     * @throws JSONException if JSONObject or JSONArray encountered a problem
     */
    public RestaurantList getRestaurantList() throws IOException, JSONException {
        String resultString;

        // create OAuth request
        OAuthRequest request = createOAuthRequest(YelpQueryType.SEARCH);

        // add parameters
        request.addQuerystringParameter("location", location);
        if (!term.equals(""))
            request.addQuerystringParameter("term", term);
        if (limit > 0)
            request.addQuerystringParameter("limit", String.valueOf(limit));
        if (offset >= 0)
            request.addQuerystringParameter("offset", String.valueOf(offset));
        if (sort >= 0)
            request.addQuerystringParameter("sort", String.valueOf(sort));
        if (!categoryFilter.equals(""))
            request.addQuerystringParameter("category_filter", categoryFilter);
        if (radiusFilter > 0)
            request.addQuerystringParameter("radius_filter", String.valueOf(radiusFilter));
        if (dealsFilter)
            request.addQuerystringParameter("deals_filter", "true");
        if (cll != null)
            request.addQuerystringParameter("cll", Double.toString(cll.getLatitude())
                    + "," + Double.toString(cll.getLongitude()));

        // perform the Yelp API call
        Log.i(TAG, "Searching for restaurants ...");
        resultString = sendRequestAndGetResponse(request);
        Log.i(TAG, "JSON response: " + resultString);
        // parse the Json and return a RestaurantList object
        return constructRestaurantList(resultString);
    }

    /**
     * Returns a {@link Restaurant} object matching the specified {@link #id}, or null if Yelp
     * could not find a restaurant matching the id.
     * @return a Restaurant matching the specified id, or null if not found
     * @throws IOException   if JSON text can't be parsed into JSONObject or JSONArray
     * @throws JSONException if JSONObject or JSONArray encountered a problem
     */
    public Restaurant getRestaurantByYelpID() throws IOException, JSONException {
        String resultString;
        Restaurant result;
        // create OAuth request
        OAuthRequest request = createOAuthRequest(YelpQueryType.BUSINESS);
        // perform the Yelp API call
        Log.i(TAG, "Searching for restaurant '" + id + "' ...");
        resultString = sendRequestAndGetResponse(request);
        Log.i(TAG, "JSON response: " + resultString);
        result = constructRestaurant(resultString);
        return result;
    }



    /* Static methods */

     /**
     * Returns a one-line String representation of an {@link Address} appropriate for the
     * RestaurantApiClient {@link #location} parameter. This method is declared static so
     * that it cna be used prior to the API call. Example usage:
     * <p>
     * <pre>
     *     String addrString = RestaurantApiClient.addressToString(addr);
     * </pre>
     * @param address  an {@link Address} object
     * @return a one-line String representation of the Address object
     */
    public static String addressToString(Address address) {
        StringBuilder result = new StringBuilder("");
        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
            if (address.getAddressLine(i).compareTo("") != 0)
                result.append(address.getAddressLine(i));
            if (i < address.getMaxAddressLineIndex() - 1)
                result.append(", ");
        }
        return result.toString();
    }

    /**
     * Converts a double distance from one specified {@link DistanceUnit} to another. Units
     * allowed are:
     * <p>
     * <ul>
     *     <li>DistanceUnit.METERS
     *     <li>DistanceUnit.KILOMETERS
     *     <li>DistanceUnit.FEET
     *     <li>DistanceUnit.MILES
     * </ul>
     *
     * @param num  a quantity of distance which is asserted to be of DistanceUnit inType
     * @param inType   distance unit of num
     * @param outType  target distance unit
     * @return the result of the conversion as a double
     */
    public static double convertDistanceUnits(double num, DistanceUnit inType,
                                              DistanceUnit outType) {
        if (inType == DistanceUnit.FEET) {
            switch (outType) {
                case FEET:
                    break;
                case MILES:
                    num *= 0.000189394;
                    break;
                case METERS:
                    num *= 0.3048;
                    break;
                case KILOMETERS:
                    num *= 0.0003048;
                    break;
            }
        } else if (inType == DistanceUnit.MILES) {
            switch (outType) {
                case FEET:
                    num *= 5280;
                    break;
                case MILES:
                    break;
                case METERS:
                    num *= 1609.34;
                    break;
                case KILOMETERS:
                    num *= 1.60934;
                    break;
            }
        } else if (inType == DistanceUnit.METERS) {
            switch (outType) {
                case FEET:
                    num *= 3.28084;
                    break;
                case MILES:
                    num *= 0.000621371;
                    break;
                case METERS:
                    break;
                case KILOMETERS:
                    num *= 0.001;
                    break;
            }
        } else if (inType == DistanceUnit.KILOMETERS) {
            switch (outType) {
                case FEET:
                    num *= 3280.84;
                    break;
                case MILES:
                    num *= 0.621371;
                    break;
                case METERS:
                    num *= 1000;
                    break;
                case KILOMETERS:
                    break;
            }
        }

        return num;
    }

    /**
     * Converts an integer distance from one specified {@link DistanceUnit} to another. Units
     * allowed are:
     * <p>
     * <ul>
     *     <li>DistanceUnit.METERS
     *     <li>DistanceUnit.KILOMETERS
     *     <li>DistanceUnit.FEET
     *     <li>DistanceUnit.MILES
     * </ul>
     *
     * @param num  a quantity of distance which is asserted to be of DistanceUnit inType
     * @param inType   distance unit of num
     * @param outType  target distance unit
     * @return the result of the conversion as a double
     */
    public static double convertDistanceUnits(int num, DistanceUnit inType, DistanceUnit outType) {
        return convertDistanceUnits((double) num, inType, outType);
    }



    /* RestaurantApiClient object methods */

    @Override
    public String toString() {
        return "RestaurantApiClient{\n" +
                "key=(hidden)" + '\n' +
                ", location='" + location + "'\n" +
                ", term='" + term + "'\n" +
                ", limit=" + limit + '\n' +
                ", sort=" + sort + '\n' +
                ", categoryFilter='" + categoryFilter + "'\n" +
                ", radiusFilter=" + radiusFilter + '\n' +
                ", dealsFilter=" + dealsFilter + '\n' +
                ", id='" + id + "'\n" +
                ", service=(hidden)" + '\n' +
                ", accessToken=(hidden)" + '\n' +
                '}';
    }



    /* Helper methods: Yelp API result parsing */

    private RestaurantList constructRestaurantList(String jsonString)
            throws IOException, JSONException {
        RestaurantList rList = new RestaurantList();
        JSONObject in = new JSONObject(jsonString);
        JSONArray inNames = in.names();

        for (int i = 0; i < inNames.length(); i++) {
            switch (inNames.getString(i)) {
                case "businesses": // access nested array of businesses
                    JSONArray inBusinesses = in.getJSONArray("businesses");
                    rList.restaurants = new ArrayList<>();
                    for (int j = 0; j < inBusinesses.length(); j++)
                        rList.restaurants.add(constructRestaurant(inBusinesses.getString(j)));
                    break;
                case "region": // access nested region information
                    JSONObject inRegion = in.getJSONObject("region");
                    // get span
                    JSONObject inRegionSpan = inRegion.getJSONObject("span");
                    double sLat = inRegionSpan.getDouble("latitude_delta");
                    double sLong = inRegionSpan.getDouble("longitude_delta");
                    // get center
                    JSONObject inRegionCenter = inRegion.getJSONObject("center");
                    double cLat = inRegionCenter.getDouble("latitude");
                    double cLong = inRegionCenter.getDouble("longitude");
                    // set RestaurantList MapBounds
                    rList.bounds = new MapBounds(sLat, sLong, cLat, cLong);
                    break;
                default:
                    break;
            } // end swithc
        } // end for

        if (rList.restaurants.isEmpty()) // if search produced no results
            return null;

        rList.trimToSize();
        return rList;
    }

    private Restaurant constructRestaurant(String jsonString) throws IOException, JSONException {
        Restaurant r = new Restaurant();
        JSONObject in = new JSONObject(jsonString);
        JSONArray inNames = in.names();

        for (int i = 0; i < inNames.length(); i++) {
            switch (inNames.getString(i)) {
                case "error":
                    // this field only occurs when the Yelp API call receives bad response,
                    // which means the specified ID was not found
                    Log.i(TAG, "Specified ID not found. Returning null.");
                    return null;
                case "id":
                    r.id = in.getString("id");
                    break;
                case "is_closed":
                    r.isClosed = true;
                    break;
                case "name":
                    r.name = in.getString("name");
                    break;
                case "location": // access nested location information
                    r.address = new Address(Locale.getDefault());
                    JSONObject inLocation = in.getJSONObject("location");
                    JSONArray inLocationNames = inLocation.names();
                    for (int j = 0; j < inLocationNames.length(); j++) {
                        switch (inLocationNames.getString(j)) {
                            case "address":
                                JSONArray inLocationAddress = inLocation.getJSONArray("address");
                                for (int k = 0; k < inLocationAddress.length(); k++)
                                    r.address.setAddressLine(k, inLocationAddress.getString(k));
                                break;
                            case "city":
                                r.address.setLocality(inLocation.getString("city"));
                                break;
                            case "state_code":
                                r.address.setAdminArea(inLocation.getString("state_code"));
                                break;
                            case "postal_code":
                                r.address.setPostalCode(inLocation.getString("postal_code"));
                                break;
                            case "country_code":
                                r.address.setCountryCode(inLocation.getString("country_code"));
                                break;
                            case "display_address":
                                JSONArray inLocationDisplayAddress =
                                        inLocation.getJSONArray("display_address");
                                StringBuilder addressDisplay = new StringBuilder("");
                                for (int k = 0; k < inLocationDisplayAddress.length(); k++) {
                                    addressDisplay.append(inLocationDisplayAddress.optString(k));
                                    if (k < inLocationDisplayAddress.length() - 1)
                                        addressDisplay.append("\n");
                                }
                                r.addressDisplay = addressDisplay.toString();
                                break;
                            case "coordinate":
                                JSONObject inLocationMapableAddress =
                                        inLocation.getJSONObject("coordinate");
                                Location addressMapable = new Location("Yelp");
                                addressMapable.setLatitude(inLocationMapableAddress
                                        .getDouble("latitude"));
                                addressMapable.setLongitude(inLocationMapableAddress
                                        .getDouble("longitude"));
                                r.addressMapable = addressMapable;
                                break;
                            default:
                                break;
                        } // end switch (inLocationNames.getString(j))
                    } // end for
                    break; // end case "location"
                case "display_phone":
                    r.phoneDisplay = in.getString("display_phone");
                    break;
                case "phone":
                    String phoneText = in.getString("phone");
                    if (phoneText.charAt(0) == '+')
                        phoneText = phoneText.substring(1);
                    r.phoneDialable = Uri.parse("tel:" + phoneText);
                    break;
                case "image_url":
                    r.imageUrl = Uri.parse(in.getString("image_url"));
                    r.hasImageUrl = true;
                    break;
                case "url":
                    r.businessUrl = Uri.parse(in.getString("url"));
                    r.hasBusinessUrl = true;
                    break;
                case "mobile_url":
                    r.mobileUrl = Uri.parse(in.getString("mobile_url"));
                    r.hasMobileUrl = true;
                    break;
                case "distance":
                    r.distanceFromSearchLocation = in.getDouble("distance");
                    break;
                case "categories": // access nested categories array of JSON objects
                    JSONArray inCategories = in.optJSONArray("categories");
                    r.categories = new ArrayList<>();
                    for (int j = 0; j < inCategories.length(); j++) {
                        JSONArray inCategoriesCurrent = inCategories.getJSONArray(j);
                        YelpCategory newYelpCategory =
                                new YelpCategory(inCategoriesCurrent.getString(0),
                                inCategoriesCurrent.getString(1));
                        r.categories.add(newYelpCategory);
                    }
                    break;
                case "rating":
                    r.rating = in.getDouble("rating");
                    r.ratingImgUrl = Uri.parse(in.getString("rating_img_url"));
                    r.reviewCount = in.getInt("review_count");
                    break;
                case "deals": // access nested array of JSON objects
                    r.hasDeals = true;
                    JSONArray inDeals = in.getJSONArray("deals");
                    for (int j = 0; j < inDeals.length(); j++) {
                        JSONObject inDealsEach = inDeals.getJSONObject(j);
                        YelpDeal newDeal = new YelpDeal();
                        boolean parsedSuccess = false;
                        try { // if all of this deal's data can't be parsed, don't add it
                            newDeal.id = inDealsEach.getString("id");
                            newDeal.title = inDealsEach.getString("title");
                            newDeal.whatYouGet = inDealsEach.getString("what_you_get");
                            newDeal.dealUrl = Uri.parse(inDealsEach.getString("url"));
                            newDeal.dealStartTime = inDealsEach.getInt("time_start");
                            if (inDealsEach.has("time_end")) {
                                newDeal.dealEndTime = inDealsEach.getInt("time_end");
                                newDeal.dealEnds = true;
                            }
                            parsedSuccess = true;
                        } finally {
                            if (parsedSuccess)
                                r.yelpDeals.add(newDeal);
                        }
                    }
                    break;
                case "reservation_url":
                    r.seatMeUrl = Uri.parse(in.getString("reservation_url"));
                    r.hasSeatMeUrl = true;
                    break;
                case "eat24_url":
                    r.eat24Url = Uri.parse(in.getString("eat24_url"));
                    r.hasEat24Url = true;
                    break;
                default:
                    break;
            } // end switch
        } // end for

        // If an Address object has already been constructed, populate it with
        // additional details -- useful for Google Maps
        if (r.address != null) {
            if (r.addressMapable != null) {
                r.address.setLatitude(r.addressMapable.getLatitude());
                r.address.setLongitude(r.addressMapable.getLongitude());
            }
            if (r.phoneDisplay.compareTo("") != 0)
                r.address.setPhone(r.phoneDisplay);
            if (r.name.compareTo("") != 0)
                r.address.setFeatureName(r.name);
            if (r.hasBusinessUrl)
                r.address.setUrl(r.businessUrl.toString());
        }

        return r;
    }



    /* API Client helper methods */

    /**
     * Creates and returns an {@link OAuthRequest} based on the API endpoint specified.
     *
     * @param type  one of enumerated YelpQueryType SEARCH, BUSINESS, or PHONE_SEARCH
     * @return <tt>OAuthRequest</tt>
     */
    private OAuthRequest createOAuthRequest(YelpQueryType type) {
        String path = "";
        switch (type) {
            case SEARCH:
                path = YELP_SEARCH_PATH;
                break;
            case BUSINESS:
                path = YELP_BUSINESS_PATH + "/" + id;
                break;
            case PHONE_SEARCH:
                path = YELP_PHONE_SEARCH_PATH;
                break;
        }

        return new OAuthRequest(Verb.GET, "http://" + YELP_API_HOST + path);
    }

    /**
     * Sends an {@link OAuthRequest} and returns the {@link Response} body.
     *
     * @param request {@link OAuthRequest} corresponding to the API request
     * @return <tt>String</tt> body of API response
     */
    private String sendRequestAndGetResponse(OAuthRequest request) {
        this.service.signRequest(this.accessToken, request);
        Log.i(TAG, "Sending request URL: " + request.getCompleteUrl());
        Log.i(TAG, "Sending request via: " + request.toString());
        Response response = request.send();
        Log.i(TAG, "Received response: " + response.getMessage());
        return response.getBody();
    }



    /**
     * Constants for sort-order.
     */
    public static class SortBy {
        public static final int BEST_MATCH = 0;
        public static final int DISTANCE = 1;
        public static final int HIGHEST_RATED = 2;
    }



    /* Builder */

    /**
     * Object builder for {@link RestaurantApiClient}. See {@link RestaurantApiClient} class
     * for usage examples.
     * <p>
     * <ul>
     *     <li>{@link #location} - Defaults to "San Francisco, CA" if no explicit
     *         location was passed
     *     <li>{@link #cll} - a {@link Location} specifying a latitude and
     *         longitude; if passed, this will be sent along with {@link #location} in order
     *         to obtain more localized search results
     *     <li>{@link #term} - Search terms
     *     <li>{@link #limit} - Maximum number of desired results
     *     <li>{@link #offset} - Start results at a particular index. In combination with
     *         {@link #limit}, this is useful for paginating results.
     *     <li>{@link #sort} - Sort mode: 0=Best matched (default), 1=Distance, 2=Highest Rated
     *         (see <a href="https://www.yelp.com/developers/documentation/v2/search_api">Yelp
     *         Search API documentation</a> for more information)
     *     <li>{@link #categoryFilter} - A list of comma-delimited categories. See
     *         <a href="https://www.yelp.com/developers/documentation/v2/all_category_list">
     *         Yelp Category List</a> for a master list. "food" or "foodtrucks,restaurants"
     *         are probably good choices in this context.
     *     <li>{@link #radiusFilter} - a range from the search location (a {@link Location} or
     *         the center of the search area) in meters; must be in range 1 - 40,0000
     *     <li>{@link #dealsFilter} - set to true to only display results with Yelp deals; by
     *         default this parameter is not sent with the request
     * </ul>
     */
    public static class Builder {
        private final YelpApiKey key;

        private String location;
        private Location cll;
        private String term;
        private int limit;
        private int offset;
        private int sort;
        private String categoryFilter;
        private int radiusFilter;
        private boolean dealsFilter;

        private String id;

        public Builder(YelpApiKey key) {
            this.key = key;

            // default search parameters or no-search flags
            this.location = "San Francisco, CA";
            this.cll = null;
            this.term = "";
            this.limit = -1; // if < 0, parameter won't be included
            this.sort = -1; // if < 0, parameter won't be included
            this.categoryFilter = "restaurants,foodtrucks";
            this.radiusFilter = -1; // if < 0, parameter won't be included
            this.dealsFilter = false; // if false, parameter won't be included
            this.id = "ikes-place-san-francisco-4";
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder cll(Location cll) {
            this.cll = cll;
            return this;
        }

        public Builder term(String term) {
            this.term = term;
            return this;
        }

        /**
         * @throws ParameterRangeException  if parameter is less than 1
         */
        public Builder limit(int limit) throws ParameterRangeException {
            if (limit < 1)
                throw new ParameterRangeException("limit must be 1 or greater");
            else
                this.limit = limit;
            return this;
        }

        /**
         * @throws ParameterRangeException  if parameter is less than 1
         */
        public Builder offset(int offset) throws ParameterRangeException {
            if (offset < 1)
                throw new ParameterRangeException("offset must be 1 or greater");
            else
                this.offset = offset;
            return this;
        }

        /**
         * @throws ParameterRangeException  if parameter is outide range 0-2
         */
        public Builder sort(int sort) throws ParameterRangeException {
            if (sort > 2 || sort < 0)
                throw new ParameterRangeException("sort requires integer input in range 0-2");
            else
                this.sort = sort;
            return this;
        }

        /**
         * Append new category filters to the default ("restaurants,foodtrucks").
         * For example, passing "asian_fusion" will result in an overall category filter parameter
         * of "restaurants,foodtrucks,asian_fusion".
         * @param categoryFilter a category filter as specified in Yelp API documentation
         * @return a RestaurantApiClient object to pass down the chain
         */
        public Builder categoryFilter(String categoryFilter) {
            if (!categoryFilter.equals(""))
                this.categoryFilter += "," + categoryFilter;
            return this;
        }

        /**
         * Replace category filter default ("restaurants,foodtrucks") with new category
         * filter parameter(s).
         * @param categoryFilter a category filter as specified in Yelp API documentation
         * @return a RestaurantApiClient object to pass down the chain
         */
        public Builder replaceCategoryFilterDefaults(String categoryFilter) {
            this.categoryFilter = categoryFilter;
            return this;
        }

        /**
         * @throws ParameterRangeException  if parameter is outside range 1-40000
         */
        public Builder radiusFilter(int radiusFilter) throws ParameterRangeException {
            if (radiusFilter > 40000 || radiusFilter < 1)
                throw new ParameterRangeException("radius_filter requires integer input in range" +
                        "1-40000");
            else
                this.radiusFilter = radiusFilter;
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        /**
         * @return a new {@link RestaurantApiClient} with the specified parameters; parameters
         *         not specified assume their default value
         */
        public RestaurantApiClient build() {
            Log.i(TAG, "Built RestaurantApiClient: " + this.toString());
            return new RestaurantApiClient(this);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "key=(hidden)" +
                    ", location='" + location + '\'' +
                    ", cll=" + cll +
                    ", term='" + term + '\'' +
                    ", limit=" + limit +
                    ", offset=" + offset +
                    ", sort=" + sort +
                    ", categoryFilter='" + categoryFilter + '\'' +
                    ", radiusFilter=" + radiusFilter +
                    ", dealsFilter=" + dealsFilter +
                    ", id='" + id + '\'' +
                    '}';
        }
    }

    /**
     * Enumerated search type for a Yelp API query, used to signal the proper request format.
     */
    private enum YelpQueryType {
        SEARCH, BUSINESS, PHONE_SEARCH
    }
}
