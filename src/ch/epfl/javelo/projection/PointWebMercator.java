package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

public record PointWebMercator(double  x, double y) {
    public PointWebMercator {
        Preconditions.checkArgument(x <= 1 && x >= 0);
        Preconditions.checkArgument(y <= 1 && y >= 0);
    }

    public static PointWebMercator of(int zoomLevel, double x, double y) {

    }

    public static PointWebMercator ofPointCh(PointCh pointCh) {

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

    }
}
