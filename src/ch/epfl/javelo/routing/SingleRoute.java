package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A route maybe of multiple edges
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class SingleRoute implements Route {
    private final List<Edge> allEdges;
    private final double[] distances;

    /**
     * Creates a route using a list of edges
     * @param edges any list of edges to compose to route
     * @throws IllegalArgumentException if the edges list is empty
     */
    public SingleRoute(List<Edge> edges) {
        Preconditions.checkArgument(!edges.isEmpty());

        allEdges = List.copyOf(edges);
        this.distances = new double[allEdges.size()+1];
        for (int i = 1; i < distances.length; i++) {
            distances[i] = distances[i - 1] + (allEdges.get(i-1).length());
        }
    }

    /**
     * @param position any position
     * @return the index of the segment
     */
    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    /**
     * @return the total length of the route (in meters)
     */
    @Override
    public double length() {
        double length = 0;
        for (Edge edge : allEdges) {
            length += edge.length();
        }
        return length;
    }

    /**
     * @return a new instance of list with all the route's edges
     */
    @Override
    public List<Edge> edges() {
        return List.copyOf(allEdges);
    }

    /**
     * @return a new instance of list with all the points (all the nodes)
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> points = new ArrayList<>();
        for(double distance : distances) {
            points.add(pointAt(distance));
        }
        return points;
    }

    /**
     * @param position any position
     * @return returns a PointCh at the entered position on the route
     */
    @Override
    public PointCh pointAt(double position) {
        position = Math2.clamp(0, position, length());
        int edgeId = edgeIndexAtPosition(position);
        return (edgeId < distances.length-1)
                ? allEdges.get(edgeId).pointAt(position - distances[edgeId])
                : allEdges.get(edgeId-1).pointAt(allEdges.get(edgeId-1).length());
    }

    /**
     * @param position any position
     * @return the closest node of the entered position of the route
     */
    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0, position, length());
        int index = Arrays.binarySearch(distances, position);

        if (index >= 0) {
            return (index<allEdges.size()) ? allEdges.get(index).fromNodeId() : allEdges.get(index-1).toNodeId();
        } else {
            index = -(index + 2);
            double u = position - distances[index];
            return (u <= allEdges.get(index).length()/2)
                    ? allEdges.get(index).fromNodeId()
                    : allEdges.get(index).toNodeId();
        }
    }

    /**
     * @param point any point
     * @return returns the closest point of the entered one on the route
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint minimum = RoutePoint.NONE;
        double pos = 0;
        for (Edge edge : allEdges) {

            double edgePosition = Math2.clamp(0, edge.positionClosestTo(point), edge.length());
            PointCh newPoint = pointAt(edgePosition + pos);
            double distance = newPoint.distanceTo(point);
            minimum = minimum.min(newPoint, edgePosition+pos, distance);
            pos += edge.length();

        }
        return minimum;
    }

    /**
     * @param position any position
     * @return the elevation at the entered position of the route
     */
    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0, position, length());
        int index = Arrays.binarySearch(distances, position);

        if (index >= allEdges.size()){
            return (allEdges.get(index-1).elevationAt(position - distances[index-1]));
        } else {
            if (index < 0) index = -(index)-2;
            return (allEdges.get(index).elevationAt(position - distances[index]));
        }
    }

    /**
     * @param position any position
     * @return the corrected index if needed of the array binary index search
     */
    private int edgeIndexAtPosition(double position) {
        return (Arrays.binarySearch(distances, position) < 0)
                ? -(Arrays.binarySearch(distances, position) + 2)
                : Arrays.binarySearch(distances, position);
    }
}
