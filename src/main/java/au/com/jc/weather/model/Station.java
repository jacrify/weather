package au.com.jc.weather.model;

import au.com.jc.weather.lga.Sample;

/**
 * Simple holder class to hold station data.
 * Created by john on 21/03/16.
 */
public class Station {
    private  String name;
    private  double lat;
    private  double longi;
    private double elevation;

    public Station(String name, double lat, double longi, World world) {
        super();
        this.name=name;
        this.lat=lat;
        this.longi=longi;
        elevation=world.getElevation(lat,longi);


    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLongi() {
        return longi;
    }



    public Sample generateSample(World world) {
        return world.getSample(lat, longi);
    }

    public double getElevation() {
        return elevation;
    }
}
