package com.csc413.team5.restaurantapiwrapper;

/**
 * Enumerated day of the week: MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
 * <p>
 * Created on 6/24/2015.
 * @author Eric C. Black
 */
public enum DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

    /**
     * Return a String representation of the supplied DayOfWeek.
     * @param d an enumerated DayOfWeek, e.g. DayOfWeek.WEDNESDAY
     * @return a String, e.g. "Wednesday", or "" if supplied DayOfWeek is invalid
     */
    public static String toString(DayOfWeek d) {
        switch (d) {
            case MONDAY:
                return "Monday";
            case TUESDAY:
                return "Tuesday";
            case WEDNESDAY:
                return "Wednesday";
            case THURSDAY:
                return "Thursday";
            case FRIDAY:
                return "Friday";
            case SATURDAY:
                return "Saturday";
            case SUNDAY:
                return "Sunday";
            default:
                return "";
        }
    }

    /**
     * Return a day of the week based on an input String. For example, input "Monday" will return
     * DayOfWeek.MONDAY . Common abbreviations such as "Tues", "W", or "Su" are allowed.
     * @param s a String representing the day of the weeek
     * @return an enumerated day of the week, or null if the String input does not match a
     *         common abbreviation for a day of the week
     */
    public static DayOfWeek fromString(String s) {
        s = s.trim().toLowerCase();

        switch (s) {
            case "monday":
            case "mon":
            case "m":
                return DayOfWeek.MONDAY;
            case "tuesday":
            case "tues":
            case "tue":
            case "t":
                return DayOfWeek.TUESDAY;
            case "wednesday":
            case "wed":
            case "w":
                return DayOfWeek.WEDNESDAY;
            case "thursday":
            case "thu":
            case "th":
                return DayOfWeek.THURSDAY;
            case "friday":
            case "fri":
            case "f":
                return DayOfWeek.FRIDAY;
            case "saturday":
            case "sat":
            case "sa":
            case "s":
                return DayOfWeek.SATURDAY;
            case "sunday":
            case "sun":
            case "su":
                return DayOfWeek.SUNDAY;
            default:
                return null;
        }
    }
}
