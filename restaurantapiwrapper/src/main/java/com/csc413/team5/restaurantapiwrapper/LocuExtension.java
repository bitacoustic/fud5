package com.csc413.team5.restaurantapiwrapper;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

/**
 * Locu extension for {@link RestaurantApiClient}; WARNING: not yet fully implemented -- currently
 * only obtains open hours, if available.
 * <p>
 * Created on 7/2/2015.
 *
 * @author Eric C. Black
 */
public class LocuExtension {
    private static final String TAG = "LocuExtension";
    private final LocuApiKey key;

    public LocuExtension(LocuApiKey key) {
        this.key = key;
    }

    /**
     * Attempts to match the specified {@link Restaurant} to a venue in Locu's database, extracting
     * the following information if it is available:
     * <ul>
     *     <li>Open hours
     *     <li>Restaurant menu
     * </ul>
     * <p>The Restaurant will be updated in place to reflect the additional information. Returns
     * the unique Locu ID if a match was found, or an empty String ("") if a match was not found,
     * as in {@link #getLocuId(Restaurant)}
     * @param r a Restaurant object
     * @return a String containing the unique Locu ID of the match, or "" if a match was not
     *         found
     */
    public String update(Restaurant r) {
        String id = getLocuId(r);
        if (id.compareTo("") == 0) // match not found
            return id;              // don't update Restaurant

        r.locuId = id;
        updateFromMatchedLocuId(r, id);

        return id;
    }

    /**
     * Attempts to get the Locu ID for the given Restaurant, using the Restaurant's geolocation,
     * name, address, and postal code. If no match was found, returns an empty String ("").
     * @param r a Restaurant object
     * @return a Locu ID if match was found or if it is already stored in the Restaurant,
     *         "" if ID information is otherwise unavailable
     */
    public String getLocuId(Restaurant r) {

        String rName = r.name;
        int indexNameFirstSpace = rName.indexOf(' ');
        if (indexNameFirstSpace > 0)
            rName = rName.substring(0, indexNameFirstSpace);

        String rAddr = r.address.getAddressLine(0).replaceAll(" ", "%20");

        String url = "http://api.locu.com/v1_0/venue/search/?"
                + "location=" + Double.toString(r.addressMapable.getLatitude())
                + "%2C" + Double.toString(r.addressMapable.getLongitude())
                + "&name=" + rName + "&address=" + rAddr
                + "&postal_code=" + r.address.getPostalCode()
                + "&api_key=" + key.getKey();

        OAuthRequest request = new OAuthRequest(Verb.GET, url);

        Log.i(TAG, "Sending Locu request: " + request.getUrl());
        Response response = request.send();
        Log.i(TAG, "Received response: " + response.getMessage());
        Log.i(TAG, "Response: " + response.getBody());

        // if GET response was not OK
        if (response.getMessage().compareTo("OK") != 0)
            return "";

        JSONObject in = null;
        try {
            in = new JSONObject(response.getBody());
            JSONArray inObjects = in.getJSONArray("objects");
            if (inObjects.length() > 0) {
                JSONObject inObjectsMatch = inObjects.getJSONObject(0);
                if (inObjectsMatch.has("id"))
                    return inObjectsMatch.getString("id");
                else
                    return ""; // id not present, return""
            }

        } catch (JSONException e) {
            e.printStackTrace();  // if can't read response, match not possible
            return "";
        }

        return ""; // default: can't get Locu ID for match
    }

