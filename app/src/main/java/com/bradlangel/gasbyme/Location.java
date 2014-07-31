
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;

        Location location = (Location) o;

        if (dateTime != location.dateTime) return false;
        if (Double.compare(location.latitude, latitude) != 0) return false;
        if (Double.compare(location.longitude, longitude) != 0) return false;
        if (!id.equals(location.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id.hashCode();
        result = 31 * result + (int) (dateTime ^ (dateTime >>> 32));
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
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
