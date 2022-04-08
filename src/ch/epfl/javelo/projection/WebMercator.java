package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * Utility class for WebMercator coordinates conversions
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class WebMercator {

    private WebMercator() {
    }

    /**
     * @param lon longitudinal coordinates of point (radian)
     * @return x-coordinates of point (WebMercator system)
     */
    public static double x(double lon) {
        return (1 / (2*Math.PI)) * (lon + Math.PI);
    }

    /**
     * @param lat lattiduinal coordinates of point (radian)
     * @return y-coordinates of point (WebMercator)
     */
    public static  double y(double lat) {
        return (1 / (2*Math.PI)) * (Math.PI - Math2.asinh(Math.tan(lat)));
    }

    /**
     *
     * @param x x-coordinates of point (WebMercator)
     * @return longitudinal coordinates (radian)
     */
    public static double lon(double x) {
        return (2*Math.PI*x) - Math.PI;
    }

    /**
     *
     * @param y y-coordinates of point(WebMercator)
     * @return lattitudinal coordinates (radian)
     */
    public static double lat(double y){
        return Math.atan(Math.sinh(Math.PI - 2*Math.PI*y));
    }
}
