package com.csc413.team5.restaurantapiwrapper;

import java.util.ArrayList;

/**
 * TODO description
 * <p>
 * Created on 7/3/2015.
 *
 * @author Eric C. Black
 */
public class LMenuItem extends LMenuContent {
    protected String name;
    protected String description;
    protected String price;
    protected ArrayList<LMenuOptionGroup> optionGroups;

    public LMenuItem(String name, String description, String price) {
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

    public LMenuOptionGroup getOptionGroup(int index) {
        if (index < 0 || index >= optionGroups.size())
            return null;
        else
            return optionGroups.get(index);
    }

    @Override
    public String toString() {
        return "LMenuItem{" +
                "\nname='" + name + '\'' +
                ",\ndescription='" + description + '\'' +
                ",\nprice='" + price + '\'' +
                ",\noptionGroups=" + optionGroups +
                "\n}";
    }
}
