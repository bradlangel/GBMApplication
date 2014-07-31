
package com.bradlangel.gasbyme;

import java.util.HashMap;
import java.util.Map;

public class GasStation {

    //Immutable fields
    private final String id;
    private final long dateTime;
    private final String zip;
    private final String imageUrl;
    private final String name;
    private final String longName;
    private final String address;
    private final Location location;
    private final double regular;
    private final double plus;
    private final double premium;
    private final double distance;
    private final double diesel;

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    /*
     * Constructor to ensure that the fields can only be set once
     */
    public GasStation(String id, long dateTime, String zip, String imageUrl, String name,
                      String longName, String address, Location location, double regular,
                      double plus, double premium, double distance, double diesel) {
        this.id = id;
        this.dateTime = dateTime;
        this.zip = zip;
        this.imageUrl = imageUrl;
        this.name = name;
        this.longName = longName;
        this.address = address;
        this.location = location;
        this.regular = regular;
        this.plus = plus;
        this.premium = premium;
        this.distance = distance;
        this.diesel = diesel;
    }
    public String getId() {
        return id;
    }

    public long getDateTime() {
        return dateTime;
    }

    public String getZip() {
        return zip;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getLongName() {
        return longName;
    }

    public String getAddress() {
        return address;
    }

    public Location getLocation() {
        return location;
    }

    public double getRegular() {
        return regular;
    }

    public double getPlus() {
        return plus;
    }

    public double getPremium() {
        return premium;
    }

    public double getDistance() {
        return distance;
    }

    public double getDiesel() {
        return diesel;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
