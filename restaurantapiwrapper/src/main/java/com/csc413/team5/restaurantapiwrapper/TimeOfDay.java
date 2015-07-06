package com.csc413.team5.restaurantapiwrapper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Representation of a time of day, that is, a time in a single 24-hour period beginning and
 * ending at 12:00AM.
 * <p>
 * Created on 7/3/2015.
 *
 * @author Eric C. Black
 */
public class TimeOfDay {
    protected double time;
    protected String string12;
    protected String string24;

    public TimeOfDay(double time) {
        this.time = time;
    }

    public TimeOfDay() {
        time = -1.0;
    }



    /* Setters */

    /**
     * Set time by either a 12-hour or 24-hour String. Input examples include "1:35 PM" and
     * "13:35"
     * @param time a String representing a 12-hour (with AM/PM) or 24-hour time of day
     */
    public void setTime(String time) {
        if (time.toUpperCase().contains("M"))
                setTimeBy12String(time);
            else
                setTimeBy24String(time);
    }

    public void setTime(double time) {
        this.time = time;
        if (time >= 0) {
            string12 = timeDoubleTo12(time);
            string24 = timeDoubleto24(time);
        } else {
            string12 = string24 = "?";
        }
    }

    public void setTime(Calendar time) {
        setTimeBy24String(time.HOUR_OF_DAY + ":" + time.MINUTE);
    }

    public void setTime(Date time) {
        setTimeBy24String(time.getHours() + ":" + time.getMinutes());
    }

    public void setTimeBy12String(String time) {
        this.time = time12ToDouble(time);
        string12 = timeDoubleTo12(this.time);
        string24 = timeDoubleto24(this.time);
    }

    public void setTimeBy24String(String time) {
        this.time = time24ToDouble(time);
        string12 = timeDoubleTo12(this.time);
        string24 = timeDoubleto24(this.time);
    }

    public void setTime(SimpleDateFormat time) {
        setTime(time.getCalendar());
    }

    public void setTime(GregorianCalendar time) {
        setTimeBy24String(time.HOUR_OF_DAY + ":" + time.MINUTE);
    }



    /* Getters */
    public String to12HrString() {
        return string12;
    }

    public String to24HrString() {
        return string24;
    }

    public double toDouble() {
        return time;
    }



    /* Static methods */
    /**
     * Convert a double representation of a time of day to a 24-hour String, e.g. 13.75 becomes
     * "13:45".
     * @param time double representation of a time of day
     * @return String representation of the double input
     */
    public static String timeDoubleto24(double time) {
        if (time < 0)
            return "?";
        if (time >= 24.0)
            time = time % 24;
        double fractional = time % 1;
        int hour = (int) (time - fractional);
        int minute = (int) (fractional * 60);
        return hour + ":" + minute;
    }

    /**
     * Convert a double representation of a time of day to a 12-hour String, e.g. 13.75 becomes
     * "1:45 PM".
     * @param time double representation of a time of day
     * @return String representation of the double input
     */
    public static String timeDoubleTo12(double time) {
        return time24To12(timeDoubleto24(time));
    }

    /**
     * Convert a String representation of a 24-hour time of day to a double, e.g. "13:45"
     * becomes 13.75
     * @param time String representation of 24-hour time of day
     * @return String representation of the double input
     */
    public static double time24ToDouble(String time) {
        String[] t = time.trim().split(":");
        return Double.parseDouble(t[0]) + Double.parseDouble(t[1]) / 60;
    }

    /**
     * Convert a 24-hour time of day to 12-hour format with AM and PM. This works for input in
     * format 24-hour HH:MM[:SS ...].
     * @param time of day in 24 hour format
     * @return time of day in 12 hour format
     */
    public static String time24To12(String time) {
        time = time.trim(); // remove whitespaces
        int hourEnds = time.indexOf(":"); // first index of colon marks end of hour
        int hour = Math.abs(Integer.parseInt(time.substring(0, hourEnds)));
        String m = " AM";
        if (hour > 11) {
            m = " PM";
            if (hour > 12)
                hour -= 12;
        }
        if (hour == 0)
            hour = 12;

        return Integer.toString(hour) + time.substring(hourEnds) + m;
    }

    /**
     * Parses a 12-hour String representation of a time and returns a double, where input format
     * is [H]H:MM[:SS...][ ][AM|PM], e.g. "1:35PM", "01:35:49 PM"
     * @param time  a time of day as a 12-hour String
     * @return
     */
    public static double time12ToDouble(String time) {
        time = time.trim().toUpperCase();
        int m = 0;
        if (time.contains("PM"))
            m = 1;
        String[] s = time.split(":");
        double hour = Math.abs(Double.parseDouble(s[0]));
        double minute = Math.abs(Double.parseDouble(s[1])) / 60;
        if (m == 1)
            hour += 12;
        return hour + minute;
    }
}
