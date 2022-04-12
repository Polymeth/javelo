package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

import java.awt.*;

/**
 * Creates a point in WebMercator coordinates
 *
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 * @param x x coordinate of the point
 * @param y y coordinate of the point
 */
public record PointWebMercator(double x, double y) {

    /**
     * Creates a point in WebMercator coordinates
     *
     * @param x any x coordinate
     * @param y any y coordinate
     * @throws IllegalArgumentException if not x and y are not between 1 and 0 (included)
     */
    public PointWebMercator {
        Preconditions.checkArgument(x <= 1 && x >= 0);
        Preconditions.checkArgument(y <= 1 && y >= 0);
    }

    /**
     * @param zoomLevel level of zoom wanted (integer)
     * @param x         x-coordinates (integer, in WebMercator system)
     * @param y         y-coordinates (integer, in WebMercator system)
     * @return WebMercator point with parametered values
     */
    public static PointWebMercator of(int zoomLevel, double x, double y) {
        return new PointWebMercator(Math.scalb(x, -zoomLevel - 8), Math.scalb(y, -zoomLevel - 8));
    }

    /**
     * Converts a pointCh to WebMercator
     *
     * @param pointCh point in Swiss Bounds system
     * @return point in WebMercator system
     */
    public static PointWebMercator ofPointCh(PointCh pointCh) {
        return new PointWebMercator(WebMercator.x(pointCh.lon()), WebMercator.y(pointCh.lat()));
    }

    /**
     * @param zoomLevel level of wanted zoom (integer)
     * @return x-coordinates zoomed in
     */
    public double xAtZoomLevel(int zoomLevel) {
        return Math.scalb(x, 8 + zoomLevel);
    }

    /**
     * @param zoomLevel zoomLevel level of wanted zoom (integer)
     * @return y-coordinates zoomed in
     */
    public double yAtZoomLevel(int zoomLevel) {
        return Math.scalb(y, 8 + zoomLevel);
    }

    /**
     * @return Longitude of point (in radians)
     */
    public double lon() {
        return WebMercator.lon(x);
    }

    /**
     * @return Lattitude of point (in radians)
     */
    public double lat() {
        return WebMercator.lat(y);
    }

    /**
     * Convert local point (this) to Swiss System
     *
     * @return Point in Swiss System if point is in SwissBounds limits, null otherwise
     */
    public PointCh toPointCh() {
        double e = Ch1903.e(lon(), (lat()));
        double n = Ch1903.n(lon(), (lat()));
        return SwissBounds.containsEN(e, n) ? new PointCh(e, n) : null;
    }
}
