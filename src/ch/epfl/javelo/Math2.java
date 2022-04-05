package ch.epfl.javelo;

/**
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class Math2 {

    private Math2(){}

    /**
     *
     * @param x integer
     * @param y integer
     * @return the ceiled value of the division of x by y
     */
    public static int ceilDiv(int x, int y){
        Preconditions.checkArgument(x>=0);
        Preconditions.checkArgument(y>0);
        return ((x + y - 1) / y);
    }

    /**
     * @param y0 y-coordinate of 1st point
     * @param y1 y-coordinate of 2nd point
     * @param x x-coordinate of point on the slope
     * @return slope of line at point x
     */
    public static double interpolate(double y0, double y1, double x) {
        return Math.fma(y1 - y0, x, y0);
    }

    /**
     * @param x coordinates of point
     * @return hyperbolix sinus inverse of point x
     */
    public static double asinh(double x){
        double exp = x + Math.sqrt(Math.pow(x, 2) + 1);
        Preconditions.checkArgument(exp > 0);
        return Math.log(exp);
    }

    /**
     * @param min minimum bound for variable v
     * @param v value to be bounded
     * @param max maximum bound for variable v
     * @return value of v between min and max (included)
     */
    public static int clamp(int min, int v, int max){
        Preconditions.checkArgument(min < max);
        if (v < min){
            return min;
        } else if (v > max){
            return max;
        } else return v;
    }

    /**
     * @param min minimum bound for variable v
     * @param v value to be bounded
     * @param max maximum bound for variable v
     * @return value of v between min and max (included)
     */
    public static double clamp(double min, double v, double max){
        Preconditions.checkArgument(min < max);
        if (v < min){
            return min;
        } else if (v > max){
            return max;
        } else return v;
    }

    /**
     * @param uX x coordinates of the u vector
     * @param uY y coordinates of the u vector
     * @param vX x coordinates of the v vector
     * @param vY y coordinates of the v vector
     * @return the dot product of the vectors u and v
     */
    public static double dotProduct(double uX, double uY, double vX, double vY) {
        return uX*vX + uY*vY;
    }

    /**
     * @param uX x coordinates of the u vector
     * @param uY y coordinates of the u vector
     * @return the norm of the vector u
     */
    public static double norm(double uX, double uY) {
        return Math.sqrt(dotProduct(uX, uY, uX, uY));
    }

    /**
     * @param uX x coordinates of the u vector
     * @param uY y coordinates of the u vector
     * @return the squared norm of the vector u
     */
    public static double squaredNorm(double uX, double uY) {
        return Math.pow(norm(uX, uY), 2);
    }

    /**
     * @param aX x coordinates of the A point
     * @param aY y coordinates of the A point
     * @param bX x coordinates of the B point
     * @param bY y coordinates of the B point
     * @param pX x coordinates of the P point
     * @param pY y coordinates of the P point
     * @return the lenght of the orthogonal projection of the vector from A to P on the vector from A to B
     */
    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY){
        double vect = norm((bX - aX), (bY - aY));
        Preconditions.checkArgument(vect != 0);
        return dotProduct(pX - aX, pY - aY, bX - aX, bY - aY)/vect;
    }
}