    /**
     * Parse information about a Locu venue and add it to the Restaurant. If the ID is not
     * found in the Locu database the Restaurant will not be updated. Any exceptions are
     * handled within the method; as such, if any input or
     * @param r   {@link Restaurant}
     * @param id  a Locu venue ID
     */
    public void updateFromMatchedLocuId(Restaurant r, String id) {
        String url = "http://api.locu.com/v1_0/venue/" + id + "/?api_key=" + key.getKey();
        OAuthRequest request = new OAuthRequest(Verb.GET, url);
        Log.i(TAG, "Sending Locu request: " + request.getUrl());
        Response response = request.send();
        Log.i(TAG, "Received response: " + response.getMessage());
        if (response.getMessage().compareTo("OK") != 0)
            return; // query unsuccessful; don't update Restaurant

        JSONObject in;
        try {
            in = new JSONObject(response.getBody());
            if (in != null) {
                JSONArray inNames = in.names();
                for (int i = 0; i < inNames.length(); i++) {
                    switch (inNames.getString(i)) {
                        case "not_found":
                            JSONArray inNotFound = in.getJSONArray("not_found");
                            if (inNotFound.length() > 0)
                                return; // id not found; don't update Restaurant
                            break;
                        case "objects":
                            JSONArray inObjects = in.getJSONArray("objects");
                            JSONObject inObjectsVenue = inObjects.getJSONObject(0);
                            updateFromMatchedLocuIdHelper(inObjectsVenue, r);
                        default:
                            break;
                    } // end switch
                } // end if
            } // end try
        } catch (JSONException e) {
            e.printStackTrace();
            return; // JSON exception; don't update Restaurant
        }

        // default: JSONObject is null; don't update Restaurant
    } // end updateFromMatchedLocuId()

    /**
     * Helper for updateFromMatchedLocuId()
     * @param in single object in "objects" field of Locu venue detail as JSONObject
     * @param r  a Restaurant
     */
    private void updateFromMatchedLocuIdHelper(JSONObject in, Restaurant r) {
        if (in.has("menus")) {
            JSONArray inMenus = null;
            Menus menus = null;

            try {
                inMenus = in.getJSONArray("menus");
                menus = new Menus();
                for (int i = 0; i < inMenus.length(); i++) {
                    JSONObject inMenusEach = inMenus.getJSONObject(i);
                    Menu newMenu = updateMenuHelper(inMenusEach, menus, r);
                    if (newMenu != null)
                        menus.add(newMenu);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (menus != null)
                r.locuMenus = menus;
        } // end if (in.has("menus"))

        if (in.has("open_hours")) {
            JSONObject inHours = null;
            OpenHours newHours = null;

            try {
                inHours = in.getJSONObject("open_hours");

                // get the open hours for each day
                JSONArray inHoursNames = inHours.names(); // get days of the week

                newHours = new OpenHours();

                for (int i = 0; i < inHoursNames.length(); i++) { // for each day
                    String thisDay = inHoursNames.getString(i);
                    JSONArray inHoursThisDay = inHours.getJSONArray(thisDay); // array of open hours
                                                                          // ranges for this day
                    OpenHoursPerDay openHoursThisDay = new OpenHoursPerDay();

                    // if exists at least one entry for this day, add each entry to the day's
                    // open hours
                    if (inHoursThisDay.length() > 0)
                        for (int j = 0; j < inHoursThisDay.length(); j++)
                            openHoursThisDay.add(new OpenHoursRange(inHoursThisDay.getString(j)));

                    newHours.put(DayOfWeek.fromString(thisDay), openHoursThisDay);
                } // end for (int i...)
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (newHours != null)
                r.hours = newHours;
        } // end if (in.has("open_hours"))

    } // end updateFromMatchedLocuIdHelper()

    /**
     * Helper for updateFromMatchedLocuIdHelper()
     * @param in     single menu in "menus" field of Locu venue detail as JSONObject
     * @return menu  a {@link Menus} objection, that is, a collection of menus for a restaurant
     * @param r      a Restaurant
     */
    private Menu updateMenuHelper(JSONObject in, Menus menus, Restaurant r) {
        Menu newMenu = null;
        try {
            String menuName = "";
            String currencySymbol = "";
            if (in.has("menu_name"))
                menuName = in.getString("menu_name");
            if (in.has("currency_symbol"))
                currencySymbol = in.getString("currency_symbol");
            if (in.has("sections")) {

            }
            newMenu = new Menu(menuName, currencySymbol);

        } catch (JSONException e) {
            newMenu = null; // if there was an error with menu creation, just clear the menu
            e.printStackTrace();
        }

        return newMenu;

    }


}
