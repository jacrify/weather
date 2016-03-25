package au.com.jc.weather.model;

import au.com.jc.weather.lga.*;

import java.util.*;

/**
 * Model of the world.
 * Holds an LGA (see lga package) to model air.
 * Key responsiblilities of this class are:
 * -Seeding the LGA
 * -Applying temperature changes due to earth rotation/latitude
 * -Providing transformations between three different coordinate systems:
 *   a) latitude longitude (currently using an equirectangular projection
 *   b) an x y cartesian grid of the same size as the underlying LGA
 *   c) the LGA coordinate system, where (0,0) is at the same point as the grid (0,0)
 *      but (0,maxy) is at grid (0+y/2,y) due to the skew of the LGA triangular grid.
 * Created by john on 21/03/16.
 */
public class World {

    private final int width;
    private final int height;
    private final ModelParameters parameters;
    private int hourOfDayAtGMT=0;

    public Elevation getElevation() {
        return elevation;
    }

    public void setElevation(Elevation elevation) {
        this.elevation = elevation;
    }

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

                int gridx= (int) convertLatticexToGridx(x,y);
                double delta = calculateTemperatureDelta(gridx, y, hourOfDayAtGMT);

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
     * PointSample conditions at one point
     * @param lat
     * @param longi
     * @return
     */
    public Sample getSample(double lat, double longi) {
        double gridy=convertLatitudeToGridY(lat);
        double gridx=convertLongitudeToGridX(longi);

        double[][] neighbours = getNearestNeighourLatticeCoordsAndDists(gridx, gridy);

        List<PointSample> samples=new ArrayList<PointSample>();
        WeightedSample sample=new WeightedSample();
        for (int i = 0; i < 3; i++) {
            int latticex=(int)neighbours[i][0];
            int latticey=(int)neighbours[i][1];
            if ((latticey>=0) && (latticey<height)) {
                PointSample s = lattice.sample(latticex, latticey, parameters.getSamplesize());
                //weight closer samples higher
                sample.addSample(s, 1.0 - neighbours[i][2]);
            }
        }
        return sample;
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
        return Util.interpolate(0,height,90,-90,lat);
    }


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
        double x= (gridx - (gridy/2.0)) ;
        if (x<0)
            x=width+x;

        if (x>=width)
            return 0;
        return x;
    }


    /**
     * Work out the number of degrees increase/decrease due to sunlight or lack or it, per hour.
     * We need a band at 45N and 45S where net is 0,otherwise system will gain or lose energy
     * @param x a grid x coord
     * @param y a grid y coord
     * @param gmtHour
     * @return
     */
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
     * @return local hour of day
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
        int sourceX = (int) Util.interpolate(0, elevation.getElevationWidth(),-180,180, longi+180) %elevation.getElevationWidth();

        int elev= elevation.getElevation(sourceX,sourceY);
//        System.out.println("X:"+sourceX+" Y:"+sourceY + "elev:"+elev);
        return elev;
        }

    /**
     * Debug method for generate images.
     * @return
     */
    public double[][] generateTemperatureMap() {
        double[][] grid =  new double[360][180];
        for (int longi = -180; longi < 180; longi++) {
            for (int lat = 90; lat > -90; lat--) {


                grid[longi+180][180-(lat+90)]=getSample(lat,longi).getAverageTemp();
////                System.out.println("longi:"+longi+"Lat:"+lat);
//                double gridx=convertLongitudeToGridX(longi);
//                double gridy=convertLatitudeToGridY(lat);
////                System.out.println("Gridx:"+gridx+" Grid y:"+gridy);
//                int latticeX= (int) convertGridxToLatticex(gridx,gridy);
////                System.out.println("LatticeX:"+latticeX);
//                int latticeY= (int) gridy;
//                double t=lattice.sample(latticeX, latticeY, 2).getAverageTemp();
//
//                grid[longi+180][lat+90]=t;

            }
        }
        return grid;
    }


    /**
     * Given a grid point, return the lattice coords of the three nearest lattice points, and distances
     * to those points
     * @param gridx
     * @param gridy
     * @return an array of three arrays, each of which hold x y lattice coords of three
     * nearest point and distance to those points [3][3]
     */
    public double[][] getNearestNeighourLatticeCoordsAndDists(double gridx,double gridy)
    {
        double[][] out=new double[3][3];
        // 1) Find grid points of nearest 4 neighbours on the lattice-
        // p0 :above back
        // p1: above forwards
        // p2: down back
        // p3: down forwards

        //arrays to hold grid coords of above points
        double px[]=new double[4];
        double py[]=new double[4];

        //array to hold lattice x coord of above points
        double plx[] = new double[4];

        py[0]=Math.round(gridy);
        plx[0]=Math.round(convertGridxToLatticex(gridx, gridy))%width;
        px[0]=convertLatticexToGridx(plx[0], py[0]);

        py[1]=py[0];
        plx[1]=Math.ceil(convertGridxToLatticex(gridx, gridy))%width; //wrap arounde-w;
        px[1]=convertLatticexToGridx(plx[1],py[1]);

        py[2]=Math.ceil(gridy);
        plx[2]=Math.round(convertGridxToLatticex(gridx, gridy))%width;
        px[2]=convertLatticexToGridx(plx[2],py[2]);


        py[3]=py[2];
        plx[3]=Math.ceil(convertGridxToLatticex(gridx, gridy))%width; //wrap around e-w
        px[3]=convertLatticexToGridx(plx[3],py[3]);




        //find largest distance index- we only want nearest three as the matrix is triangular
        double maxDist=Double.MIN_VALUE;
        int indexOfMax=0;
        double[] dists=new double[4];


        //find the longest distance and remember which one it is
        for (int i = 0; i < 4; i++) {
//            System.out.println(px[i] + " "+ py[i]);
            dists[i]=Util.distance(gridx,gridy,px[i],py[i]);
//            System.out.println(dists[i]);
            if (dists[i]>maxDist) {
                maxDist=dists[i];
                indexOfMax=i;
            }
        }

//        return the other three points
        int targetIndex=0;
        //build output array
        for (int i = 0; i < 4; i++) {
            if (i!=indexOfMax) {
                out[targetIndex][0]=plx[i];
                out[targetIndex][1]=py[i];
                out[targetIndex][2]=dists[i];
                targetIndex++;
            }
        }

        return out;
    }
}
