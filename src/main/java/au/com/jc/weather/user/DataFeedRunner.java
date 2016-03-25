package au.com.jc.weather.user;

import au.com.jc.weather.lga.Sample;
import au.com.jc.weather.model.ElevationData;
import au.com.jc.weather.model.ModelParameters;
import au.com.jc.weather.model.Station;
import au.com.jc.weather.model.World;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class spins up the model, creates a bunch of stations to report on,
 * runs the model forward in time reporting on the stations at each point,
 * and prints the conditions at each station.
 */
public class DataFeedRunner {
    private static final char SEPARATOR = '|';
    private static final char COMMA = ',';
    private static SimpleDateFormat timeFormatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private static NumberFormat latFormatter=new DecimalFormat("#0.00");
    private static NumberFormat elevationFormatter=new DecimalFormat("##0");
    private static NumberFormat longFormatter=new DecimalFormat("##0.00");
    private static NumberFormat pressureFormatter=new DecimalFormat("###0.0");
    private static NumberFormat tempFormatter=new DecimalFormat("+###0.00;-###0.00");
    private static NumberFormat humidityFormatter=new DecimalFormat("#0");

    private ArrayList<Station> stations = new ArrayList<Station>();


    public static void main(String[] args) {

        DataFeedRunner datafeed = new DataFeedRunner();

        List<String> lines = datafeed.run();
        for(String line:lines) {
            System.out.println(line);
        }
    }

    /**
     * Spin up a world and run it forward through time.
     * Report on stations at each step.
     * @return a List of formatted output lines
     */
    private List<String> run() {
        List<String> out=new ArrayList<String>();

        ModelParameters p=new ModelParameters();
        World world=new World(p);
        ElevationData e=new ElevationData();
        world.setElevationData(e);
        populateStations(world);



        for (int i = 0; i < p.getSteps(); i++) {
            world.step();
            for (Station station : stations) {
                Sample sample=station.generateSample(world);

                String output =buildOutputString(station,sample,world.getCurrentTime());
                out.add(output);
            }
        }
        return out;
    }

    /**
     * Build a hard coded list of stations to report on.
     * @param world
     */
    private void populateStations(World world) {

        stations.add(new Station("ADL", -34.9524, 138.5204,world));
        stations.add(new Station("ASP", -23.7951, 133.889,world));
        stations.add(new Station("BWU", -33.9181, 150.9864,world));
        stations.add(new Station("BME", -17.9475, 122.2353,world));
        stations.add(new Station("DRW", -12.4239, 130.8925,world));
        stations.add(new Station("DBO", -32.2206, 148.5753,world));
        stations.add(new Station("HTI", -20.3658, 148.9536,world));
        stations.add(new Station("HBA", -42.8339, 147.5033,world));
        stations.add(new Station("KGI", -30.7847, 121.4533,world));
        stations.add(new Station("MEL", -37.6655, 144.8321,world));
        stations.add(new Station("OAG", -33.3768, 149.1263,world));
        stations.add(new Station("MCY", -26.6006, 153.0903,world));
        stations.add(new Station("SYD", -33.9465, 151.1731,world));

    }

    private String buildOutputString(Station s,Sample sample,Date time) {
        StringBuilder sb=new StringBuilder();
        sb.append(s.getName());
        sb.append(SEPARATOR);
        sb.append(latFormatter.format(s.getLat()));
        sb.append(COMMA);
        sb.append(longFormatter.format(s.getLongi()));
        sb.append(COMMA);
        sb.append(elevationFormatter.format(s.getElevation()));
        sb.append(SEPARATOR);
        sb.append(timeFormatter.format(time));
        sb.append(SEPARATOR);
        //TODO :)
        sb.append("Sunny");
        sb.append(SEPARATOR);
        sb.append(tempFormatter.format(sample.getAverageTemp()));
        sb.append(SEPARATOR);
        sb.append(pressureFormatter.format(sample.getPressure()));
        sb.append(SEPARATOR);
        //TODO :)
        sb.append("50");

        return sb.toString();

        }


}


