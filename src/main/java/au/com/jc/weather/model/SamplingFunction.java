package au.com.jc.weather.model;

import au.com.jc.weather.lga.Sample;

/**
 * Created by john on 25/03/16.
 */
public interface SamplingFunction {
    public double extractFromSample(Sample s);
}
