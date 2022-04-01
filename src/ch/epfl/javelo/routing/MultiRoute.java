package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class MultiRoute implements Route{
    private final List<Route> allRoutes;

    public MultiRoute(List<Route> segments){
        Preconditions.checkArgument(!segments.isEmpty());
        allRoutes = List.copyOf(segments);
    }

    /**
     * @param position any position
     * @return index of the segment that contains the given position
     */
    @Override
    public int indexOfSegmentAt(double position) {
        position = Math2.clamp(0, position, length());
        int index = 0;
        double pos = position;

        for (Route route : allRoutes) {
            if (route.length() < pos) {
                index += route.indexOfSegmentAt(pos) + 1;
                pos-=route.length();
            } else {
                index += route.indexOfSegmentAt(pos) + 1;
                break;
            }
        }
        return index-1;
    }

    /**
     * Iterates of all the routes in the Multiroute.
     * @return Total length of the path, in meters.
     */
    @Override
    public double length() {
        double length = 0;
        for (Route route : allRoutes) {
            length += route.length();
        }
        return length;
    }

    /**
     * Iterates of all the routes in the Multiroute.
     * @return All the edges of the path
     */
    @Override
    public List<Edge> edges() {
        List<Edge> allEdges = new ArrayList<>();
        for(Route route : allRoutes){
            allEdges.addAll(route.edges());
        }
        return allEdges;
    }

    /**
     * Iterates of all the routes in the Multiroute.
     * @return All points on the end of the edges, without duplicates
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> allPoints = new ArrayList<>();
        for(Route route : allRoutes){
            for(PointCh point : route.points()){
                if(!(allPoints.contains(point))){
                    allPoints.add(point);
                }
            }
        }
        return allPoints;
    }

    /**
     * Iterates on all the routes. If the position is over the length of a route, then it must be on a route after it,
     * so we substract the length of the route to the position, and look on the next route
     * @param position the position
     * @return the PointCh at the given position
     */
    @Override
    public PointCh pointAt(double position) {
        position = Math2.clamp(0, position, length());
        double pos = 0;

        for (Route route : allRoutes) {
            if (position > pos + route.length()) {
                pos+=route.length();
            } else {
                return route.pointAt(position-pos);
            }
        }
        return null;
    }

    /**
     * Iterates on all the routes. If the position is over the length of a route, then it must be on a route after it,
     * so we substract the length of the route to the position, and look on the next route
     * @param position the position
     * @return the closest node of the path to the position
     */
    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0, position, length());
        double pos = 0;

        for (Route route : allRoutes) {
            if (position > pos + route.length()) {
                pos+=route.length();
            } else {
                return route.nodeClosestTo(position-pos);
            }
        }

        return 0;
    }

    /**
     * Iterates of all the routes in the Multiroute.
     * @param point any point
     * @return RoutePoint closest to the given PointCh point
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint minimum = RoutePoint.NONE;
        double pos = 0;
        for (Route route : allRoutes) {
            PointCh coolPoint = route.pointClosestTo(point).point();
            double edgePosition = pos + route.pointClosestTo(point).position();
            double distance = route.pointClosestTo(point).distanceToReference();
            minimum = minimum.min(coolPoint, edgePosition, distance);
            pos += route.length();
        }
        return minimum;
    }

    /**
     *
     * @param position any position
     * @return height of the position on path, can be NaN if edge has no profile
     */
    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0, position, length());
        double pos = 0;

        for (Route route : allRoutes) {
            if (position > pos + route.length()) {
                pos+=route.length();
            } else {
                return route.elevationAt(position-pos);
            }
        }
        return 0;
    }
}
