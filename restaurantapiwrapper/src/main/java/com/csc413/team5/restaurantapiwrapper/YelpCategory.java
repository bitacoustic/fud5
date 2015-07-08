package com.csc413.team5.restaurantapiwrapper;

/**
 * A Yelp category consisting of display name and alias used for the category filter in an
 * API request.
 * <p>
 * Created on 6/24/2015.
 *
 * @author Eric C. Black
 */
public class YelpCategory {
    private String name;
    private String alias;

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

    @Override
    public String toString() {
        return "YelpCategory{" +
                "name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                '}';
    }
}
