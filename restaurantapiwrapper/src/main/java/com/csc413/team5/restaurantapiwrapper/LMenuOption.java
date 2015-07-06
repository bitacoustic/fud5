package com.csc413.team5.restaurantapiwrapper;

/**
 * TODO Description
 * <p>
 * Created on 7/3/2015.
 *
 * @author Eric C. Black
 */
public class LMenuOption {
    protected String name;
    protected String price;

    public LMenuOption(String name, String price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "LMenuOption{" +
                "name='" + name + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
