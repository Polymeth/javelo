package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

/**
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class Functions {
    private Functions() {
    }

    /**
     * @param y a real number
     * @return returns a constant function that is always y
     */
    public static DoubleUnaryOperator constant(double y) {
        return new Constant(y);
    }

    /**
     * @param samples equally spaced height points
     * @param xMax the maximum distance (the distance of the last height point)
     * @return returns the interpolated function based on the equally spaced given heigh points
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        Preconditions.checkArgument(samples.length >= 2);
        Preconditions.checkArgument(xMax > 0);
        return new Sampled(samples, xMax);
    }

    private record Constant(double y) implements DoubleUnaryOperator {
        @Override
        public double applyAsDouble(double operand) {
            return y;
        }
    }

    private record Sampled(float[] samples, double xMax) implements DoubleUnaryOperator {
        @Override
        public double applyAsDouble(double operand) {
            if (operand > xMax) {
                return samples[samples.length - 1];
            } else if (operand < 0) {
                return samples[0];
            } else {
                double interval = xMax/(samples.length-1);
                double closestLowestIntervalX = Math.floor(operand/interval);
                return Math2.interpolate(samples[(int)(closestLowestIntervalX)], samples[(int)(closestLowestIntervalX+1)], (operand-closestLowestIntervalX*interval)/interval);
            }
        }
    }
}