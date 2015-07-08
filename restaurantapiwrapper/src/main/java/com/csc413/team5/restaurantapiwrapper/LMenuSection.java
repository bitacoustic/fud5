package com.csc413.team5.restaurantapiwrapper;

import java.util.ArrayList;

/**
 * TODO description
 * <p>
 * Created on 7/3/2015.
 *
 * @author Eric C. Black
 */
public class LMenuSection {
    protected String sectionName;
    protected ArrayList<LMenuSubSection> subsections;

    public LMenuSection(String sectionName, ArrayList<LMenuSubSection> subsections) {
        this.sectionName = sectionName;
        this.subsections = new ArrayList<>();
    }

    public String getSectionName() {
        return sectionName;
    }

    public LMenuSubSection getSubsection(int index) throws IndexOutOfBoundsException {
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
        return "LMenuSection {" +
                "\nsectionName='" + sectionName + '\'' +
                ",\n subsections=" + subsections +
                "\n}";
    }
}
