package com.csc413.team5.restaurantapiwrapper;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A category of {@link MenuOption}s for a given {@link MenuItem}.
 * <p>
 * Created on 7/3/2015.
 *
 * @author Eric C. Black
 */
public class MenuOptionGroup implements Serializable {
    protected String type;
    protected String text;
    protected ArrayList<MenuOption> options;

    public MenuOptionGroup(String type, String text) {
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

    public MenuOption getMenuOption(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= options.size())
            throw new IndexOutOfBoundsException("invalid menu option index");
        else
            return options.get(index);
    }

    @Override
    public String toString() {
        return "MenuOptionGroup{" +
                "type='" + type + '\'' +
                ", text='" + text + '\'' +
                ", options=" + options +
                '}';
    }
}
