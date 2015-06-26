package com.csc413.team5.restaurantapiwrapper;

import java.io.IOException;

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
 * @see    Restaurant
 */
public interface RestaurantListInterface {
    /**
     * Returns the Restaurant at specified index.
     *
     * @param index  get restaurant by index
     * @return Restaurant at specified index, or null if index is invalid
     */
    Restaurant getRestaurant(int index);

    /**
     * Returns the first Restaurant matching specified ID String.
     *
     * @param id  database identifier String, e.g. Yelp ID
     * @return Restaurant object, or null if restaurant was not found
     */
    Restaurant getRestaurant(String id);

    /**
     * @return number of entries in list
     */
    int getSize();

    /**
     * @return true if restaurant list is empty, false otherwise
     */
    boolean isEmpty();

    /**
     * Returns a MapBounds object holding suggested bounds of a map which
     * displays the restaurants in the list.
     * <p>
     * Example:
     * <p>
     * <pre>
     *     double longitude = myRestaurantList.getMapBounds().getLongitude();
     * </pre>
     * @return a MapBounds object
     * @see    MapBounds
     */
    MapBounds getMapBounds();

    /**
     * Sets contents of this restaurant list based on an encoded list of restaurants
     * from a restaurant API query, e.g. JSON-encoded String from Yelp API query
     *
     * @param in  encoded String with a list of restaurants
     *
     */
    void setRestaurantList(String in) throws IOException;

    /**
     * Remove the Restaurant at the specified index.
     * @param index
     * @return the Restaurant removed if successful, otherwise null
     */
    Restaurant removeRestaurant(int index);

    /**
     * Removes a restaurant from the list based on database identifier, e.g. Yelp ID
     *
     * @param id  database identifier
     * @return the Restaurant removed if successful, otherwise null
     */
    Restaurant removeRestaurant(String id);

    /**
     * Sorts restaurants exclusively by distance from search location.
     */
    void sortByDistance();

    // TODO if we can get this information
    // void sortByPrice();

    /**
     * Demotes the specified Restaurant the specified number of places in the list,
     * or to the end of the list, whichever is closer to its current position.
     *
     * @param id         database identifier String
     * @param numPlaces  an integer number of indices
     */
    void demote(String id, int numPlaces);

    /**
     * Promotes the specified Restaurant the specified number of places in the list,
     * or to the beginning of the list, whichever is closer to its current position.
     *
     * @param id         database identifier String
     * @param numPlaces  an integer number of indices
     */
    void promote(String id, int numPlaces);
}
