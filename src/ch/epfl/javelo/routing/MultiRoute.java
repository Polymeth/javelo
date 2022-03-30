package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MultiRoute implements Route{
    private final List<Route> allRoutes;
    private final double[] distances;

    public MultiRoute(List<Route> segments){
        //Preconditions.checkArgument(!segments.isEmpty());
        allRoutes = List.copyOf(segments); // faire de même dans singleroute
        this.distances = new double[allRoutes.size()+1];
        distances[0] = 0;
        for (int i = 1; i < distances.length; i++) {
            distances[i] = distances[i - 1] + (allRoutes.get(i-1).length());
        }
    }

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

    @Override
    public double length() {
        double length = 0;
        for (Route route : allRoutes) {
            length += route.length();
        }
        return length;
    }

    @Override
    public List<Edge> edges() {
        List<Edge> allEdges = new ArrayList<>();
        for(Route route : allRoutes){
            allEdges.addAll(route.edges());
        }
        return allEdges;
    }

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

    @Override
    public PointCh pointAt(double position) {
        position = Math2.clamp(0, position, length());
        PointCh point = null;

        for(Route route : allRoutes){
            point = route.pointAt(position);
        }
        return point;
    }

    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0, position, length());
        int node = 0;

        for(Route route : allRoutes){
            node = route.nodeClosestTo(position);
        }
        return node;
    }


    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint minimum = RoutePoint.NONE;

        for(Route route : allRoutes){
            minimum = route.pointClosestTo(point);
        }
        return minimum;
    }

    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0, position, length());



        return elevation;
    }
}
