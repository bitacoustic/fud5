package com.csc413.team5.restaurantapiwrapper;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Representation of the open hours of a venue on a particular day.
 * <p>
 * Extends {@link ArrayList}.
 * <p>
 * Created on 7/3/2015.
 *
 * @author Eric C. Black
 */
public class OpenHoursPerDay<OpenHoursRange> extends ArrayList implements Serializable {

    /**
     * Returns whether there is an OpenHoursRange for this day. Alias for
     * {@link OpenHoursPerDay#isEmpty()}.
     *
     * @return whether there is an OpenHoursRange for this day
     */
    public boolean hasHours() {
        return isEmpty();
    }

    /**
     * @return the string representation of this {@code OpenHoursPerDay}.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");

        for (int i = 0; i < size(); i++) {
            sb.append(get(i));
            if (i < size() - 1)
                sb.append(", ");
        }

        return sb.toString();
    }
}
