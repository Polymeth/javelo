package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

// lat -> x, y machin
public final class WebMercator {
    private WebMercator() {}


    /**
     *
     * @param lon longitudinal coordinates of point (degrees)
     * @return x-coordinates of point (WebMercator system)
     */
    public static double x(double lon) {
        lon = Math.toRadians(lon);
        return (1/(2*Math.PI))*(lon+Math.PI);
    }

    /**
     *
     * @param lat lattiduinal coordinates of point (degrees)
     * @return y-coordinates of point (WebMercator)
     */
    public static  double y(double lat) {
        lat = Math.toRadians(lat);
        return (1/(2 * Math.PI))*(Math.PI-Math2.asinh(Math.tan(lat)));
    }

    /**
     *
     * @param x x-coordinates of point (WebMercator)
     * @return longitudinal coordinates (degrees)
     */
    public static double lon(double x) {
        return Math.toDegrees((2*Math.PI*x) - Math.PI);
    }


    /**
     *
     * @param y y-coordinates of point(WebMercator)
     * @return lattitudinal coordinates (degrees)
     */
    public static double lat(double y){
        return Math.toDegrees(Math.atan(Math.sinh(Math.PI - 2*Math.PI*y)));
    }
}
