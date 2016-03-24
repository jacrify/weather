package au.com.jc.weather.model;

import au.com.jc.weather.lga.Lattice;
import au.com.jc.weather.lga.Mass;
import au.com.jc.weather.lga.Point;
import au.com.jc.weather.lga.Sample;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by john on 21/03/16.
 */
public class World {

    private final int width;
    private final int height;
    private final ModelParameters parameters;
    private int hourOfDayAtGMT=0;

    private Elevation elevation;

    private Lattice lattice;

    private int stepCount=0;

    public World( ModelParameters parameters) {
        super();
        this.width=parameters.getLatticeWidth();
        this.height=parameters.getLatticeHeight();
        this.parameters=parameters;

        int seed=(int)System.currentTimeMillis();


        lattice=new Lattice(width,height);
        seedLattice(parameters.getDensity());

        elevation=new Elevation();
    }

    public Lattice getLattice() {
        return lattice;
    }

    /**
     * Populate the lattice with air masses.
     * Bias equator masses towards easterly movement (it looks cool)
     * @param density
     */
    private void seedLattice(double density) {
        //density bias for initial directions

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Point p=new Point(x,y);
                p.setRand(parameters.getRand());
                for (int k = 0; k < 6; k++) {

                    double equatorialAdjustment= Util.triangle(y, height, 2, 0, -0.5);

                    double adjustedDensity=density+(equatorialAdjustment*parameters.getDirectionBias()[k]);

                    if (parameters.getRand().nextDouble() <= (adjustedDensity)) {
                        Mass m=new Mass();
                        p.addMass(m,k);

                    }
                }
                lattice.setPoint(p);
            }
        }
    }

    /**
     * Step the world forward one hour.Step the lattice,
     * adjust temperatures, add one to hour.
     */
    public void step() {
        lattice.step();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double delta = calculateTemperatureDelta(x, y, hourOfDayAtGMT);

                lattice.adjustTemp(x,y,delta);
            }

        }
        //one step is one hour
        hourOfDayAtGMT=(hourOfDayAtGMT+1) % 24;
        stepCount++;

    }

    /**
     * Returns the time in the world right now
     * @return
     */
    public Date getCurrentTime() {
        Calendar cal= GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.add(Calendar.HOUR, stepCount);
        return cal.getTime();
    }

    /**
     * Sample conditions at one point
     * @param lat
     * @param longi
     * @return
     */
    public Sample getSample(double lat, double longi) {
        double gridy=convertLatitudeToGridY(lat);
        double gridx=convertLongitudeToGridX(longi);

        int latticeX= (int) convertGridxToLatticex(gridx,gridy);
        int latticeY=(int)gridy;

        //TODO interpolate
        Sample s=lattice.sample(latticeX, latticeY, parameters.getSamplesize());

        return s;
    }

    /**
     * Convert a lat long to a floating point lattice coords.
     * Lattice lines are offset (skewed) to this is tricky.
     * @param lat
     * @param longi
     * @return array[2] of doubles, {x,y}
     */
    private double[] convertLatLongToLatticeCoords(double lat, double longi) {
        double gridX=convertLongitudeToGridX(longi);
        double gridY=convertLatitudeToGridY(lat);

       return null;
    }

    protected double convertLatitudeToGridY(double lat) {
        return Util.interpolate(0,height,-90,90,lat);
    }

    //TODO fix this:(0,height) lattice coord should not map to (0,maxLongitude) but (2x,maxLongitude)
    //Be careful though- there isn't only used by lattice projection, but by elevation also.
    protected double convertLongitudeToGridX(double longi) {
        return Util.interpolate(0, width, -180, 180, longi + 180) % width;
    }

    /**
     * Convert lattice coords (sked) into rectangular grid coords
     * @param latticex
     * @param latticey
     * @return x grid coord
     */
    protected double convertLatticexToGridx(double latticex, double latticey) {
        return (latticex+(latticey/2.0)) %width;
    }

    protected double  convertGridxToLatticex (double gridx,double gridy) {
        return gridx - (gridy/2.0) ;
    }

    //Work out the number of degrees increase/decrease due to sunlight or lack or it,
    //per hour
    //We need a band at 45N and 45S where net is 0,
    //otherwise system will gain or lose energy
    public double calculateTemperatureDelta(int x, int y, int gmtHour) {
        double localHour= calculateLocalHour(x, gmtHour);
        double timeOfDayModifier= calculateTimeOfDayTempModifier(localHour);

        //Now work out a adjustment to factor in distance from equator
        //Closer to the equator is warmer
        double latModifier= calculateLatitudeTempModifier(y);
        //Apply multiplier
        double tempIncreaseDecrease= parameters.getLat_temp_delta() *latModifier+ parameters.getTime_temp_delta()*timeOfDayModifier;

        return tempIncreaseDecrease;

    }

    //return value between -1 and 1 showing hour of day affecting temp
    // 12 noon is 1, midnight is -1

    protected double calculateTimeOfDayTempModifier(double localHour) {

        //assume:
        // midday is hottest
        // 6am sunrise
        // 6am sunset

        //TODO add axial tilt/day length
        return Util.triangle(localHour, 24.0, 2.0, 0.0, -1.0);
    }

    /**
     * Given x is an index into a location, gmthour is time at gm.
     * calculate local hour here.
     * @param x
     * @param gmtHour
     * @return
     */
    protected double calculateLocalHour(int x, int gmtHour) {
        if (x== parameters.getGmt_x())
            return gmtHour;
        //First work out local time of day
        double hourWidth=width/24.0;
        double distanceToGreenwich=x- parameters.getGmt_x();

        double hoursFromGreenwich=distanceToGreenwich/hourWidth;
        if (Double.isNaN(hoursFromGreenwich))
            return gmtHour;
        return (gmtHour+hoursFromGreenwich) % 24;
    }

    // Returns -1 at poles (cold), 0 at halfway to poles (stable)
    // 1 at equator (hot)
    protected double calculateLatitudeTempModifier(int y) {
        return Util.triangle(y, height, 2.0, 0, -1.0);
//        return 1-(distanceToEquator/equatorY);
    }

    public int getElevation(double lat, double longi) {
        int sourceY = (int) Util.interpolate( 0, elevation.getElevationHeight(),90, -90,lat);

        //Greenwich is a x 0 on our map, so shift longitude back 180
        int sourceX = (int) Util.interpolate(0, elevation.getElevationWidth(),-180,180, longi-180);
        return elevation.getElevation(sourceX,sourceY);

        }

}
