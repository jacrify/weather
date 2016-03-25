package au.com.jc.weather.lga;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds one or more samples with assigned weights (eg distance from a point)
 * and calculates weighted values based on the weights.
 * Created by john on 25/03/16.
 */
public class WeightedSample implements Sample {
    private double weightTotal=0;
    private Map<Sample,Double> samples=new HashMap<Sample, Double>();


    public void addSample(Sample s,double weight) {
        samples.put(s, weight);
        weightTotal+=weight;
    }



    @Override
    public double getDensity() {
        double o=0;
        for(Map.Entry<Sample, Double> entry: samples.entrySet()) {
            o+=(entry.getValue().doubleValue()/weightTotal) * entry.getKey().getDensity();
        }

        return o;
    }

    @Override
    public double getPressure() {
        double o=0;
        for(Map.Entry<Sample, Double> entry: samples.entrySet()) {
            o+=(entry.getValue().doubleValue()/weightTotal) * entry.getKey().getPressure();
        }

        return o;
    }

    @Override
    public double getAverageTemp() {
        double o=0;
        for(Map.Entry<Sample, Double> entry: samples.entrySet()) {
           o+=(entry.getValue().doubleValue()/weightTotal) * entry.getKey().getAverageTemp();
        }

        return o;
    }
}
