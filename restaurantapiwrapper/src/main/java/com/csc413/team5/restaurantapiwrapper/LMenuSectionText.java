package com.csc413.team5.restaurantapiwrapper;

/**
 * TODO Description
 * <p>
 * Created on 7/3/2015.
 *
 * @author Eric C. Black
 */
public class LMenuSectionText extends LMenuContent {
    protected String text;

    public LMenuSectionText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "LMenuSectionText{" +
                "text='" + text + '\'' +
                '}';
    }
}
