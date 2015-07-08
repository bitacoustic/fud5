package com.csc413.team5.restaurantapiwrapper;

import java.util.ArrayList;

/**
 * TODO Description
 * <p>
 * Created on 7/3/2015.
 *
 * @author Eric C. Black
 */
public class LMenuOptionGroup {
    protected String type;
    protected String text;
    protected ArrayList<LMenuOption> options;

    public LMenuOptionGroup(String type, String text) {
        this.type = type;
        this.text = text;
        this.options = new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public LMenuOption getMenuOption(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= options.size())
            throw new IndexOutOfBoundsException("invalid menu option index");
        else
            return options.get(index);
    }

    @Override
    public String toString() {
        return "LMenuOptionGroup{" +
                "type='" + type + '\'' +
                ", text='" + text + '\'' +
                ", options=" + options +
                '}';
    }
}
