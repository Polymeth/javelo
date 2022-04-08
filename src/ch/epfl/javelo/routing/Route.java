package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.List;

/**
 * Interface to create a type of route
 *
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public interface Route {

    /**
     * @param position any position
     * @return index of the segment that contains the given position
     */
    int indexOfSegmentAt(double position);

    /**
     * Iterates of all the routes in the Multiroute.
     *
     * @return Total length of the path, in meters.
     */
    double length();

    /**
     * Iterates of all the routes in the Multiroute.
     *
     * @return All the edges of the path
     */
    List<Edge> edges();

    /**
     * Iterates of all the routes in the Multiroute.
     *
     * @return All points on the end of the edges, without duplicates
     */
    List<PointCh> points();

    /**
     * Iterates on all the routes. If the position is over the length of a route, then it must be on a route after it,
     * so we substract the length of the route to the position, and look on the next route
     *
     * @param position the position
     * @return the PointCh at the given position
     */
    PointCh pointAt(double position);

    /**
     * Iterates on all the routes. If the position is over the length of a route, then it must be on a route after it,
     * so we substract the length of the route to the position, and look on the next route
     *
     * @param position the position
     * @return the closest node of the path to the position
     */
    int nodeClosestTo(double position);

    /**
     * Iterates of all the routes in the Multiroute.
     *
     * @param point any point
     * @return RoutePoint closest to the given PointCh point
     */
    RoutePoint pointClosestTo(PointCh point);

    /**
     * @param position any position
     * @return height of the position on path, can be NaN if edge has no profile
     */
    double elevationAt(double position);
}
