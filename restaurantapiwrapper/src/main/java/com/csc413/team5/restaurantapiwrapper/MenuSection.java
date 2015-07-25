package com.csc413.team5.restaurantapiwrapper;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A section of a {@link Menu}.
 * <p>
 * Created on 7/3/2015.
 *
 * @author Eric C. Black
 */
public class MenuSection<MenuSubSection> extends ArrayList<MenuSubSection>
        implements Serializable {
    protected String sectionName;

    public MenuSection() {
        super();
        this.sectionName = "";
    }


    public MenuSection(String sectionName) {
        super();
        this.sectionName = sectionName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public MenuSubSection getSubSection(int index) {
        return get(index);
    }

    public void addSubSection(MenuSubSection mss) {
        add(mss);
    }

    public void removeSubSection(int index) {
        remove(index);
    }


    /**
     * @return a printable representation of this object.
     */
    @Override
    public String toString() {
        return "MenuSection {" +
                "\nsectionName='" + sectionName + '\'' +
                ",\n subsections=" + super.toString() +
                "\n}";
    }
}
