package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MultiRoute implements Route{
    private final List<Route> allRoutes;
    private final double[] distances;

    public MultiRoute(List<Route> segments){
        allRoutes = List.copyOf(segments); // faire de même dans singleroute
        this.distances = new double[allRoutes.size()+1];
        distances[0] = 0;
        for (int i = 1; i < distances.length; i++) {
            distances[i] = distances[i - 1] + (allRoutes.get(i-1).length());
        }
    }

    @Override
    public int indexOfSegmentAt(double position) {
        position = correctedPosition(position);
        // first step = find which route of the list
        int indexOnList = (Arrays.binarySearch(distances, position) < 0) ? -(Arrays.binarySearch(distances, position) + 2) : Arrays.binarySearch(distances, position);


    }

    @Override
    public double length() {
        double length = 0;
        for (Route route : allRoutes) {
            length+=route.length();
        }
        return length;
    }

    @Override
    public List<Edge> edges() {
        List<Edge> allEdges = new ArrayList<>();
        for(Route route : allRoutes){
            for(Edge edge : route.edges()) {
                allEdges.add(edge);
            }
        }
        return allEdges;
    }

    @Override
    public List<PointCh> points() {
        return null;
    }

    @Override
    public PointCh pointAt(double position) {
        position = correctedPosition(position);
        return null;
    }

    @Override
    public int nodeClosestTo(double position) {
        position = correctedPosition(position);
        return 0;
    }


    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        return null;
    }

    @Override
    public double elevationAt(double position) {
        return 0;
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
