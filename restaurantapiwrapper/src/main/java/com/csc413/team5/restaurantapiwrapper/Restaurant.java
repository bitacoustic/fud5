package com.csc413.team5.restaurantapiwrapper;

import android.location.Address;
import android.location.Location;
import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Representation of a restaurant.
 * <p>
 * Contains the useful information gathered by parsing an individual business component of
 * a JSON-encoded Yelp API query result.
 * <p>
 * URLs and the business phone number can be accessed as URIs, which can be utilized by
 * front-end activities (e.g. open URLs in a browser, dial a phone number).
 * <p>
 * Created on 6/24/2015.
 *
 * @author Eric C. Black
 */
public class Restaurant implements Serializable {
    /* Member variables */

    // Yelp
    protected String id;
    protected boolean isClosed;

    protected String name;

    protected Address address;
    protected String addressDisplay;
    protected Location addressMapable;

    protected String phoneDisplay;
    protected Uri phoneDialable;

    protected Uri imageUrl;
    protected boolean hasImageUrl;

    protected Uri businessUrl;
    protected boolean hasBusinessUrl;
    protected Uri mobileUrl;
    protected boolean hasMobileUrl;

    protected double distanceFromSearchLocation;

    protected ArrayList<YelpCategory> categories;

    protected double rating;
    protected Uri ratingImgUrl;

    protected int reviewCount;

    protected ArrayList<YelpDeal> yelpDeals;
    protected boolean hasDeals;

    protected Uri seatMeUrl;
    protected boolean hasSeatMeUrl;

    protected Uri eat24Url;
    protected boolean hasEat24Url;

    // Locu
    protected String locuId;
    protected Menus locuMenus;

    // Locu or Factual
    protected OpenHours hours;
    protected boolean hasHours;



    /* Constructors */

    /**
     * Constructs a restaurant with empty/null values. Use {@link RestaurantApiClient} to
     * set data via API(s).
     */
    public Restaurant() {
        id = "";
        isClosed = true;
        name = "";
        address = null;
        addressDisplay = "";
        addressMapable = null;
        phoneDisplay = "";
        phoneDialable = null;
        imageUrl = null;
        hasImageUrl = false;
        businessUrl = null;
        mobileUrl = null;
        hasMobileUrl = false;
        hasBusinessUrl = false;
        distanceFromSearchLocation = -1.0;
        categories = null;
        rating = -1.0;
        ratingImgUrl = null;
        reviewCount = -1;
        yelpDeals = null;
        hasDeals = false;
        seatMeUrl = null;
        hasSeatMeUrl = false;
        eat24Url = null;
        hasEat24Url = false;
        locuId = "";
        locuMenus = null;
        hours = new OpenHours();
        hasHours = false;
    }



    /* Getters */

    /**
     * @return unique Yelp ID; can be used for back-end lists
     */
    public String getId() {
        return id;
    }

    /**
     * If the restaurant is permanently closed, we don't want to display it.
     * @return true if the restaurant is *permanently* closed, otherwise false
     */
    public boolean isClosed() {
        return isClosed;
    }

    /**
     * @return the name of the business
     */
    public String getBusinessName() {
        return name;
    }

    /**
     * @return an Address object representing the location of the Restaurant (including physical
     *         address, latitude & longitude, and business phone number), or null
     *         if the information is not available
     */
    public Address getAddress() {
        return address;
    }

    /**
     * @return address for display; might include cross-streets or other useful information
     */
    public String getAddressDisplay() {
        return addressDisplay;
    }

    /**
     * @return {@link android.location.Location} coordinate of restaurant
     */
    public Location getAddressMapable() {
        return addressMapable;
    }

    /**
     * @return {@link android.location.Location} coordinate of restaurant
     */
    public Location getLocation() {
        return addressMapable;
    }

    /**
     * @return phone number formatted for display
     */
    public String getPhoneDisplay() {
        return phoneDisplay;
    }

    /**
     * Returns phone number number formatted for device's Dialer
     *
     * @return a URI, if there is a phone number for this business, null otherwise
     */
    public Uri getPhoneDialable() {
        return phoneDialable;
    }

    /**
     * @return URI for the business image
     */
    public Uri getImageUrl() {
        return imageUrl;
    }

    /**
     * @return true if business has an image URL, false otherwise
     */
    public boolean hasImageUrl() {
        return hasImageUrl;
    }

    /**
     * @return URI for the business's website
     */
    public Uri getBusinessUrl() {
        return businessUrl;
    }

