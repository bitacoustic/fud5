package com.csc413.team5.fud5;

/**
 * TODO Description
 * <p>
 * Created on 6/24/2015.
 *
 * @author Eric C. Black
 */
public class MapBounds {
    // span of suggested map bounds encompassing restaurant results
    private double spanLatitudeDelta;
    private double spanLongitudeDelta;

    // center position of map bounds
    private double centerLatitude;
    private double centerLongitude;

    public MapBounds(double spanLatitudeDelta, double spanLongitudeDelta,
                     double centerLatitude, double centerLongitude) {
        this.setSpanLatitudeDelta(spanLatitudeDelta);
        this.setSpanLongitudeDelta(spanLongitudeDelta);
        this.setCenterLatitude(centerLatitude);
        this.setCenterLongitude(centerLongitude);
    }

    public double getSpanLatitudeDelta() {
        return spanLatitudeDelta;
    }

    public double getSpanLongitudeDelta() {
        return spanLongitudeDelta;
    }

    public double getCenterLatitude() {
        return centerLatitude;
    }

    public double getCenterLongitude() {
        return centerLongitude;
    }

    public void setSpanLatitudeDelta(double spanLatitudeDelta) {
        this.spanLatitudeDelta = spanLatitudeDelta;
    }

    public void setSpanLongitudeDelta(double spanLongitudeDelta) {
        this.spanLongitudeDelta = spanLongitudeDelta;
    }

    public void setCenterLatitude(double centerLatitude) {
        this.centerLatitude = centerLatitude;
    }

    public void setCenterLongitude(double centerLongitude) {
        this.centerLongitude = centerLongitude;
    }

    public void clear() {
        spanLatitudeDelta = 0.0;
        spanLongitudeDelta = 0.0;
        centerLatitude = 0.0;
        centerLongitude = 0.0;
    }
}
