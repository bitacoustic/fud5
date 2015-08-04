package com.example.healthinspectionwrapper;

/**
 * Contains health inspection data for a restaurant.
 * Use HealthRecordClient to make one.
 */
public class HealthRecord {
    protected String business_id;
    protected String name;
    protected String address;
    protected boolean hasCoordinates;
    protected double latitude;
    protected double longitude;
    protected boolean hasScore;
    protected double score;

    public String getBusiness_id() { return business_id; }

    public String getName() { return name; }

    public String getAddress() { return address; }

    public boolean isHasCoordinates() { return hasCoordinates; }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }

    public boolean hasScore(){ return hasScore; }

    public double getScore() { return score; }
}
