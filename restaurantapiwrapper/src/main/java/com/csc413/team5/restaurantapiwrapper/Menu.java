package com.csc413.team5.restaurantapiwrapper;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * An individual menu of a restaurant, e.g. the lunch menu, possibly comprised of multiple
 * {@link MenuSection}s.
 * <p>
 * Created on 7/3/2015.
 *
 * @author Eric C. Black
 */
public class Menu<MenuSection> extends ArrayList<MenuSection> implements Serializable {
    protected String menuName;
    protected String currencySymbol;

    public Menu(String menuName, String currencySymbol) {
        super();
        this.menuName = menuName;
        this.currencySymbol = currencySymbol;
    }

    public String getMenuName() {
        return menuName;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public MenuSection getSection(int index) {
        return get(index);
    }

    public void addMenuSection(MenuSection m) {
        add(m);
    }

    /**
     * @return a printable representation of this object.
     */
    @Override
    public String toString() {
        return "Menu {" +
                "\nmenuName='" + menuName + '\'' +
                ",\n currencySymbol='" + currencySymbol + '\'' +
                ",\n sections=" + super.toString() +
                "\n}";
    }
}
