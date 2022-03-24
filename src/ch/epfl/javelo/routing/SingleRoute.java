package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SingleRoute implements Route {
    private final ArrayList<Edge> allEdges = new ArrayList<>();

    public SingleRoute(List<Edge> edges) {
       Preconditions.checkArgument(edges.size() != 0);
        for (int i = 0; i < edges.size(); i++) {
            allEdges.add(edges.get(i));
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
        double[] distances = distanceList();
        List<PointCh> points = new ArrayList<>();
        for (double distance : distances) {
            points.add(pointAt(distance));
        }
        return points;
    }

    @Override
    public PointCh pointAt(double position) {
        double[] distances = distanceList();
        int edgeId = edgeIndexAtPosition(position);
        return (edgeId < distances.length-1)
                ? allEdges.get(edgeId).pointAt(position - distances[edgeId])
                : allEdges.get(edgeId-1).pointAt(allEdges.get(edgeId-1).length()); // kinda sus
        // todo passer le bidule du edgeId - 1 dans la methode index
    }

    @Override
    public int nodeClosestTo(double position) {
        double[] distances = distanceList();
        int edgeId = edgeIndexAtPosition(position);
        double distanceOnEdge = position - distances[edgeId];

        if (distanceOnEdge < allEdges.get(edgeId).length()) {
            //SEX
        }

        return 0;


    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint minimum = RoutePoint.NONE;
        //for (Edge edge : allEdges) {

        //}
        return null;
    }

    @Override
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
    }

    private int edgeIndexAtPosition(double position) {
        double[] distances = distanceList();
        return (Arrays.binarySearch(distances, position) < 0) ? -(Arrays.binarySearch(distances, position) + 2) : Arrays.binarySearch(distances, position);
    }

    /**
     * @return a
     */
    private double[] distanceList() {
        double[] distances = new double[allEdges.size()+1];

        for (int i = 0; i <= allEdges.size(); i++) {
            if (i != 0) {
                Edge edge = allEdges.get(i-1);
                distances[i] = distances[i-1] + allEdges.get(i-1).length();
            } else {
                distances[i] = 0d;
            }
        }
        return distances;
    }
}
