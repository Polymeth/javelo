package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class SingleRoute implements Route {
    private final ArrayList<Edge> allEdges = new ArrayList<>();
    private final double[] distances;

    public SingleRoute(List<Edge> edges) {
        Preconditions.checkArgument(edges.size() != 0);
        for (int i = 0; i < edges.size(); i++) {
            allEdges.add(edges.get(i));
        }

        this.distances = new double[allEdges.size()+1];
        distances[0] = 0;

        for (int i = 1; i < distances.length; i++) {
            distances[i] = distances[i - 1] + (allEdges.get(i-1).length());
        }
    }

    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    @Override
    public double length() {
        double length = 0;
        for (Edge edge : allEdges) {
            length += edge.length();
        }
        return length;
    }

    @Override
    public List<Edge> edges() {
        return List.copyOf(allEdges);
    }

    @Override
    public List<PointCh> points() {
        List<PointCh> points = new ArrayList<>();
        points.add(pointAt(0));
        for (int i = 1; i < distances.length; i++) {
            points.add(pointAt(distances[i]));
        }
        return points;
    }

    @Override
    public PointCh pointAt(double position) {
        int edgeId = edgeIndexAtPosition(position);
        return (edgeId < distances.length-1)
                ? allEdges.get(edgeId).pointAt(position - distances[edgeId])
                : allEdges.get(edgeId-1).pointAt(allEdges.get(edgeId-1).length()); // kinda sus
    }

    @Override
    public int nodeClosestTo(double position) {
        position = correctedPosition(position);
        int index = Arrays.binarySearch(distances, position);

        if (index >= 0) {
            return (index<allEdges.size()) ? allEdges.get(index).fromNodeId() : allEdges.get(index-1).toNodeId();
        } else {
            index = -(index + 2);
            double normFirstNode = Math2.norm(position - allEdges.get(index).fromPoint().e(), position - allEdges.get(index).fromPoint().n());
            double normSecondNode = Math2.norm(position - allEdges.get(index).toPoint().e(), position - allEdges.get(index).toPoint().n());
            return (normFirstNode < normSecondNode) ? allEdges.get(index).fromNodeId() : allEdges.get(index).toNodeId();
        }
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint minimum = RoutePoint.NONE;
        for (Edge edge : allEdges) {
            double distance = Math2.clamp(0, edge.positionClosestTo(point), edge.length());
            minimum = minimum.min(edge.pointAt(distance), distance, edge.pointAt(distance).distanceTo(point));
        }
        return minimum;
    }

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

    /*@Override
    public double elevationAt(double position) {
        //double[] distances = {0d, 5800d, 8100d, 9200d, 11400d, 13100d};
        double[] distances = distanceList();
        int edgeId = edgeIndexAtPosition(position);
        if (position < 0d) {
            return allEdges.get(0).elevationAt(0);
        } else if (position > distances[distances.length-1]) {
            return allEdges.get(edgeId-1).elevationAt(allEdges.get(edgeId-1).length());
        }
        return allEdges.get(edgeId).elevationAt(position - distances[edgeId]);
    }*/

    private int edgeIndexAtPosition(double position) {
        return (Arrays.binarySearch(distances, position) < 0) ? -(Arrays.binarySearch(distances, position) + 2) : Arrays.binarySearch(distances, position);
    }

    private double correctedPosition(double position) {
        if (position < 0) {
            return 0d;
        } else if (position > length()) {
            return length();
        }
        return position;
    }
}