    /**
     * @return true if business has a website, false otherwise
     */
    public boolean hasBusinessUrl() {
        return hasBusinessUrl;
    }

    /**
     * @return URI for the business's mobile website
     */
    public Uri getMobileUrl() {
        return mobileUrl;
    }

    /**
     * @return true if business has a mobile website, false otherwise
     */
    public boolean hasMobileUrl() {
        return hasMobileUrl;
    }

    /**
     * Returns distance in *meters* from search location. If no latitude and longitude
     *     were provided in the request, the distance will likely not be supplied and this
     *     field will have its default value of -1.0. In that case, use
     *     {@link #getDistanceFromLocation} instead.
     *
     * @return distance in meters from search location; this value is *negative* if
     *         the information was not provided by Yelp
     */
    public double getDistanceFromSearchLocation() {
        return distanceFromSearchLocation;
    }

    /**
     * Returns distance from search location with the specified unit of measure. Units of measure
     * allowed are:
     * <ul>
     *     <li>DistanceUnit.METERS
     *     <li>DistanceUnit.KILOMETERS
     *     <li>DistanceUnit.FEET
     *     <li>DistanceUnit.MILES
     * </ul>
     * <p>
     * If no latitude and longitude were provided in the request, the distance will likely not be
     * supplied and this field will have its default value of -1.0. In that case, use
     * {@link #getDistanceFromLocation} instead.
     *
     * @return distance in meters from search location; this value is *negative* if
     *         the information was not provided by Yelp
     */
    public double getDistanceFromSearchLocation(DistanceUnit units) {
        switch (units) {
            case METERS:
                return distanceFromSearchLocation;
            case KILOMETERS:
                return distanceFromSearchLocation * 1000;
            case FEET:
                return distanceFromSearchLocation * 3.28084;
            case MILES:
                return distanceFromSearchLocation * 0.000621371;
            default:
                return -1.0;
        }
    }

    /**
     * Returns a distance in *meters* from the location passed as a parameter to this
     * Restaurant. This only utilizes latitude and longitude, not altitude information, and
     * is approximate to ~ 10 meters.
     *
     * @param origin location to evaluate in terms of the Restaurant's location
     * @return the distance in meters from origin to this Restaurant
     */
    public double getDistanceFromLocation(Location origin) {
        return getDistanceFromLocation(origin, DistanceUnit.METERS);
    }

    /**
     * Returns a distance from the location passed as a parameter to this
     * Restaurant, using the specified unit of measure. Units of measure allowed are:
     * <ul>
     *     <li>DistanceUnit.METERS
     *     <li>DistanceUnit.KILOMETERS
     *     <li>DistanceUnit.FEET
     *     <li>DistanceUnit.MILES
     * </ul>
     * <p>
     * This only utilizes latitude and longitude, not altitude information, and
     * is approximate to ~ 10 meters.
     *
     * @param origin location to evaluate in terms of the Restaurant's location
     * @return the distance in meters from origin to this Restaurant
     */
    public double getDistanceFromLocation(Location origin, DistanceUnit units) {
        double distance;

        if (addressMapable == null)
            return -1.0;
        double span_latitude =
                Math.abs(addressMapable.getLatitude() - origin.getLatitude());
        double span_longitude =
                Math.abs(getAddressMapable().getLongitude() - origin.getLongitude()) * 111111
                        * Math.cos(span_latitude);
        span_latitude *= 111111;

        distance = Math.sqrt(Math.pow(span_latitude, 2) + Math.pow(span_longitude, 2));

        switch (units) {
            case METERS:
                return distance;
            case KILOMETERS:
                return distance * 1000;
            case FEET:
                return distance * 3.28084;
            case MILES:
                return distance * 0.000621371;
            default:
                return -1.0;
        }
    }

    /**
     * Returns a list of {@link YelpCategory} objects under which the location falls. Categories
     * are pairs of name and category_list alias, e.g. {"Asian Fusion", "asian_fusion"}.
     *
     * @return a list of categories under which the location falls
     */
    public ArrayList<YelpCategory> getCategories() {
        return categories;
    }

    /**
     * @return the number of Yelp categories assigned to this Restaurant
     */
    public int numCategories() {
        if (categories != null)
            return categories.size();
        else
            return 0;
    }

    /**
     * @return true if the Restaurant has been assigned a category, false otherwise
     */
    public boolean hasCategories() {
        if (categories == null)
            return false;
        else
            return true;
    }

