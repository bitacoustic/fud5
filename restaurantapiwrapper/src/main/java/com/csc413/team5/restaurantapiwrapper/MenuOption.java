package com.csc413.team5.restaurantapiwrapper;

import java.io.Serializable;

/**
 * An individual option which falls under a {@link MenuOptionGroup}.
 * <p>
 * Created on 7/3/2015.
 *
 * @author Eric C. Black
 */
public class MenuOption implements Serializable {
    protected String name;
    protected String price;

    public MenuOption(String name, String price) {
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
        return "MenuOption{" +
                "name='" + name + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
