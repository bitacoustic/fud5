package com.csc413.team5.restaurantapiwrapper;

import java.util.ArrayList;

public class LMenu {
    protected String menuName;
    protected String currencySymbol;
    protected ArrayList<LMenuSection> sections;

    public LMenu(String menuName, String currencySymbol) {
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

    public LMenuSection getSection(int index) throws IndexOutOfBoundsException {
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
        return "LMenu {" +
                "\nmenuName='" + menuName + '\'' +
                ",\n currencySymbol='" + currencySymbol + '\'' +
                ",\n sections=" + sections +
                "\n}";
    }
}
