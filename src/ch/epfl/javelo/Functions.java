package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

/**
 * Utility class creating math functions
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class Functions {

    private Functions() {}

    /**
     * @param y a real number
     * @return returns a constant function that is always y
     */
    public static DoubleUnaryOperator constant(double y) {
        return (operand) -> y;
    }

    /**
     * @param samples equally spaced height points
     * @param xMax the maximum distance (the distance of the last height point)
     * @return returns the interpolated function based on the equally spaced given heigh points
     * @throws IllegalArgumentException if there is strictly less than 2 samples or if the xMax value isnt
     *                                  strictly positive
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        Preconditions.checkArgument(samples.length >= 2);
        Preconditions.checkArgument(xMax > 0);
        return new Sampled(samples.clone(), xMax);
    }

    private record Sampled(float[] samples, double xMax) implements DoubleUnaryOperator {

        /**
         * @param operand the x coordinate where you want to see the interpolated value
         * @return the interpolated value at x
         */
        @Override
        public double applyAsDouble(double operand) {
            if (operand <= 0) {
                return samples[0];
            } else if (operand >= xMax) {
                return samples[samples.length - 1];
            } else {
                double interval = xMax/(samples.length-1);
                int closestLowestIntervalX = (int)(operand/interval);
                return Math2.interpolate(samples[(closestLowestIntervalX)], samples[closestLowestIntervalX+1], (operand-closestLowestIntervalX*interval)/interval);
            }
        }
    }
}