    /**
     * Returns an individual {@link YelpCategory}.
     *
     * @param index of the YelpCategory to return
     * @return the {@link YelpCategory} at the specified index
     * @throws IndexOutOfBoundsException if index is out of range
     */
    public YelpCategory getCategory(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= categories.size())
            throw new IndexOutOfBoundsException("categories index out of range");
        else
            return categories.get(index);
    }

    /**
     * Yelp: Returns a restaurant rating on a 5-star scale.
     *
     * @return restaurant rating from set {1, 1.5, 2, 2.5, 3, 3.4, 4, 4.5, 5} or
     *         a *negative value* if the information was not provided by Yelp
     */
    public double getRating() {
        return rating;
    }

    /**
     * Yelp: Returns the URI of the Yelp rating image corresponding to the Yelp rating.
     * <p>According to Yelp API display requirements: "You always need to display our aggregate star
     * rating graphics and the number of reviews on which they're based."</p>
     *
     * @return URI of rating image
     */
    public Uri getRatingImgUrl() {
        return ratingImgUrl;
    }

    /**
     * Yelp: Returns the number of Yelp reviews about this location.
     * <p>According to Yelp API display requirements: "You always need to display our aggregate star
     * rating graphics and the number of reviews on which they're based."</p>
     *
     * @return the number of Yelp reviews about this location, or a *negative* number
     *         if this information was not provided by Yelp
     */
    public int getReviewCount() {
        return reviewCount;
    }

    /**
     * @return an ArrayList of {@link YelpDeal} objects, or null if there are no deals;
     *         check for empty list first using {@link #hasDeals()}
     */
    public ArrayList<YelpDeal> getYelpDeals() {
        return yelpDeals;
    }


    /**
     * @return the number of Yelp deals offered by this Restaurant
     */
    public int numDeals() {
        if (yelpDeals != null)
            return yelpDeals.size();
        else
            return 0;
    }

    /**
     * @return true if the restaurant has special deals going on right now, false otherwise
     */
    public boolean hasDeals() {
        return hasDeals;
    }

    /**
     * @return URI for making a SeatMe reservation
     */
    public Uri getSeatMeUrl() {
        return seatMeUrl;
    }

    /**
     * @return true if business takes reservations through SeatMe, false otherwise
     */
    public boolean hasSeatMeUrl() {
        return hasSeatMeUrl;
    }

    /**
     * @return URI for Eat24 delivery
     */
    public Uri getEat24Url() {
        return eat24Url;
    }

    /**
     * @return true if business delivers through Eat24, false otherwise
     */
    public boolean hasEat24Url() {
        return hasEat24Url;
    }


    public String getLocuId() {
        return locuId;
    }

    public Menus getLocuMenus() {
        return locuMenus;
    }

    public boolean hasLocuMenus() {
        return (locuMenus != null);
    }

    public OpenHours getHours() {
        return hours;
    }

    public boolean hasHours() {
        return hasHours;
    }


    // TODO
    // TEST to get current version of db code to work
    public void setRestaurantName(String name) {
        this.name = name;
    }


    /**
     * @return a String representation of a Restaurant object
     */
    @Override
    public String toString() {
        return "\n------------\nRestaurant{" +
                "\nid='" + id + "'" +
                ",\nisClosed=" + isClosed +
                ",\nname='" + name +
                "',\naddress=" + address +
                ",\naddressDisplay='" + addressDisplay +
                "',\naddressMapable='" + addressMapable +
                "',\nphoneDisplay='" + phoneDisplay +
                "',\nphoneDialable=" + phoneDialable +
                ",\nimageUrl=" + imageUrl +
                ",\nhasImageUrl=" + hasImageUrl +
                ",\nbusinessUrl=" + businessUrl +
                ",\nhasBusinessUrl=" + hasBusinessUrl +
                ",\nmobileUrl=" + mobileUrl +
                ",\nhasMobileUrl=" + hasMobileUrl +
                ",\ndistanceFromSearchLocation=" + distanceFromSearchLocation +
                ",\ncategories=" + categories +
                ",\nrating=" + rating +
                ",\nratingImgUrl=" + ratingImgUrl +
                ",\nreviewCount=" + reviewCount +
                ",\nyelpDeals=" + yelpDeals +
                ",\nhasDeals=" + hasDeals +
                ",\nseatMeUrl=" + seatMeUrl +
                ",\nhasSeatMeUrl=" + hasSeatMeUrl +
                ",\neat24Url=" + eat24Url +
                ",\nhasEat24Url=" + hasEat24Url +
                ",\nlocuId='" + locuId +
                "',\nlocuMenus=" + locuMenus +
                ",\nhours=" + hours +
                ",\nhasHours=" + hasHours +
                "}";
    }

} // end class
