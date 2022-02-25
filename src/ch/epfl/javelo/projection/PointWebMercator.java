package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

public record PointWebMercator(double  x, double y) {
    public PointWebMercator {
        Preconditions.checkArgument(x <= 1 && x >= 0);
        Preconditions.checkArgument(y <= 1 && y >= 0);
    }

    public static PointWebMercator of(int zoomLevel, double x, double y) {
        return new PointWebMercator(Math.scalb(x, -zoomLevel-8), Math.scalb(y, -zoomLevel-8));
    }

    public static PointWebMercator ofPointCh(PointCh pointCh) {
        return new PointWebMercator(WebMercator.x(pointCh.lon()), WebMercator.y(pointCh.lat()));
    }

    public double xAtZoomLevel(int zoomLevel) {

    }

    public double yAtZoomLevel(int zoomLevel) {

    }

    public double lon() {

    }

    public double lat() {

    }

    public PointCh toPointCh() {
        double e = Ch1903.e(lon(), lat());
        double n = Ch1903.n(lon(), lat());
        return SwissBounds.containsEN(e, n) ? new PointCh(e, n) : null;
    }
}
