package com.csc413.team5.restaurantapiwrapper;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A subsection of a {@link MenuSection}.
 * <p>
 * Created on 7/3/2015.
 *
 * @author Eric C. Black
 */
public class MenuSubSection<MenuContent> extends ArrayList<MenuContent> implements Serializable {
    protected String subSectionName;

    public MenuSubSection(String subSectionName) {
        super();
        this.subSectionName = subSectionName;
    }

    public String getSubSectionName() {
        return subSectionName;
    }

    public MenuContent getContent(int index) {
        return get(index);
    }

    public void addContent(MenuContent mc) {
        add(mc);
    }

    @Override
    public String toString() {
        return "MenuSubSection{" +
                "\nsubSectionName='" + subSectionName + '\'' +
                ",\n contents=" + super.toString() +
                "\n}";
    }
}
