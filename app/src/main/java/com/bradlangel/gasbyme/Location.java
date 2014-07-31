
package com.bradlangel.gasbyme;

import java.util.HashMap;
import java.util.Map;


public class Location {


    //Immutable fields
    private final String id;
    private final long dateTime;
    private final double latitude;
    private final double longitude;

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Location(String id, long dateTime, double latitude, double longitude) {
        this.id = id;
        this.dateTime = dateTime;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public long getDateTime() {
        return dateTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
