package au.com.jc.weather.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * This class holds all the tweakable parameters of the model.
 * Created by john on 23/03/16.
 */
public class ModelParameters {
    private static SimpleDateFormat timeFormatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    //when to start our model
    private static final String startTimeString = "2016-01-05T00:45:08Z";
    private Date startTime;

    //size of rectangle to be examined when sampling temp
     private  int samplesize = 3;

    //X COORD OF Greenwich
    private   int gmt_x = 0;

    //size of the air lattice
    private int latticeWidth=150;
    private int latticeHeight=150;


    //change in air temp per hour at midday
    private   double time_temp_delta = 2.0;

    //extra change in air temp at equator
    private  double lat_temp_delta = 0.2;


    //When seeding the lattice, this array is used to "bias" particle motion
    //in one direction. Array indexes are the directions (see Point)
    private double[] directionBias=new double[] {0.0,0.04,0.08,0.04,0.0,0.0};

    //air density at start
    //this is the change that any given mass slot will be populated
    private double density=0.09;

    //steps to run
    private int steps=200;

    //use same RNR for all runs to ensure consistency.
    private Random rand;

    public ModelParameters() {
        super();
        try {
            startTime = timeFormatter.parse(startTimeString);
            rand=new Random(startTime.getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Wrong start date for model",e);
        }
    }

    public int getLatticeHeight() {
        return latticeHeight;
    }

    public void setLatticeHeight(int latticeHeight) {
        this.latticeHeight = latticeHeight;
    }

    public int getLatticeWidth() {
        return latticeWidth;
    }

    public void setLatticeWidth(int latticeWidth) {
        this.latticeWidth = latticeWidth;
    }

    public int getSamplesize() {
        return samplesize;
    }

    public void setSamplesize(int samplesize) {
        this.samplesize = samplesize;
    }

    public int getGmt_x() {
        return gmt_x;
    }

    public void setGmt_x(int gmt_x) {
        this.gmt_x = gmt_x;
    }

    public double getLat_temp_delta() {
        return lat_temp_delta;
    }

    public void setLat_temp_delta(double lat_temp_delta) {
        this.lat_temp_delta = lat_temp_delta;
    }

    public double getTime_temp_delta() {
        return time_temp_delta;
    }

    public void setTime_temp_delta(double time_temp_delta) {
        this.time_temp_delta = time_temp_delta;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public double[] getDirectionBias() {
        return directionBias;
    }

    public void setDirectionBias(double[] directionBias) {
        this.directionBias = directionBias;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public Random getRand() {
        return rand;
    }

    public void setRand(Random rand) {
        this.rand = rand;
    }
}


