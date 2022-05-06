package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public final class RouteBean {
    private final RouteComputer rc;
    public ObservableList<Waypoint> waypoints;
    public ObjectProperty<Route> route;
    public DoubleProperty highlightedPosition;
    public ObjectProperty<ElevationProfile> elevationProfile;

    public RouteBean(RouteComputer rc){
        this.rc = rc;
        this.waypoints = FXCollections.observableArrayList();
        this.highlightedPosition = new SimpleDoubleProperty();
        this.route = new SimpleObjectProperty<>();
        this.elevationProfile = new SimpleObjectProperty<>();

        waypoints.addListener((ListChangeListener<Waypoint>) l-> {
            if ((waypoints.size() < 2) || !isARouteNull()) {
                System.out.println("y'a r");
                route.set(null);
                elevationProfile.set(null);
            } else {
                route.set(createRoute());
                elevationProfile.set(ElevationProfileComputer.elevationProfile(route.get(), 5));
                System.out.println("length: " + createRoute().length());
                System.out.println(ElevationProfileComputer.elevationProfile(route.get(), 5).minElevation()
                        + "m to " + ElevationProfileComputer.elevationProfile(route.get(), 5).maxElevation() + "m");
            }
        });
    }

    private Route createRoute(){
        //todo cache
        List<Route> allSegments = new ArrayList<>();
        for (int i = 0; i < waypoints.size() - 1; i++) {
            Route segment = rc.bestRouteBetween(waypoints.get(i).nodeId(), waypoints.get(i + 1).nodeId());
            allSegments.add(segment);
        }
        return new MultiRoute(allSegments);
    }

    private boolean isARouteNull() {
        for (int i = 0; i < waypoints.size() - 2; i++) {
            if (rc.bestRouteBetween(waypoints.get(i).nodeId(), waypoints.get(i + 1).nodeId()) == null) {
                return false;
            }
        }
        return true;
    }

    public DoubleProperty highlightedPositionProperty() {
        return this.highlightedPosition;
    }

    public double highlightedPosition() {
        return this.highlightedPosition.get();
    }

    public void setHighlightedPosition(double pos) {
        this.highlightedPosition.set(pos);
    }

    public ReadOnlyObjectProperty<Route> getRoute() {
        return this.route;
    }

    public ReadOnlyObjectProperty<ElevationProfile> getElevationProfile() {
        return this.elevationProfile;
    }

    public ObservableList<Waypoint> getWaypoints() {
        return this.waypoints;
    }

}
