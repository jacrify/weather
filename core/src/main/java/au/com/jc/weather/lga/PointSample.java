package au.com.jc.weather.lga;

import java.util.HashSet;
import java.util.Set;

/**
 * Container class for sample of points. Can calculate average temp, density, etc.
 * Created by john on 22/03/16.
 */
public class PointSample  implements Sample{
    //TODO this may change is lattice model changes
    private static final double MAX_MASSES_PER_POINT = 6.0;
    private static final double MAX_PRESSURE = 20000;
    private Set<Point> points=new HashSet<Point>();
    private Set<Mass> masses=new HashSet<Mass>();


    public void addPoint(Point point) {
        points.add(point);
        for (Mass m: point.massesPopulated) {
            if (m!=null)
                masses.add(m);
        }

    }

    public void addAll(Set<Point> points) {
        for(Point p: points) {
            addPoint(p);
        }
    }

    public int getPointCount() {
        return points.size();
    }


    public int getMassCount() {
        return masses.size();
    }

    /**
     * Calculate density of a region. This is defined as the number of masses in a region,
     * divided by the number of masses that could be in the region max.
     * @return density between 0.0 and 1.0
     */
    @Override
    public double getDensity() {
        return masses.size()/(points.size()*MAX_MASSES_PER_POINT);
    }

    @Override
    public double getPressure() {
        return MAX_PRESSURE*getDensity();
    }

    @Override
    public double getAverageTemp() {
        double accum=0;
        for (Mass m : masses) {
            accum+=m.temp;
        }
        return accum/masses.size();
    }
}
