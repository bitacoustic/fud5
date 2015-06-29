package com.csc413.team5.restaurantapiwrapper;

import java.util.ArrayList;

/**
 * A list of {@link Restaurant} objects with suggested {@link MapBounds}.
 * <p>
 * Created on 6/24/2015.
 *
 * @author Eric C. Black
 */
public class RestaurantList {
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
        bounds = null;
    }

    /******
     Getters
     *******/

    /**
     * Returns the Restaurant at specified index.
     *
     * @param index  get restaurant by index
     * @return Restaurant at specified index, or null if index is invalid
     * @throws IndexOutOfBoundsException if index is invalid
     */
    public Restaurant getRestaurant(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= restaurants.size())
            throw new IndexOutOfBoundsException("RestaurantList index out of bounds");
        else
            return restaurants.get(index);
    }

    /**
     * Returns the first Restaurant matching specified ID String.
     *
     * @param id  database identifier String, e.g. Yelp ID
     * @return Restaurant object, or null if restaurant was not found
     */
    public Restaurant getRestaurant(String id) {
        int foundIndex = -1;
        for (int i = 0; i < restaurants.size(); i++)
            if (restaurants.get(i).id.equals("id"))
                foundIndex = i;
        if (foundIndex > -1)
            return restaurants.get(foundIndex);
        else
            return null;
    }

    /**
     * @param id a Yelp ID String
     * @return true if the Restaurant matching the specified Yelp ID was found, false otherwise
     */
    public boolean hasRestaurant(String id) {
        boolean found = false;
        for (int i = 0; i < restaurants.size(); i++)
            if (restaurants.get(i).id.equals("id"))
                found = true;
        return found;
    }

    /**
     * @param r a {@link Restaurant} object
     * @return true if the Restaurant object was found in the RestaurantList, false otherwise
     */
    public boolean hasRestaurant(Restaurant r) {
        return restaurants.contains(r);
    }

    /**
     * @return number of entries in list
     */
    public int getSize() {
        if (restaurants == null)
            return 0;
        else
            return restaurants.size();
    }

    /**
     * @return true if restaurant list is empty, false otherwise
     */
    public boolean isEmpty() {
        return (restaurants == null);
    }

    /**
     * Returns a {@link MapBounds} object holding suggested bounds of a map which
     * displays the restaurants in the list.
     * <p>
     * Example:
     * <p>
     * <pre>
     *     double longitude = myRestaurantList.getMapBounds().getLongitude();
     * </pre>
     * @return a {@link MapBounds} object
     */
    public MapBounds getMapBounds() {
        return bounds;
    }

    /**
     * @return whether the RestaurantList has bounds that can be used for mapping
     */
    public boolean hasMapBounds() {
        return (bounds != null);
    }

    /*************
     Other methods
     *************/

    /**
     * Remove the Restaurant at the specified index.
     * @param index
     * @return the Restaurant removed if successful, otherwise null
     */
    public Restaurant removeRestaurant(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= restaurants.size())
            throw new IndexOutOfBoundsException("RestaurantList index out of bounds");
        else
            return restaurants.remove(index);
    }

    /**
     * Removes a restaurant from the list based on database identifier, e.g. Yelp ID
     *
     * @param id  database identifier
     * @return the Restaurant removed if successful, otherwise null
     */
    public Restaurant removeRestaurant(String id) {
        int removeIndex = -1;
        for (int i = 0; i < restaurants.size(); i++) {
            if (restaurants.get(i).id.equals(id))
                removeIndex = i;
        }
        if (removeIndex > -1)
            return restaurants.remove(removeIndex);
        else
            return null;
    }

    /**
     * Demotes the specified {@link Restaurant} the specified number of places in the list,
     * or to the end of the list, whichever is closer to its current position.
     *
     * @param id         database identifier String
     * @param numPlaces  an integer number of indices
     * @return the demoted Restaurant if it was found, otherwise null
     */
    public Restaurant demote(String id, int numPlaces) {
        int foundIndex = -1;
        int targetIndex;

        for (int i = 0; i < restaurants.size(); i++)
            if (restaurants.get(i).id.equals(id))
                foundIndex = i;

        if (foundIndex < 0)
            return null;

        Restaurant pickedRestaurant = restaurants.get(foundIndex);
        restaurants.remove(foundIndex);

        targetIndex = foundIndex + numPlaces;
        if (targetIndex >= restaurants.size())
            restaurants.add(pickedRestaurant);
        else
            restaurants.add(targetIndex, pickedRestaurant);

        return pickedRestaurant;
    }

    /**
     * Promotes the specified {@link Restaurant} the specified number of places in the list,
     * or to the beginning of the list, whichever is closer to its current position.
     *
     * @param id         database identifier String
     * @param numPlaces  an integer number of indices
     * @return the promoted Restaurant if it was found, otherwise null
     */
    public Restaurant promote(String id, int numPlaces) {
        int foundIndex = -1;
        int targetIndex;

        for (int i = 0; i < restaurants.size(); i++)
            if (restaurants.get(i).id.equals(id))
                foundIndex = i;

        if (foundIndex < 0)
            return null;

        Restaurant pickedRestaurant = restaurants.get(foundIndex);
        restaurants.remove(foundIndex);

        targetIndex = foundIndex - numPlaces;
        if (targetIndex < 0)
            restaurants.add(0, pickedRestaurant);
        else
            restaurants.add(targetIndex, pickedRestaurant);

        return pickedRestaurant;
    }

    /**
     * Trims the capacity of the underlying ArrayList to be the list's current size.
     */
    public void trimToSize() {
        restaurants.trimToSize();
    }

    /**
     * @return String representation of RestaurantList object.
     */
    @Override
    public String toString() {
        return "\nRestaurantList{" +
                "\nbounds=" + bounds +
                ",\nrestaurants=\n" + restaurants +
                '}';
    }
}
