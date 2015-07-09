package com.csc413.team5.restaurantapiwrapper;

import android.location.Location;

import java.io.Serializable;

/**
 * Suggested map boundaries for mapping results of an API query. Span refers to the suggested
 * length and width of the map centered on {@link #center}.
 * <p>
 * Created on 6/24/2015.
 *
 * @author Eric C. Black
 */
public class MapBounds implements Serializable {
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

    /**
     * Returns the height of the suggested map.
     * @return the height of the suggested map
     */
    public double getSpanLatitudeDelta() {
        return spanLatitudeDelta;
    }

    /**
     * Returns the width of the suggested map.
     * @return the width of the suggested map
     */
    public double getSpanLongitudeDelta() {
        return spanLongitudeDelta;
    }

    /**
     * Returns the y coordinate of the center of the suggested map.
     * @return the y coordinate of the center of the suggested map
     */
    public double getCenterLatitude() {
        return centerLatitude;
    }

    /**
     * Returns the x coordinate of the center of the suggested map.
     * @return the x coordinate of the center of the suggested map
     */
    public double getCenterLongitude() {
        return centerLongitude;
    }

    /**
     * @return a {@link Location} object with latitude and longitude of map center
     */
    public Location getCenter() {
        return center;
    }

    /**
     * Sets all data values in this MapBounds to 0.0
     */
    public void clear() {
        spanLatitudeDelta = 0.0;
        spanLongitudeDelta = 0.0;
        centerLatitude = 0.0;
        centerLongitude = 0.0;
    }

    /**
     * @return String representation of MapBounds object.
     */
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
