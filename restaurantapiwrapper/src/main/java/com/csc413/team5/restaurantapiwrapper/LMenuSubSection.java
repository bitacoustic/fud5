package com.csc413.team5.restaurantapiwrapper;

import java.util.ArrayList;

/**
 * TODO description
 * <p>
 * Created on 7/3/2015.
 *
 * @author Eric C. Black
 */
public class LMenuSubSection {
    protected String subSectionName;
    protected ArrayList<LMenuContent> contents;

    public LMenuSubSection(String subSectionName) {
        this.subSectionName = subSectionName;
        this.contents = new ArrayList<>();
    }

    public String getSubSectionName() {
        return subSectionName;
    }

    public LMenuContent getContent(int index) {
        if (index < 0 || index >= contents.size())
            return null;
        else
            return contents.get(index);
    }

    @Override
    public String toString() {
        return "LMenuSubSection{" +
                "\nsubSectionName='" + subSectionName + '\'' +
                ",\n contents=" + contents +
                "\n}";
    }
}
