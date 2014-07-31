
package com.bradlangel.gasbyme;

import java.util.HashMap;
import java.util.Map;

public class GasStation implements Comparable<GasStation> {

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

    /*
    * Equals
    */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GasStation)) return false;

        GasStation that = (GasStation) o;

        if (dateTime != that.dateTime) return false;
        if (Double.compare(that.diesel, diesel) != 0) return false;
        if (Double.compare(that.plus, plus) != 0) return false;
        if (Double.compare(that.premium, premium) != 0) return false;
        if (Double.compare(that.regular, regular) != 0) return false;
        if (!address.equals(that.address)) return false;
        if (!name.equals(that.name)) return false;
        if (!zip.equals(that.zip)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (dateTime ^ (dateTime >>> 32));
        result = 31 * result + name.hashCode();
        result = 31 * result + address.hashCode();
        temp = Double.doubleToLongBits(regular);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(plus);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(premium);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(diesel);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /*
     * Default is 'Regular' gas type
     */
    @Override
    public int compareTo(GasStation other) {
        // compareTo should return < 0 if this is supposed to be
        // less than other, > 0 if this is supposed to be greater than
        // other and 0 if they are supposed to be equal
        if (this.regular >= other.getRegular()) {
            return 1;
        } else {
            return -1;
        }
    }

    /*
     * Getters
     */
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
