package com.csc413.team5.restaurantapiwrapper;

import java.util.ArrayList;

/**
 * A subsection of a {@link MenuSection}.
 * <p>
 * Created on 7/3/2015.
 *
 * @author Eric C. Black
 */
public class MenuSubSection {
    protected String subSectionName;
    protected ArrayList<MenuContent> contents;

    public MenuSubSection(String subSectionName) {
        this.subSectionName = subSectionName;
        this.contents = new ArrayList<>();
    }

    public String getSubSectionName() {
        return subSectionName;
    }

    public MenuContent getContent(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= contents.size())
            throw new IndexOutOfBoundsException("invalid content index");
        else
            return contents.get(index);
    }

    @Override
    public String toString() {
        return "MenuSubSection{" +
                "\nsubSectionName='" + subSectionName + '\'' +
                ",\n contents=" + contents +
                "\n}";
    }
}
