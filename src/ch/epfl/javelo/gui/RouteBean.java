package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.MultiRoute;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public final class RouteBean {

    private RouteComputer rc;
    public ObservableList<Waypoint> waypoints;
    public ReadOnlyObjectProperty<Route> route;
    public DoubleProperty highlightedPosition;
    public ReadOnlyObjectProperty<ElevationProfile> elevationProfile;

    public RouteBean(RouteComputer rc){
        this.rc = rc;

        if(waypoints.size() < 2){
            route = null;
            elevationProfile = null;
        }
        if (!isRouteBetween(waypoints)) {
            route = null;
            elevationProfile = null;
        }

        waypoints.addListener((ListChangeListener<Waypoint>) l-> {
            createRoute(waypoints);
        });
    }

    private boolean isRouteBetween(List<Waypoint> waypointList) {
        for (int i = 0; i < waypoints.size() - 2; i++) {
            if (rc.bestRouteBetween(waypoints.get(i).nodeId(), waypoints.get(i + 1).nodeId()) == null) {
                return false;
            }
        }
        return true;
    }

    private void createRoute(List<Waypoint> waypointList){
        //todo implements map to not recalculate everytime
        List<Route> allSegments = new ArrayList<>();
        for (int i = 0; i < waypoints.size() - 2; i++) {
            Route segment = rc.bestRouteBetween(waypoints.get(i).nodeId(), waypoints.get(i + 1).nodeId());
            allSegments.add(segment);
        }
        MultiRoute route1 = new MultiRoute(allSegments);
        //todo create elevation profile

    }
}
