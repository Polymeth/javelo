package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


//todo: clamp pour la position
/**
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class SingleRoute implements Route {
    private final ArrayList<Edge> allEdges = new ArrayList<>();
    private final double[] distances;

    public SingleRoute(List<Edge> edges) {
        Preconditions.checkArgument(edges.size() != 0);

        allEdges.addAll(edges);
        this.distances = new double[allEdges.size()+1];
        distances[0] = 0;
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
        points.add(pointAt(0));
        for (int i = 1; i < distances.length; i++) {
            points.add(pointAt(distances[i]));
        }
        return points;
    }

    /**
     * @param position any position
     * @return returns a PointCh at the entered position on the route
     */
    @Override
    public PointCh pointAt(double position) {
        position = correctedPosition(position);

        int edgeId = edgeIndexAtPosition(position);
        return (edgeId < distances.length-1)
                ? allEdges.get(edgeId).pointAt(position - distances[edgeId])
                : allEdges.get(edgeId-1).pointAt(allEdges.get(edgeId-1).length()); //S
    }

    /**
     * @param position any position
     * @return the closest node of the entered position of the route
     */
    @Override
    public int nodeClosestTo(double position) {
        position = correctedPosition(position);

        int index = Arrays.binarySearch(distances, position);

        if (index >= 0) {
            return (index<allEdges.size()) ? allEdges.get(index).fromNodeId() : allEdges.get(index-1).toNodeId();
        } else {
            index = -(index + 2);
            PointCh point = allEdges.get(index).pointAt(position);
            double u = position - distances[index];
            return (u < allEdges.get(index).length()/2) ? allEdges.get(index).fromNodeId() : allEdges.get(index).toNodeId();
        }
    }

    /**
     * @param point any point
     * @return returns the closest point of the entered one on the route
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint minimum = RoutePoint.NONE;
        for (Edge edge : allEdges) {
            double distance = Math2.clamp(0, edge.positionClosestTo(point), edge.length());
            minimum = minimum.min(edge.pointAt(distance), distances[edge.fromNodeId()] + distance, edge.pointAt(distance).distanceTo(point));
        }
        return minimum;
    }

    /**
     * @param position any position
     * @return the elevation at the entered position of the route
     */
    @Override
    public double elevationAt(double position) {
        position = correctedPosition(position);
        int index = Arrays.binarySearch(distances, position);

        if (index < 0) {
            index = -(index)-2;
            return (allEdges.get(index).elevationAt(position - distances[index]));
        } else if (index >= allEdges.size()) {
            return (allEdges.get(index-1).elevationAt(position - distances[index-1]));
        } else {
            return (allEdges.get(index).elevationAt(position - distances[index]));
        }
    }

    /**
     * @param position any position
     * @return the corrected index if needed of the array binary index search
     */
    private int edgeIndexAtPosition(double position) {
        return (Arrays.binarySearch(distances, position) < 0) ? -(Arrays.binarySearch(distances, position) + 2) : Arrays.binarySearch(distances, position);
    }

    /**
     * @param position any position
     * @return the corrected position if it's out of bound, or itself otherwise
     */
    private double correctedPosition(double position) {
        if (position < 0) {
            return 0d;
        } else if (position > length()) {
            return length();
        }
        return position;
    }
}
