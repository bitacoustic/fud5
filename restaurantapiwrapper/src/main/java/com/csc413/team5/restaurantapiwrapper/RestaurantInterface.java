package com.csc413.team5.restaurantapiwrapper;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Representation of a restaurant as an object.
 * <p>
 * URLs and the business phone number can be accessed as URIs, which can be utilized by
 * front-end activities.
 * <p>
 * Created on 6/24/2015.
 *
 * @author Eric C. Black
 */
public interface RestaurantInterface {
    /*
    Getters
     */

    /**
     * @return unique Yelp ID; can be used for back-end lists
     */
    String getId();

    /**
     * If the restaurant is permanently closed, we don't want to display it.
     * @return true if the restaurant is *permanently* closed, otherwise false
     */
    boolean isClosed();

    /**
     * @return the name of the business
     */
    String getBusinessName();

    /**
     * @return address for display; might include cross-streets or other useful information
     */
    String getAddressDisplay();

    /**
     * TODO maybe this should be a latitude, longitude pair instead (utilize Location API Geocoder)
     * @return address that can be used to map location
     */
    String getAddressMapable();

    /**
     * @return phone number formatted for display
     */
    String getPhoneDisplay();

    /**
     * Returns phone number number formatted for device's Dialer
     *
     * @return a URI, if there is a phone number for this business, null otherwise
     */
    Uri getPhoneDialable();

    /**
     * @return URI for the business image
     */
    Uri getImageUrl();

    /**
     * @return true if business has an image URL, false otherwise
     */
    boolean hasImageUrl();

    /**
     * @return URI for the business's website; preference is given to the mobile
     *     version of the website, if it is available
     */
    Uri getBusinessUrl();

    /**
     * @return true if business has a website, false otherwise
     */
    boolean hasBusinessUrl();

    /**
     * Returns distance in *meters* from search location. If no latitude and longitude
     *     were provided in the request, then it will be the center of the most
     *     accurate part of the supplied address (e.g. the center of the city).
     *
     * @return distance in meters from search location; this value is *negative* if
     *         the information was not provided by Yelp
     */
    double getDistanceFromSearchLocation();

    /**
     * Returns a list of categories under which the location falls. Categories are
     * pairs of name and category_list alias, e.g. {"Asian Fusion", "asian_fusion"}.
     *
     * @return a list of categories under which the location falls
     * @see    Category
     */
    ArrayList<Category> getCategories();

    /**
     * Yelp: Returns a restaurant rating on a 5-star scale.
     *
     * @return restaurant rating from set {1, 1.5, 2, 2.5, 3, 3.4, 4, 4.5, 5} or
     *         a *negative value* if the information was not provided by Yelp
     */
    double getRating();

    /**
     * Yelp: Returns the URI of the Yelp rating image corresponding to the Yelp rating.
     * <p>According to Yelp API display requirements: "You always need to display our aggregate star
     * rating graphics and the number of reviews on which they&rsquo;re based."</p>
     *
     * @return URI of rating image
     */
    Uri getRatingImgUrl();

    /**
     * Yelp: Returns the number of Yelp reviews about this location.
     * <p>According to Yelp API display requirements: "You always need to display our aggregate star
     * rating graphics and the number of reviews on which they&rsquo;re based."</p>
     *
     * @return the number of Yelp reviews about this location, or a *negative* number
     *         if this information was not provided by Yelp
     */
    int getReviewCount();

    /**
     * @return true if the restaurant has special deals going on right now, false otherwise
     */
    boolean hasDeals();

    /**
     * @return URI for ongoing deals; null if there are none
     */
    Uri getDealsUrl();

    /**
     * @return URI for making a SeatMe reservation
     */
    Uri getSeatMeUrl();

    /**
     * @return true if business takes reservations through SeatMe, false otherwise
     */
    boolean hasSeatMeUrl();

    /**
     * @return URI for Eat24 delivery
     */
    Uri getEat24Url();

    /**
     * @return true if business delivers through Eat24, false otherwise
     */
    boolean hasEat24Url();
}
