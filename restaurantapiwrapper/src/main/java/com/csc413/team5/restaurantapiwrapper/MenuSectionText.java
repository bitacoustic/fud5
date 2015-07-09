package com.csc413.team5.restaurantapiwrapper;

import java.io.Serializable;

/**
 * Text which may appear in sequence with {@link Menu} items.
 * <p>
 * Created on 7/3/2015.
 *
 * @author Eric C. Black
 */
public class MenuSectionText extends MenuContent implements Serializable {
    protected String text;

    public MenuSectionText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "MenuSectionText{" +
                "text='" + text + '\'' +
                '}';
    }
}
