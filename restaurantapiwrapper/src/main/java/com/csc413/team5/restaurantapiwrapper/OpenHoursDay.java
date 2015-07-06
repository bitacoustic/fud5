package com.csc413.team5.restaurantapiwrapper;

/**
 * Model for the open hours of a venue on a particular day.
 * <p>
 * Created on 7/3/2015.
 *
 * @author Eric C. Black
 */
public class OpenHoursDay {
    protected TimeOfDay open;
    protected TimeOfDay close;



    /* Constructors */
    public OpenHoursDay() {
        this.open = null;
        this.close = null;
    }

    public OpenHoursDay(double open, double close) {
        this.open = new TimeOfDay(open);
        this.close = new TimeOfDay(close);
    }



    /* Getters */
    public TimeOfDay getOpen() {
        return open;
    }

    public TimeOfDay getClose() {
        return close;
    }



    /* Setters */
    public void setOpen(TimeOfDay open) {
        this.open = open;
    }

    public void setClose(TimeOfDay close) {
        this.close = close;
    }

    public void setFromLocuRange(String range) {
        String[] hoursArray = range.split(" - ");
        String[] openArray = hoursArray[0].split(":");
        String[] closeArray = hoursArray[1].split(":");
        this.open.time = Double.parseDouble(openArray[0])
                + Double.parseDouble(openArray[1]) / 60;
        this.close.time = Double.parseDouble(closeArray[0])
                + Double.parseDouble(closeArray[1]) / 60;
    }
}
