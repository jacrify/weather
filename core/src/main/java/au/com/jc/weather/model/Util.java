package au.com.jc.weather.model;

/**
 * Created by john on 21/03/16.
 */
public class Util {
    //

    /**
     * Generate a triangle wave of amp with period, solve for x, offset by x and y offsets
     * @param x value along x axis
     * @param period Period of triangle wave
     * @param amplitude max/min height of wave
     * @param xoffset fixed x offset of 0 height rising
     * @param yoffset fixed offset to height
     * @return
     */
    protected static double triangle(double x,double period, double amplitude,double xoffset,double yoffset) {
        //TODO Can this be cheaper?
        x=x+xoffset;
        return (amplitude * 2 * Math.abs(Math.round(x/period)-(x/period)))+yoffset;

    }

    /**
     * Given a value actualSourceValue on a range defined with a lower and upper bound
     * (lowerSourceRange, upperSouceRange), and another range defined with a lower and upper bound
     * lowerTargetRange and upperBoundValue, return the value within the second range that
     * is the same percentage through the range as actualSourceValue is through the first range.
     *
     * @param lowerTargetRange
     * @param upperTargetRange
     * @param lowerSourceRange
     * @param upperSouceRange
     * @param actualSourceValue
     * @return
     */
    public static double interpolate(double lowerTargetRange, double upperTargetRange, double lowerSourceRange, double upperSouceRange, double actualSourceValue) {
        double ratio=(actualSourceValue - lowerSourceRange)/(upperSouceRange - lowerSourceRange);
        return ((upperTargetRange - lowerTargetRange)*ratio)+ lowerTargetRange;
    }
}
