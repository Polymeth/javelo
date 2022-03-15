package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.List;

/**
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public interface Route {
    int indexOfSegmentAt(double position);

    double length();

    List<Edge> edges();

    List<PointCh> points();

    PointCh pointAt(double position);

    int nodeClosestTo(double position);

    RoutePoint pointClosestTo(PointCh point);

    double elevationAt(double position);
}
