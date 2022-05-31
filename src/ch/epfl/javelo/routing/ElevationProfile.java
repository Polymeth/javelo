package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;
import java.util.function.DoubleUnaryOperator;

/**
 * Creates an elevation profile according to entered elevation points
 *
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class ElevationProfile {
    private final double length;
    private final float[] elevationSamples;

    /**
     * Creates an elevation profile according to entered elevation points
     *
     * @param length           length of the profile (in meter)
     * @param elevationSamples the array containing all elevation points
     * @throws IllegalArgumentException if the length is negative or if there is strictly less than 2 samples
     */
    public ElevationProfile(double length, float[] elevationSamples) {
        Preconditions.checkArgument(length > 0 && elevationSamples.length >= 2);
        this.length = length;
        this.elevationSamples = elevationSamples.clone();
    }

    /**
     * @return length of profile (in meters)
     */
    public double length() {
        return length;
    }

    /**
     * @return minimum height of the profile (in meters)
     */
    public double minElevation() {
        DoubleSummaryStatistics stats = new DoubleSummaryStatistics();
        for (float elevationSample : elevationSamples) {
            stats.accept(elevationSample);
        }
        return stats.getMin();
    }

    /**
     * @return maximum height of profile (in meters)
     */
    public double maxElevation() {
        DoubleSummaryStatistics stats = new DoubleSummaryStatistics();
        for (float elevationSample : elevationSamples) {
            stats.accept(elevationSample);
        }
        return stats.getMax();
    }

    /**
     * @return total positive ascent of profile (in meters)
     */
    public double totalAscent() {
        double ascent = 0;
        for (int i = 1; i < elevationSamples.length; i++) {
            if (elevationSamples[i] - elevationSamples[i - 1] > 0) {
                ascent += elevationSamples[i] - elevationSamples[i - 1];
            }
        }
        return ascent;
    }

    /**
     * @return total positive descent of profile (in meters)
     */
    public double totalDescent() {
        double descent = 0;
        for (int i = 1; i < elevationSamples.length; i++) {
            if (elevationSamples[i] - elevationSamples[i - 1] < 0) {
                descent += elevationSamples[i] - elevationSamples[i - 1];
            }
        }
        return Math.abs(descent);
    }

    /**
     * @param position of wanted point height (can be negative or over length of samples)
     * @return height of profile at given position
     */
    public double elevationAt(double position) {
            DoubleUnaryOperator sample = Functions.sampled(elevationSamples, length);
            return sample.applyAsDouble(position);
    }
}
