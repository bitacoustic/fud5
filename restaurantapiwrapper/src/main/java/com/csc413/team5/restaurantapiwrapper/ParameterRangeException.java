package com.csc413.team5.restaurantapiwrapper;

/**
 * Exception thrown for invalid parameters to {@link RestaurantApiClient}.
 * <p>
 * Created on 6/25/2015.
 *
 * @author Eric C. Black
 */
public class ParameterRangeException extends Exception{
    public ParameterRangeException(String message) {
        super(message);
    }

    public ParameterRangeException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
