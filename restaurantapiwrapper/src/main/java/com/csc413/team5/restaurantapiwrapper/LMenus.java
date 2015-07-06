package com.csc413.team5.restaurantapiwrapper;

import java.util.ArrayList;

/**
 * TODO description
 * <p>
 * Created on 7/3/2015.
 *
 * @author Eric C. Black
 */
public class LMenus {
    protected ArrayList<LMenu> menus;

    public LMenus() {
        this.menus = new ArrayList<>();
    }

    public LMenu getMenu(int index) {
        if (index < 0 || index >= menus.size())
            return null;
        else
            return menus.get(index);
    }

    /**
     * @return a printable representation of this object.
     */
    @Override
    public String toString() {
        return "LMenus {\n" + menus + "\n}";
    }
}

