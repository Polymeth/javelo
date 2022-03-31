package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

/**
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class ElevationProfile {
    private final double length;
    private final float[] elevationSamples;

    public ElevationProfile(double length, float[] elevationSamples){
        Preconditions.checkArgument(length > 0 && elevationSamples.length >= 2);
        this.length = length;
        this.elevationSamples = elevationSamples;
    }

    /**
     * @return length of profile (in meters)
     */
    public double length(){
        return length;
    }

    /**
     * @return minimum height of the profile (in meters)
     */
    public double minElevation(){
        DoubleSummaryStatistics stats = new DoubleSummaryStatistics();
        for(int i = 0; i< elevationSamples.length; i++){
            stats.accept(elevationSamples[i]);
        }
        return stats.getMin();
    }

    /**
     * @return maximum height of profile (in meters)
     */
    public double maxElevation(){
        DoubleSummaryStatistics stats = new DoubleSummaryStatistics();
        for(int i = 0; i< elevationSamples.length; i++){
            stats.accept(elevationSamples[i]);
        }
        return stats.getMax();
    }

    /**
     * @return total positive ascent of profile (in meters)
     */
    public double totalAscent(){
        double ascent = 0;
        for(int i = 1; i < elevationSamples.length; i++){
            if(elevationSamples[i] - elevationSamples[i-1] > 0){
                ascent += elevationSamples[i] - elevationSamples[i-1];
            }
        }
        return ascent;
    }

    /**
     * @return total positive descent of profile (in meters)
     */
    public double totalDescent(){
        double descent = 0;
        for(int i = 1; i < elevationSamples.length; i++){
            if(elevationSamples[i] - elevationSamples[i-1] < 0){
                descent += elevationSamples[i] - elevationSamples[i-1];
            }
        }
        return Math.abs(descent);
    }

    /**
     * @param position of wanted point height (can be negative or over length of samples)
     * @return height of profile at given position
     */
    public double elevationAt(double position){
        if(position < 0){
            return elevationSamples[0];
        } else if(position >= length) {
            return elevationSamples[elevationSamples.length -1];
        } else {
            DoubleUnaryOperator sample = Functions.sampled(elevationSamples, length);
            return sample.applyAsDouble(position);
        }
    }
}
