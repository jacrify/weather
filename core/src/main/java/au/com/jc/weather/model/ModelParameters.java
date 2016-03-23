package au.com.jc.weather.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class holds all the tweakable parameters of the model.
 * Created by john on 23/03/16.
 */
public class ModelParameters {
    //when to start our model
    private static final String startTimeString = "2016-01-05T00:45:08Z";
    private static SimpleDateFormat timeFormatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    //size of rectangle to be examined when sampling temp
     private  int samplesize = 4;
    //X COORD OF Greenwich
    private   int gmt_x = 0;
    private int latticeWidth=150;
    //base change in temp per hour, in degrees
    private  double lat_temp_delta = 0.2;
    private int latticeHeight=150;
    private   double time_temp_delta = 2.0;
    private Date startTime;

    //When seeding the lattice, this array is used to "bias" particle motion
    //in one direction. Array indexes are the directions (see Point)
    private double[] directionBias=new double[] {0.0,0.04,0.08,0.04,0.0,0.0};
    //air density at start
    private double density=0.02;
    //steps to run
    private int steps=400;

    public ModelParameters() {
        super();
        try {
            startTime = timeFormatter.parse(startTimeString);
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
}


