package com.csc413.team5.restaurantapiwrapper;

import android.net.Uri;
import android.util.JsonReader;

import java.io.IOException;
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
public class Restaurant implements RestaurantInterface {
    /****************
     Member variables
     ****************/

    /*
    Accessible data members
     */

    private String id;
    private boolean isClosed;

    private String name;

    private String addressDisplay;
    private String addressMapable;

    private String phoneDisplay;
    private Uri phoneDialable;

    private Uri imageUrl;
    private boolean hasImageUrl;

    private Uri businessUrl;
    private boolean hasBusinessUrl;

    private double distanceFromSearchLocation;

    private ArrayList<Category> categories;

    private double rating;
    private Uri ratingImgUrl;

    private int reviewCount;

    private boolean hasDeals;
    private Uri dealsUrl;

    private Uri seatMeUrl;
    private boolean hasSeatMeUrl;

    private Uri eat24Url;
    private boolean hasEat24Url;

    /*
    Hidden data members and structures
     */

    private Uri mobileUrl;
    private Uri desktopUrl;

    /************
     Constructors
     ************/

    /**
     * Construct a restaurant with empty/null values. Must use setRestaurant() to parse
     * JSON and populate the member variables with actual data.
     */
    public Restaurant() {
        id = "";
        isClosed = true;
        name = "";
        addressDisplay = "";
        addressMapable = "";
        phoneDisplay = "";
        phoneDialable = null;
        imageUrl = null;
        hasImageUrl = false;
        businessUrl = null;
        hasBusinessUrl = false;
        distanceFromSearchLocation = -1.0;
        categories = null;
        rating = -1.0;
        ratingImgUrl = null;
        reviewCount = -1;
        hasDeals = false;
        dealsUrl = null;
        seatMeUrl = null;
        hasSeatMeUrl = false;
        eat24Url = null;
        hasEat24Url = false;
    }

    /**
     * Parses JSON result of Yahoo API query result for one business and stores
     * the encountered values.
     *
     * @param in  JsonReader object which is a business in a list of businesses;
     *            this is passed from RestaurantList as it parses the Yelp API query result
     * @throws IOException if JsonReader object can't be opened or closed
     */
    public Restaurant(JsonReader in) throws IOException {
        this(); // call default constructor

        in.beginObject();

        while (in.hasNext()) {
            String field = in.nextName();
            switch (field) {
                case "id":
                    id = in.nextString();
                    break;
                case "is_closed":
                    isClosed = in.nextBoolean();
                    break;
                case "name":
                    name = in.nextString();
                    break;
                case "location":
                    // parse sub-fields of field "location"
                    in.beginObject();
                    while (in.hasNext()) {
                        String fieldNested = in.nextName();
                        if (fieldNested.equals("display_address")) {
                            addressDisplay = in.nextString();
                        } else if (fieldNested.equals("address")) {
                            addressMapable = in.nextString();
                        } else {
                            in.skipValue();
                        }
                    }
                    in.endObject();
                    break;
                case "display_phone":
                    phoneDisplay = in.nextString();
                    break;
                case "phone":
                    String phoneText = in.nextString();
                    if (phoneText.charAt(0) == '+')
                        phoneText = phoneText.substring(1);
                    if (!phoneText.equals(""))
                        phoneDialable = Uri.parse("tel:" + phoneText);
                    else
                        phoneDialable = null;
                    break;
                case "image_url":
                    String imageUrlText = in.nextString();
                    if ("".equals(imageUrlText) || imageUrlText == null) {
                        imageUrl = null;
                        hasImageUrl = false;
                    } else {
                        imageUrl = Uri.parse(imageUrlText);
                        hasImageUrl = true;
                    }
                    break;
                case "url":
                    String businessUrlText = in.nextString();
                    if ("".equals(businessUrlText) || businessUrlText == null)
                        desktopUrl = null;
                    else
                        desktopUrl = Uri.parse(in.nextString());
                    break;
                case "mobile_url":
                    String mobileUrlText = in.nextString();
                    if ("".equals(mobileUrlText) || mobileUrlText == null)
                        mobileUrl = null;
                    else
                        mobileUrl = Uri.parse(in.nextString());
                    break;
                case "distance":
                    distanceFromSearchLocation = in.nextDouble();
                    break;
                case "categories":
                    in.beginArray();
                    while (in.hasNext()) {
                        in.beginArray();
                        while (in.hasNext()) {
                            String newName = in.nextString();
                            String newAlias = in.nextString();
                            categories.add(new Category(newName, newAlias));
                        }
                        in.endArray();
                    }
                    in.endArray();
                    break;
                case "rating":
                    rating = in.nextDouble();
                    break;
                case "rating_img_url":
                    ratingImgUrl = Uri.parse(in.nextString());
                    break;
                case "review_count":
                    reviewCount = (int) in.nextDouble();
                    break;
                case "deals":
                    // parse sub-fields of field "deals"
                    in.beginObject();
                    while (in.hasNext()) {
                        String fieldNested = in.nextName();
                        if (fieldNested.equals("deals_url")) {
                            dealsUrl = Uri.parse(in.nextString());
                            hasDeals = true;
                        } else {
                            in.skipValue();
                        }
                    }
                    in.endObject();
                    break;
                case "reservation_url":
                    seatMeUrl = Uri.parse(in.nextString());
                    hasSeatMeUrl = true;
                    break;
                case "eat24_url":
                    eat24Url = Uri.parse(in.nextString());
                    hasEat24Url = true;
                default:
                    in.skipValue();
                    break;
            } // end switch
        } // end while

        /*
        extrapolate a single business URL in the case that one or more are
        available; if there is a standard (desktop) URL and a mobile URL, give preference
        to the mobile one, otherwise set business URL to the standard one
        */
        if (desktopUrl != null) {
            if (mobileUrl != null)
                businessUrl = mobileUrl;
            else
                businessUrl = desktopUrl;
            hasBusinessUrl = true;
        } // else keep default hasBusinessUrl false

        in.endObject();
    }


