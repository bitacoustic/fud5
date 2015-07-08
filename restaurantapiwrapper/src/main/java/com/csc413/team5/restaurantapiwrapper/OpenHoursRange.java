package com.csc413.team5.restaurantapiwrapper;

/**
 * Representation of a range of open hours, e.g. "5:00PM - 9:00PM"
 * <p>
 * Created on 7/7/2015.
 *
 * @author Eric C. Black
 */
public class OpenHoursRange {
    protected TimeOfDay open;
    protected TimeOfDay close;



    /* Constructors */
    public OpenHoursRange() {
        this.open = null;
        this.close = null;
    }

    public OpenHoursRange(double open, double close) {
        this.open = new TimeOfDay(open);
        this.close = new TimeOfDay(close);
    }

    public OpenHoursRange(String range) {
        setFromLocuRange(range);
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
        String[] hoursArray = range.trim().split(" - ");
        String[] openArray = hoursArray[0].split(":");
        String[] closeArray = hoursArray[1].split(":");
        this.open = new TimeOfDay(Double.parseDouble(openArray[0])
                + (Double.parseDouble(openArray[1]) / 60));
        this.close = new TimeOfDay(Double.parseDouble(closeArray[0])
                + (Double.parseDouble(closeArray[1]) / 60));
    }


    @Override
    public String toString() {
        return "OpenHoursRange{" +
                "open=" + open.to12HrString() +
                ", close=" + close.to12HrString() +
                '}';
    }
}
