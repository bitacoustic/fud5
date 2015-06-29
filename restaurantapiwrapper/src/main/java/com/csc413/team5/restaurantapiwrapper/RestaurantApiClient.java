package com.csc413.team5.restaurantapiwrapper;

import android.location.Location;
import android.net.Uri;
import android.util.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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
    private final YelpApiKey key; // required

    // Search API parameters
    private String location; // required by Yelp API but defaults
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
     * Use {@link com.csc413.team5.restaurantapiwrapper.RestaurantApiClient.Builder} to construct
     * a new API call.
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

    /**
     * Returns a {@link RestaurantList} object using any of the following parameters passed
     * to {@link com.csc413.team5.restaurantapiwrapper.RestaurantApiClient.Builder}:
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
     *
     * @return a {@link RestaurantList} object containing the results of the search
     * @throws IOException
     * @throws JSONException
     */
    public RestaurantList getRestaurantList() throws IOException, JSONException {
        String resultString;
        JsonReader resultJson;

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
        resultString = sendRequestAndGetResponse(request);
        // parse the Json and return a RestaurantList object
        return constructRestaurantList(resultString);
    }

    /**
     * @return a {@link Restaurant} object matching the specified {@link #id}.
     * @throws IOException
     * @throws JSONException
     */
    public Restaurant getRestaurantByYelpID() throws IOException, JSONException {
        String resultString;
        Restaurant result = null;
        // create OAuth request
        OAuthRequest request = createOAuthRequest(YelpQueryType.BUSINESS);
        // perform the Yelp API call
        resultString = sendRequestAndGetResponse(request);
        result = constructRestaurant(resultString);
        return result;
    }

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

    /**************
     Helper methods
     **************/

    private JsonReader getJsonReader(String in) throws UnsupportedEncodingException {
        // convert JSON String to InputStream
        InputStream inStream = new ByteArrayInputStream(in.getBytes(StandardCharsets.UTF_8));
        // instantiate JsonReader to parse InputStream
        return new JsonReader(new InputStreamReader(inStream, "UTF-8"));
    }

    private RestaurantList constructRestaurantList(String jsonString)
            throws IOException, JSONException {
        RestaurantList rList = new RestaurantList();
        JSONObject in = new JSONObject(jsonString);
        JSONArray inNames = in.names();

        for (int i = 0; i < inNames.length(); i++) {
            switch (inNames.getString(i)) {
                case "businesses": // access nested array of businesses
                    JSONArray inBusinesses = in.getJSONArray("businesses");
                    rList.restaurants = new ArrayList<Restaurant>();
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

        if (rList.restaurants != null)
            rList.trimToSize();
        return rList;
    }

    /**
     * Parses JSON result of Yelp API query result for one business and stores
     * the encountered values.
     *
     * @param jsonString  a String containing the JSON-encoded result of a Yelp API query
     * @throws IOException if JsonReader object can't be opened or closed
     * @throws JSONException if JSONObject can't be initialized
     */
    public Restaurant constructRestaurant(String jsonString) throws IOException, JSONException {
        Restaurant r = new Restaurant();
        JSONObject in = new JSONObject(jsonString);
        JSONArray inNames = in.names();

        for (int i = 0; i < inNames.length(); i++) {
            switch (inNames.getString(i)) {
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
                    JSONObject inLocation = in.getJSONObject("location");
                    // display address
                    if (inLocation.has("display_address")) {
                        JSONArray inLocationDisplayAddress =
                                inLocation.getJSONArray("display_address");
                        StringBuilder addressDisplay = new StringBuilder("");
                        for (int j = 0; j < inLocationDisplayAddress.length(); j++) {
                            addressDisplay.append(inLocationDisplayAddress.optString(j));
                            if (j < inLocationDisplayAddress.length() - 1)
                                addressDisplay.append("\n");
                        }
                        r.addressDisplay = addressDisplay.toString();
                    }
                    // mapable location
                    if (inLocation.has("coordinate")) {
                        JSONObject inLocationMapableAddress =
                                inLocation.getJSONObject("coordinate");
                        Location addressMapable = new Location("Yelp");
                        addressMapable.setLatitude(inLocationMapableAddress
                                .getDouble("latitude"));
                        addressMapable.setLongitude(inLocationMapableAddress
                                .getDouble("longitude"));
                        r.addressMapable = addressMapable;
                    }
                    break;
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
                    if (inDeals.getJSONObject(0).has("url")) {
                        r.dealsUrl = Uri.parse(inDeals.getJSONObject(0).getString("url"));
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

        return r;
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
        Response response = request.send();
        return response.getBody();
    }


    /**
     * Enumerated search type for a Yelp API query.
     */
    public enum YelpQueryType {
        SEARCH, BUSINESS, PHONE_SEARCH
    }

    /**
     * Object builder for {@link RestaurantApiClient}.
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

        public Builder cll(Location cll) {
            this.cll = cll;
            return this;
        }

        public Builder term(String term) {
            this.term = term;
            return this;
        }

        public Builder limit(int limit) throws ParameterRangeException {
            if (limit < 1)
                throw new ParameterRangeException("limit must be 1 or greater");
            else
                this.limit = limit;
            return this;
        }

        public Builder offset(int offset) throws ParameterRangeException {
            if (offset < 1)
                throw new ParameterRangeException("offset must be 1 or greater");
            else
                this.offset = offset;
            return this;
        }

        public Builder sort(int sort) throws ParameterRangeException {
            if (sort > 2 || sort < 0)
                throw new ParameterRangeException("sort requires integer input in range 0-2");
            else
                this.sort = sort;
            return this;
        }

        public Builder categoryFilter(String categoryFilter) {
            this.categoryFilter = categoryFilter;
            return this;
        }

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

        public RestaurantApiClient build() {
            return new RestaurantApiClient(this);
        }
    }
}
