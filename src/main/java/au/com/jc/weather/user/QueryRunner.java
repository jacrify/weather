package au.com.jc.weather.user;

import au.com.jc.weather.model.ElevationData;
import au.com.jc.weather.model.ModelParameters;
import au.com.jc.weather.model.Util;
import au.com.jc.weather.model.World;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Accept input from user on command line, and output temperature at that location and time.
 * Note: elevation is accepted but ignored (it is built into the world, and all stations are
 * assumed to be a ground level)
 * Note: input temperature is accepted but ignore (it's in the spec but it isn't clear how to use it)
 * This class is designed to be run from command line, and could be used in a script. However
 * it is fairly inefficient to run multiple times as it steps the world forward each time.
 * Created by john on 23/03/16.
 */
public class QueryRunner {

    private static NumberFormat latFormatter=new DecimalFormat("#0.00");
    private static NumberFormat elevationFormatter=new DecimalFormat("##0");
    private static NumberFormat longFormatter=new DecimalFormat("#####0");
    private static NumberFormat tempFormatter=new DecimalFormat("+###0.00;-###0.00");

    private static SimpleDateFormat timeFormatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private Date targetTime;
    private double lat;
    private double longi;

    public static void main(String[] args) {

        QueryRunner runner = new QueryRunner();
        String out;
        try {
            runner.parseArgs(args);
            System.out.println(runner.run());
        }
        catch (IllegalArgumentException e) {
             printUsage();
            System.out.println("\n\n");
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    protected String run() throws IllegalArgumentException {
        ModelParameters p=new ModelParameters();
        World world=new World(p);
        ElevationData e=new ElevationData();
        world.setElevationData(e);

        if (world.getCurrentTime().getTime() >= targetTime.getTime()) {
            throw new IllegalArgumentException("Passed time "+timeFormatter.format(targetTime)+ " must be greater than simulation start time "+timeFormatter.format(world.getCurrentTime()));
        }
        double lowerBoundTemp=0;
        double upperBoundTemp=-1;
        Date lowerBoundTime=null;
        Date upperBoundTime=null;
        double outTemp;
        boolean firstSampleGrabbed=false;
        boolean secondSampleGrabbed=false;


        for (int i = 0; i < p.getSteps(); i++) {
            world.step();


            if (world.getCurrentTime().getTime() > targetTime.getTime()) {
                upperBoundTemp = world.getSample(lat, longi).getAverageTemp();
                upperBoundTime=world.getCurrentTime();
                break;
            }

            lowerBoundTemp = world.getSample(lat, longi).getAverageTemp();
            lowerBoundTime=world.getCurrentTime();
        }




        if (upperBoundTemp==-1) {
            Date endTime=world.getCurrentTime();
                throw new IllegalArgumentException("Passed time "+timeFormatter.format(targetTime)+ " must be less than simulation end time "+timeFormatter.format(endTime));

            }

        long lowerBoundTimeInMillis=lowerBoundTime.getTime();
        long upperBoundTimeInMillis=upperBoundTime.getTime();
        long targetTimeInMillis=targetTime.getTime();

        double interpolatedTemp = Util.interpolate(lowerBoundTemp, upperBoundTemp, lowerBoundTimeInMillis, upperBoundTimeInMillis, targetTimeInMillis);


        return tempFormatter.format(interpolatedTemp);
    }

    private void parseArgs(String[] args) throws IllegalArgumentException {
        if (args.length!=5) {

            throw new IllegalArgumentException("Wrong number of arguments passed");
        }

        lat=Double.parseDouble(args[0]);
        longi=Double.parseDouble(args[1]);

        //we ignore this
        double elevation= Integer.parseInt(args[2]);


        targetTime=null;
        try {
            targetTime=timeFormatter.parse(args[3]);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Time must be formartted as yyyy-MM-ddTHH:mm:ssZ where T and Z are literals");
        }

        //we ignore this
        double inTemp= Double.parseDouble(args[4]);


    }

    private static void printUsage() {
        System.out.println("Expected args, space separated,  are:");
        System.out.println("Latitude : eg -29.89");
        System.out.println("Longitude : eg -131.66");
        System.out.println("ElevationData : eg 180");
        System.out.println("Time : eg 2016-01-05T00:45:08Z");
        System.out.println("Predicted Temp : eg 28.5");

    }
}
