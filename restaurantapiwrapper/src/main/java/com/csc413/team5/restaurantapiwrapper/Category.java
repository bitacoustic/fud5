package com.csc413.team5.restaurantapiwrapper;

/**
 * TODO Description
 * <p>
 * Created on 6/24/2015.
 *
 * @author Eric C. Black
 */
public class Category {
    private String name;
    private String alias;

    public Category(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }
}
