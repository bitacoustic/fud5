package com.csc413.team5.fud5;

import android.util.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * A list of Restaurants with suggested map bounds.
 * <p>
 * The list is initially in non-decreasing order by distance in meters from search
 * location. Then the list is modified based on the YellowList (possibly some results
 * demoted or promoted) or RedList (some results removed).
 * <p>
 * Created on 6/24/2015.
 *
 * @author Eric C. Black
 *
 * @see    Restaurant
 * @see    MapBounds
 */
public class RestaurantList implements RestaurantListInterface {
    /****************
     Member variables
     ****************/

    protected ArrayList<Restaurant> restaurants;
    protected MapBounds bounds;

    /************
     Constructors
     ************/

    /**
     * Default constructor with empty list and default map bounds of all 0.0
     */
    public RestaurantList() {
        restaurants = null;
        bounds = new MapBounds(0.0, 0.0, 0.0, 0.0);
    }

    /**
     * Constructor which parses results of a restaurant API query
     *
     * @param encodedInput  an encoded String, e.g. JSON from Yelp API query
     * @throws IOException if encoded input can't be handled
     */
    public RestaurantList(String encodedInput) throws IOException {
        this(); // call default constructor
        setRestaurantList(encodedInput);
    }

    /******
     Getters
     *******/

    /**
     * Returns the Restaurant at specified index.
     *
     * @param index get restaurant by index
     * @return Restaurant at specified index, or null if index is invalid
     */

    public Restaurant getRestaurant(int index) {
        return null; // TODO
    }

    /**
     * Returns the first Restaurant matching specified ID String.
     *
     * @param id database identifier String, e.g. Yelp ID
     * @return Restaurant object, or null if restaurant was not found
     */
    public Restaurant getRestaurant(String id) {
        return null; // TODO
    }

    /**
     * @return number of entries in list
     */
    public int getSize() {
        return restaurants.size();
    }

    /**
     * Returns a MapBounds object holding suggested bounds of a map which
     * displays the restaurants in the list.
     * <p/>
     * Example:
     * <p/>
     * <pre>
     *     double longitude = myRestaurantList.getMapBounds().getLongitude();
     * </pre>
     *
     * @return a MapBounds object
     * @see MapBounds
     */
    public MapBounds getMapBounds() {
        return bounds;
    }

    /******
     Setters
     *******/

    /**
     * Sets contents of this restaurant list based on an encoded list of restaurants
     * from a restaurant API query, e.g. JSON-encoded String from Yelp API query
     *
     * @param encodedInput  encoded String with a list of restaurants
     * @throws IOException if object read unsuccessful
     */
    public void setRestaurantList(String encodedInput) throws IOException {
        // clear existing list data if there is any
        restaurants.clear();
        bounds.clear();

        // convert JSON String to InputStream
        InputStream inStream = new ByteArrayInputStream(encodedInput
                .getBytes(StandardCharsets.UTF_8));
        // instantiate JsonReader to parse InputStream
        JsonReader in = new JsonReader(new InputStreamReader(inStream, "UTF-8"));

        // parse
        try {
            in.beginObject();
            while (in.hasNext()) {
                String field = in.nextName();
                if (field.equals("region")) {
                    in.beginObject();
                    while (in.hasNext()) {
                        String fieldRegion = in.nextName();
                        if (fieldRegion.equals("span")) {
                            in.beginObject();
                            while (in.hasNext()) {
                                String fieldRegionSpan = in.nextName();
                                if (fieldRegionSpan.equals("latitude_delta")) {
                                    bounds.setSpanLatitudeDelta(in.nextDouble());
                                } else if (fieldRegionSpan.equals("longitude_delta")) {
                                    bounds.setSpanLongitudeDelta(in.nextDouble());
                                } else {
                                    in.skipValue();
                                }
                            }
                            in.endObject();
                        } else if (fieldRegion.equals("center")) {
                            in.beginObject();
                            while (in.hasNext()) {
                                String fieldRegionCenter = in.nextName();
                                if (fieldRegionCenter.equals("latitude")) {
                                    bounds.setCenterLatitude(in.nextDouble());
                                } else if (fieldRegionCenter.equals("longitude")) {
                                    bounds.setCenterLongitude(in.nextDouble());
                                } else {
                                    in.skipValue();
                                }
                            }
                            in.endObject();
                        } else {
                            in.skipValue();
                        }
                    }
                } else if (field.equals("businesses")) {
                    in.beginArray();
                    while (in.hasNext()) {
                        // let the Restaurant constructor parse data per
                        // business entry
                        restaurants.add(new Restaurant(in));
                    }
                    in.endArray();
                } else {
                    in.skipValue();
                }
            }
            in.endObject();
        } finally {
            // close JsonReader; this needs to be done regardless of whether
            // an exception was thrown
            in.close();
        } // end try-finally block
    } // end setRestaurantList()

    /*************
     Other methods
     *************/

    /**
     * Remove the Restaurant at the specified index.
     *
     * @param index
     * @return the Restaurant removed if successful, otherwise null
     */
    public Restaurant removeRestaurant(int index) {
        return restaurants.remove(index);
    }

    /**
     * Removes a restaurant from the list based on database identifier, e.g. Yelp ID
     *
     * @param id database identifier
     * @return the Restaurant removed if successful, otherwise null
     */
    public Restaurant removeRestaurant(String id) {
        return null; // TODO
    }

    /**
     * Recently YellowListed items are demoted, older YellowListed items are
     * promoted.
     */
    public void applyYellowList() {
        // TODO
    }

    /**
     * Removes RedListed restaurants from list.
     */
    public void applyRedList() {
        // TODO
    }

    /**
     * Sorts restaurants exclusively by distance from search location.
     */
    public void sortByDistance() {
        // TODO
    }

    /**
     * Demotes the specified Restaurant the specified number of places in the list,
     * or to the end of the list, whichever is closer to its current position.
     *
     * @param id        database identifier String
     * @param numPlaces an integer number of indices
     */
    public void demote(String id, int numPlaces) {
        // TODO
    }

    /**
     * Promotes the specified Restaurant the specified number of places in the list,
     * or to the beginning of the list, whichever is closer to its current position.
     *
     * @param id        database identifier String
     * @param numPlaces an integer number of indices
     */
    public void promote(String id, int numPlaces) {
        // TODO
    }
}
