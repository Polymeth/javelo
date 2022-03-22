package ch.epfl.javelo.routing;

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
        for (int i = 0; i < allEdges.size(); i++) {
            length += allEdges.get(i).length();
        }
        return length;
    }

    // todo: raisonnement correct ? immuable donc copy
    @Override
    public List<Edge> edges() {
        return List.copyOf(allEdges);
    }

    @Override
    public List<PointCh> points() {
        double[] distances = distanceList();
       // double[] distances = {0d, 5800d, 8100d, 9200d, 11400d, 13100d};
        List<PointCh> points = new ArrayList<>();

        for (double distance : distances) {
            points.add(pointAt(distance));
        }
        return points;
    }

    @Override
    public PointCh pointAt(double position) {
        double[] distances = distanceList();
        int edgeId = nodeClosestTo(position);
        return allEdges.get(edgeId).pointAt(position - distances[edgeId]);
    }

    @Override
    public int nodeClosestTo(double position) {
        double[] distances = distanceList();
        return (Arrays.binarySearch(distances, position) < 0) ? -(Arrays.binarySearch(distances, position) + 2) : Arrays.binarySearch(distances, position);
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        return null;
    }

    @Override
    public double elevationAt(double position) {
        //double[] distances = {0d, 5800d, 8100d, 9200d, 11400d, 13100d};
        if (position < 0d) position = 0d;
        double[] distances = distanceList();
        int edgeId = nodeClosestTo(position);
        return allEdges.get(edgeId).elevationAt(position - distances[edgeId]);
    }

    private double[] distanceList() {
        double[] distances = new double[allEdges.size()];
        for (int i = 0; i < allEdges.size(); i++) {
            if (i != 0) {
                distances[i] = distances[i-1] + allEdges.get(i).length();
            } else {
                distances[i] = 0d;
            }
        }
        return distances;
    }
}
