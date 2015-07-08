package com.csc413.team5.restaurantapiwrapper;

import java.util.ArrayList;

/**
 * A section of a {@link Menu}.
 * <p>
 * Created on 7/3/2015.
 *
 * @author Eric C. Black
 */
public class MenuSection {
    protected String sectionName;
    protected ArrayList<MenuSubSection> subsections;

    public MenuSection(String sectionName, ArrayList<MenuSubSection> subsections) {
        this.sectionName = sectionName;
        this.subsections = new ArrayList<>();
    }

    public String getSectionName() {
        return sectionName;
    }

    public MenuSubSection getSubsection(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= subsections.size())
            throw new IndexOutOfBoundsException("invalid subsection index");
        else
            return subsections.get(index);
    }

    /**
     * @return a printable representation of this object.
     */
    @Override
    public String toString() {
        return "MenuSection {" +
                "\nsectionName='" + sectionName + '\'' +
                ",\n subsections=" + subsections +
                "\n}";
    }
}
