package com.csc413.team5.restaurantapiwrapper;

import java.util.ArrayList;

/**
 * Model for the open hours of a venue on a particular day.
 * <p>
 * Created on 7/3/2015.
 *
 * @author Eric C. Black
 */
public class OpenHoursDay {
    protected ArrayList<OpenHoursRange> openHoursDay;

    public OpenHoursDay() {
        this.openHoursDay = null;
    }

    public boolean hasHours() {
        return (openHoursDay != null);
    }

    public ArrayList<OpenHoursRange> getAllHours() {
        return openHoursDay;
    }

    public OpenHoursRange getHours(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= openHoursDay.size()) {
            throw new IndexOutOfBoundsException("invalid index");
        }
        else {
            return openHoursDay.get(index);
        }
    }
}
