package com.csc413.team5.restaurantapiwrapper;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * An ordered list of {@link Restaurant} objects with suggested {@link MapBounds} which would
 * encompass all Restaurants in the list.
 * <p>
 * Implements most of the methods from {@link java.util.List} except those returning a generic
 * type (since RestaurantList only holds Restaurant objects).
 * <p>
 * Created on 6/24/2015.
 *
 * @author Eric C. Black
 */
public class RestaurantList {
     /* Member variables */

    protected ArrayList<Restaurant> restaurants;
    protected MapBounds bounds;

    /* Constructors */

    /**
     * Default constructor with empty list and default map bounds of all 0.0
     */
    public RestaurantList() {
        restaurants = null;
        bounds = null;
    }



    /* Getters */

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
     * Alias for {@link #getRestaurant(String)}
     */
    public Restaurant get(String id) {
        return getRestaurant(id);
    }

    /**
     * Alias for {@link #contains(String)}
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
     * Alias for {@link #contains(Object)}
     * @param r a {@link Restaurant} object
     * @return true if the Restaurant object was found in the RestaurantList, false otherwise
     */
    public boolean hasRestaurant(Restaurant r) {
        return contains(r);
    }

    /**
     * Alias for {@link #size()}
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
     * displays the restaurants in the list, or null if no information is available (most
     * likely due to the RestaurantList being empty).
     * <p>
     * Example:
     * <p>
     * <pre>
     *     double longitude = myRestaurantList.getMapBounds().getLongitude();
     * </pre>
     * @return a {@link MapBounds} object, or null if no information is available
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



    /* Other methods */

    /**
     * Remove the Restaurant at the specified index.
     * @param index  the index of the Restaurant to be removed
     * @return the Restaurant removed if successful, otherwise null
     */
    public Restaurant removeRestaurant(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= restaurants.size())
            throw new IndexOutOfBoundsException("RestaurantList index out of bounds");
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
     * Demotes the specified {@link Restaurant} the specified number of places in the list,
     * or to the end of the list, whichever is closer to its current position.
     *
     * @param r          a {@link Restaurant} to demote
     * @param numPlaces  an integer number of indices
     * @return the demoted Restaurant if it was found, otherwise null
     */
    public Restaurant demote(Restaurant r, int numPlaces) {
        return demote(r.id, numPlaces);
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
     * Promotes the specified {@link Restaurant} the specified number of places in the list,
     * or to the beginning of the list, whichever is closer to its current position.
     *
     * @param r          a {@link Restaurant} to promote
     * @param numPlaces  an integer number of indices
     * @return the promoted Restaurant if it was found, otherwise null
     */
    public Restaurant promote(Restaurant r, int numPlaces) {
        return promote(r.id, numPlaces);
    }

    /**
     * Trims the capacity of the underlying ArrayList to be the list's current size.
     */
    public void trimToSize() {
        restaurants.trimToSize();
    }



    /* Helper methods */

    /**
     * Refactor suggested map bounds. Called when adding or removing an item from the list.
     */
    private void refactorMapBounds() {
        // define the current bounding box
        double latNorth = -181.0, latSouth = 181.0, longEast = -181.0, longWest = 181.0;
        for (Restaurant r : restaurants) {
            double temp = r.addressMapable.getLatitude();
            if (temp > latNorth)
                latNorth = temp;
            if (temp < latSouth)
                latSouth = temp;
            temp = r.addressMapable.getLongitude();
            if (temp > longEast)
                longEast = temp;
            if (temp < longWest)
                longWest = temp;
        }
        bounds.spanLatitudeDelta = (latNorth - latSouth) * 1.1;
        bounds.spanLongitudeDelta = (longEast - longWest) * 1.1;
        bounds.center.setLatitude(latSouth + (latNorth - latSouth) / 2);
        bounds.center.setLongitude(longWest + (longEast - longWest) / 2);
        bounds.centerLatitude = bounds.center.getLatitude();
        bounds.centerLongitude = bounds.center.getLongitude();
    }



    /* Methods implementing java.util.List */

    /**
     * Inserts the specified {@link Restaurant} into this {@link RestaurantList} at the specified
     * location.
     * The object is inserted before the current element at the specified
     * location. If the location is equal to the size of this {@link RestaurantList}, the object
     * is added at the end. If the location is smaller than the size of this
     * {@link RestaurantList}, then all elements beyond the specified location are moved by one
     * position towards the end of the {@link RestaurantList}.
     *
     * @param location the index at which to insert.
     * @param object   the object to add.
     * @throws UnsupportedOperationException if adding to this {@link Restaurant} is not supported.
     * @throws ClassCastException            if the class of the object is inappropriate for this
     *                                       {@link Restaurant}.
     * @throws IllegalArgumentException      if the object cannot be added to this list.
     * @throws IndexOutOfBoundsException     if {@code location < 0 || location > size()}
     */
    public void add(int location, Object object) {
        restaurants.add(location, (Restaurant) object);
        refactorMapBounds();
    }

    /**
     * Adds the specified {@link Restaurant} at the end of this {@link RestaurantList}.
     *
     * @param object the {@link Restaurant} to add.
     * @return always true.
     * @throws UnsupportedOperationException if adding to this {@link RestaurantList} is not
     *                                       supported.
     * @throws ClassCastException            if the class of the object is inappropriate for this
     *                                       {@link RestaurantList}.
     * @throws IllegalArgumentException      if the object cannot be added to this
     *                                       {@link RestaurantList}.
     */
    public boolean add(Object object) {
        restaurants.add((Restaurant)object);
        refactorMapBounds();
        return true;
    }

    /**
     * Inserts the objects in the specified collection at the specified location
     * in this {@link RestaurantList}. The objects are added in the order they are returned from
     * the collection's iterator.
     *
     * @param location   the index at which to insert.
     * @param collection the collection of objects to be inserted.
     * @return true if this {@link RestaurantList} has been modified through the insertion, false
     * otherwise (i.e. if the passed collection was empty).
     * @throws UnsupportedOperationException if adding to this {@link RestaurantList} is not
     *                                       supported.
     * @throws ClassCastException            if the class of an object is inappropriate for this
     *                                       {@link RestaurantList}.
     * @throws IllegalArgumentException      if an object cannot be added to this
     *                                       {@link RestaurantList}.
     * @throws IndexOutOfBoundsException     if {@code location < 0 || location > size()}
     */
    public boolean addAll(int location, Collection collection) {
        return restaurants.addAll(location, collection);
    }

    /**
     * Adds the objects in the specified collection of {@link Restaurant} objects to the end of
     * this {@link RestaurantList}. The
     * objects are added in the order in which they are returned from the
     * collection's iterator.
     *
     * @param collection the collection of {@link Restaurant} objects.
     * @return {@code true} if this {@link RestaurantList} is modified, {@code false} otherwise
     * (i.e. if the passed collection was empty).
     * @throws UnsupportedOperationException if adding to this {@link RestaurantList} is not supported.
     * @throws ClassCastException            if the class of an object is inappropriate for this
     *                                       {@link RestaurantList}.
     * @throws IllegalArgumentException      if an object cannot be added to this {@link RestaurantList}.
     */
    public boolean addAll(Collection collection) {
        boolean modified = false;
        for (Object aCollection : collection) {
            restaurants.add((Restaurant) aCollection);
            modified = true;
        }
        refactorMapBounds();
        return modified;
    }

    /**
     * Removes all elements from this {@link RestaurantList}, leaving it empty. Clears MapBounds
     * information.
     *
     * @throws UnsupportedOperationException if removing from this {@link RestaurantList}
     *                                       is not supported.
     * @see #isEmpty
     * @see #size
     */
    public void clear() {
        restaurants.clear();
        bounds = null;
    }

    /**
     * Tests whether this {@link RestaurantList} contains the specified {@link Restaurant}.
     * A match is contingent only on the {@link Restaurant#id} field.
     *
     * @param object the object to search for.
     * @return {@code true} if object is an element of this {@link RestaurantList}, {@code false}
     * otherwise
     */
    public boolean contains(Object object) {
        return restaurants.contains(object);
    }

    /**
     * @param id a Yelp ID String
     * @return true if the Restaurant matching the specified Yelp ID was found, false otherwise
     */
    public boolean contains(String id) {
        return hasRestaurant(id);
    }

    /**
     * Tests whether this {@link RestaurantList} contains all objects contained in the
     * specified collection.  A match per {@link Restaurant} is contingent only on the
     * {@link Restaurant#id} field.
     *
     * @param collection the collection of {@link Restaurant} objects
     * @return {@code true} if all objects in the specified collection are
     * elements of this {@link RestaurantList}, {@code false} otherwise.
     */
    public boolean containsAll(Collection collection) {
        boolean doesContainAll = true;
        Iterator it = collection.iterator();
        while (it.hasNext() && doesContainAll) {
            Restaurant cur = (Restaurant)it.next();
            if (!hasRestaurant(cur.id))
                doesContainAll = false;
        }
        return doesContainAll;
    }

    /**
     * Searches this {@link RestaurantList} for the specified {@link Restaurant} and returns the
     * index of the first occurrence. A match is contingent only on the {@link Restaurant#id} field.
     *
     * @param object the object to search for.
     * @return the index of the first occurrence of the object or -1 if the
     * object was not found.
     */
    public int indexOf(Object object) {
        return restaurants.indexOf(object);
    }

    /**
     * Returns an iterator on the {@link Restaurant} elements of this {@link RestaurantList}.
     * The elements are iterated in the same order as they occur in the {@link RestaurantList}.
     *
     * @return an iterator on the elements of this {@link RestaurantList}.
     * @see Iterator
     */
    @NonNull
    public Iterator iterator() {
        return restaurants.iterator();
    }

    /**
     * Searches this {@link RestaurantList} for the specified object and returns the index of the
     * last occurrence.
     *
     * @param object the {@link Restaurant} object to search for.
     * @return the index of the last occurrence of the object, or -1 if the
     * object was not found.
     */
    public int lastIndexOf(Object object) {
        return restaurants.lastIndexOf(object);
    }

    /**
     * Returns a {@link RestaurantList} iterator on the elements of this {@link RestaurantList}.
     * The elements are iterated in the same order that they occur in the {@link RestaurantList}.
     *
     * @return a {@link RestaurantList} iterator on the elements of this {@link RestaurantList}
     * @see ListIterator
     */
    @NonNull
    public ListIterator listIterator() {
        return restaurants.listIterator();
    }

    /**
     * Returns a list iterator on the {@link Restaurant} elements of this {@link RestaurantList}.
     * The elements are iterated in the same order as they occur in the {@link RestaurantList}.
     * The iteration starts at the specified location.
     *
     * @param location the index at which to start the iteration.
     * @return a list iterator on the {@link Restaurant} elements of this {@link RestaurantList}.
     * @throws IndexOutOfBoundsException if {@code location < 0 || location > size()}
     * @see ListIterator
     */
    @NonNull
    public ListIterator listIterator(int location) {
        return restaurants.listIterator(location);
    }

    /**
     * Removes the first occurrence of the specified object from this {@link RestaurantList}.
     * A match is contingent only on the {@link Restaurant#id} field.
     *
     * @param object the object to remove.
     * @return true if this {@link RestaurantList} was modified by this operation, false
     * otherwise.
     * @throws UnsupportedOperationException if removing from this {@link RestaurantList} is not supported.
     */
    public boolean remove(Object object) {
        int size = restaurants.size();
        removeRestaurant(((Restaurant)object).id);
        if (restaurants.size() == size - 1)
            return true;
        else
            return false;
    }

    /**
     * Removes all occurrences in this {@link RestaurantList} of each {@link Restaurant} object in
     * the specified collection.
     *
     * @param collection the collection of {@link Restaurant} objects to remove.
     * @return {@code true} if this {@link RestaurantList} is modified, {@code false} otherwise.
     * @throws UnsupportedOperationException if removing from this {@link RestaurantList} is not
     *                                       supported.
     */
    public boolean removeAll(Collection collection) {
        return restaurants.removeAll(collection);
    }

    /**
     * Removes all objects from this {@link RestaurantList} that are not contained in the
     * specified collection.
     *
     * @param collection the collection of {@link Restaurant} objects to retain.
     * @return {@code true} if this {@link RestaurantList} is modified, {@code false} otherwise.
     * @throws UnsupportedOperationException if removing from this {@link RestaurantList} is not
     *                                       supported.
     */
    public boolean retainAll(Collection collection) {
        return restaurants.retainAll(collection);
    }

    /**
     * Replaces the element at the specified location in this {@link RestaurantList} with the
     * specified object. This operation does not change the size of the {@link RestaurantList}.
     *
     * @param location the index at which to put the specified object.
     * @param object   the object to insert.
     * @return the previous element at the index.
     * @throws UnsupportedOperationException if replacing elements in this {@link RestaurantList} is not supported.
     * @throws ClassCastException            if the class of an object is inappropriate for this
     *                                       {@link RestaurantList}.
     * @throws IllegalArgumentException      if an object cannot be added to this {@link RestaurantList}.
     * @throws IndexOutOfBoundsException     if {@code location < 0 || location >= size()}
     */
    public Object set(int location, Object object) {
        return restaurants.set(location, (Restaurant) object);
    }

    /**
     * Returns the number of elements in this {@link RestaurantList}.
     *
     * @return the number of elements in this {@link RestaurantList}.
     */
    public int size() {
        return restaurants.size();
    }

    /**
     * Returns a {@link List} of the specified portion of the {@link Restaurant}s in this
     * {@link RestaurantList} from the given start index to the end index minus one. The
     * returned {@link RestaurantList} is backed by this {@link RestaurantList} so changes to
     * it are reflected by the other.
     * <p>
     * As the returned data structure is a {@link List}, {@link MapBounds} data for the
     * {@link Restaurant}s will not be contained therein -- use subRestaurantList instead to
     * get refactored MapBounds data.
     *
     * @param start the index at which to start the sublist.
     * @param end   the index one past the end of the sublist.
     * @return a list of a portion of this {@link RestaurantList}.
     * @throws IndexOutOfBoundsException if {@code start < 0, start > end} or {@code end >
     *                                   size()}
     */
    @NonNull
    public List subList(int start, int end) {
        return restaurants.subList(start, end);
    }

    public RestaurantList subRestaurantList(int start, int end) {
        RestaurantList newRList = new RestaurantList();
        for (Restaurant r : restaurants.subList(start, end))
            newRList.add(r);
        newRList.refactorMapBounds();
        return newRList;
    }

    /**
     * Returns an array containing all {@link Restaurant} elements contained in this
     * {@link RestaurantList}.
     *
     * @return an array of the {@link Restaurant} elements from this {@link RestaurantList}.
     */
    @NonNull
    public Object[] toArray() {
        return restaurants.toArray();
    }




    /**
     * Compares the given object with the {@link RestaurantList}, and returns true if they
     * represent the <em>same</em> object using a class specific comparison. For
     * {@code RestaurantList}s, this means that they contain the same elements in exactly the same
     * order.
     *
     * @param object the {@link RestaurantList} object to compare with this object
     * @return boolean {@code true} if the object is the same as this object,
     * and {@code false} if it is different from this object.
     * @see #hashCode
     */
    @Override
    public boolean equals(Object object) {
        return (this.restaurants.equals(((RestaurantList)object).restaurants)
            && this.bounds.equals(((RestaurantList)object).bounds));
    }

    /**
     * Returns the hash code for this {@link RestaurantList}. It is calculated by taking each
     * element' hashcode and its position in the {@link RestaurantList} as well as the
     * {@link MapBounds} information into account.
     *
     * @return the hash code of the {@link RestaurantList}.
     */
    @Override
    public int hashCode() {
        return (restaurants.hashCode() + bounds.hashCode()) / 2;
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
