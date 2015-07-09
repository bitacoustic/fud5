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
public class Menu implements Serializable {
    protected String menuName;
    protected String currencySymbol;
    protected ArrayList<MenuSection> sections;

    public Menu(String menuName, String currencySymbol) {
        this.menuName = menuName;
        this.currencySymbol = currencySymbol;
        this.sections = new ArrayList<>();
    }

    public String getMenuName() {
        return menuName;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public MenuSection getSection(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= sections.size())
            throw new IndexOutOfBoundsException("invalid index");
        else
            return sections.get(index);
    }

    /**
     * @return a printable representation of this object.
     */
    @Override
    public String toString() {
        return "Menu {" +
                "\nmenuName='" + menuName + '\'' +
                ",\n currencySymbol='" + currencySymbol + '\'' +
                ",\n sections=" + sections +
                "\n}";
    }
}
