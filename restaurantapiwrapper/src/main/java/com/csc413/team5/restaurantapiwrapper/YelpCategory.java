package com.csc413.team5.restaurantapiwrapper;

import java.io.Serializable;

/**
 * A Yelp category consisting of display name and alias used for the category filter in an
 * API request.
 * <p>
 * Created on 6/24/2015.
 *
 * @author Eric C. Black
 */
public class YelpCategory implements Serializable {
    private String name;
    private String alias;

    /**
     * Constructor.
     * @param name  display name of the category
     * @param alias category used in the Yelp category_filter parameter
     */
    public YelpCategory(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    /**
     * @return A String representation of a YelpCategory object.
     */
    @Override
    public String toString() {
        return "YelpCategory{" +
                "name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                '}';
    }
}
