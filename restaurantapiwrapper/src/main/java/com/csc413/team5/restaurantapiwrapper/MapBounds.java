package com.csc413.team5.restaurantapiwrapper;

import android.location.Location;

/**
 * TODO Description
 * <p>
 * Created on 6/24/2015.
 *
 * @author Eric C. Black
 */
public class MapBounds {
    // span of suggested map bounds encompassing restaurant results
    protected double spanLatitudeDelta;
    protected double spanLongitudeDelta;

    // center position of map bounds
    protected double centerLatitude;
    protected double centerLongitude;
    // representation of center as Location
    Location center;

    public MapBounds(double spanLatitudeDelta, double spanLongitudeDelta,
                     double centerLatitude, double centerLongitude) {
        this.spanLatitudeDelta = spanLatitudeDelta;
        this.spanLongitudeDelta = spanLongitudeDelta;
        this.centerLatitude = centerLatitude;
        this.centerLongitude = centerLongitude;
        center = new Location("Yelp");
        center.setLatitude(centerLatitude);
        center.setLongitude(centerLongitude);
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

    /**
     * @return a {@link Location} object with latitude and longitude of map center
     */
    public Location getCenter() {
        return center;
    }

    public void clear() {
        spanLatitudeDelta = 0.0;
        spanLongitudeDelta = 0.0;
        centerLatitude = 0.0;
        centerLongitude = 0.0;
    }

    @Override
    public String toString() {
        return "MapBounds{" +
                "spanLatitudeDelta=" + spanLatitudeDelta +
                ", spanLongitudeDelta=" + spanLongitudeDelta +
                ", centerLatitude=" + centerLatitude +
                ", centerLongitude=" + centerLongitude +
                ", center=" + center +
                '}';
    }
}