    /******
     Getters
     *******/

    public String getId() {
        return id;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public String getBusinessName() {
        return name;
    }

    public String getAddressDisplay() {
        return addressDisplay;
    }

    public String getAddressMapable() {
        return addressMapable;
    }

    public String getPhoneDisplay() {
        return phoneDisplay;
    }

    public Uri getPhoneDialable() {
        return phoneDialable;
    }

    public Uri getImageUrl() {
        return imageUrl;
    }

    public boolean hasImageUrl() {
        return hasImageUrl;
    }

    public Uri getBusinessUrl() {
        return businessUrl;
    }

    public boolean hasBusinessUrl() {
        return hasBusinessUrl;
    }

    public double getDistanceFromSearchLocation() {
        return distanceFromSearchLocation;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public double getRating() {
        return rating;
    }

    public Uri getRatingImgUrl() {
        return ratingImgUrl;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public boolean hasDeals() {
        return hasDeals;
    }

    public Uri getDealsUrl() {
        return dealsUrl;
    }

    public Uri getSeatMeUrl() {
        return seatMeUrl;
    }

    public boolean hasSeatMeUrl() {
        return hasSeatMeUrl;
    }

    public Uri getEat24Url() {
        return eat24Url;
    }

    public boolean hasEat24Url() {
        return hasEat24Url;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id='" + id + '\n' +
                ", isClosed=" + isClosed + '\n' +
                ", name='" + name + '\n' +
                ", addressDisplay='" + addressDisplay + '\n' +
                ", addressMapable='" + addressMapable + '\n' +
                ", phoneDisplay='" + phoneDisplay + '\n' +
                ", phoneDialable=" + phoneDialable + '\n' +
                ", imageUrl=" + imageUrl + '\n' +
                ", hasImageUrl=" + hasImageUrl + '\n' +
                ", businessUrl=" + businessUrl + '\n' +
                ", hasBusinessUrl=" + hasBusinessUrl + '\n' +
                ", distanceFromSearchLocation=" + distanceFromSearchLocation + '\n' +
                ", categories=" + categories + '\n' +
                ", rating=" + rating + '\n' +
                ", ratingImgUrl=" + ratingImgUrl + '\n' +
                ", reviewCount=" + reviewCount + '\n' +
                ", hasDeals=" + hasDeals + '\n' +
                ", dealsUrl=" + dealsUrl + '\n' +
                ", seatMeUrl=" + seatMeUrl + '\n' +
                ", hasSeatMeUrl=" + hasSeatMeUrl + '\n' +
                ", eat24Url=" + eat24Url + '\n' +
                ", hasEat24Url=" + hasEat24Url + '\n' +
                ", mobileUrl=" + mobileUrl + '\n' +
                ", desktopUrl=" + desktopUrl + '\n' +
                '}';
    }
} // end class
