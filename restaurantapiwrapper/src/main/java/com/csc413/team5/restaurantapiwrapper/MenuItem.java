package com.csc413.team5.restaurantapiwrapper;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * An item on a {@link Menu}, which is listed under a {@link MenuSubSection}.
 * <p>
 * Created on 7/3/2015.
 *
 * @author Eric C. Black
 */
public class MenuItem extends MenuContent implements Serializable {
    protected String name;
    protected String description;
    protected String price;
    protected ArrayList<MenuOptionGroup> optionGroups;

    public MenuItem(String name, String description, String price) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.optionGroups = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public MenuOptionGroup getOptionGroup(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= optionGroups.size())
            throw new IndexOutOfBoundsException();
        else
            return optionGroups.get(index);
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "\nname='" + name + '\'' +
                ",\ndescription='" + description + '\'' +
                ",\nprice='" + price + '\'' +
                ",\noptionGroups=" + optionGroups +
                "\n}";
    }
}
