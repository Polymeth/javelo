package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.*;

/**
 * The bean containing the current route
 *
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class RouteBean {
    private final RouteComputer rc;
    private final ObservableList<Waypoint> waypoints;
    private final ObjectProperty<Route> route;
    private final DoubleProperty highlightedPosition;
    private final ObjectProperty<ElevationProfile> elevationProfile;

    private final Map<Pair<Integer, Integer>, Route> cache;
    private final static int RAM_CACHE_CAPACITY = 50;

    /**
     * Initiliaze the bean contaning the current route
     *
     * @param rc any RouteComputer used to compute a path
     */
    public RouteBean(RouteComputer rc) {
        this.rc = rc;
        this.waypoints = FXCollections.observableArrayList();
        this.highlightedPosition = new SimpleDoubleProperty(Double.NaN);
        this.route = new SimpleObjectProperty<>();
        this.elevationProfile = new SimpleObjectProperty<>();
        this.cache = new LinkedHashMap<>();

        waypoints.addListener((ListChangeListener<Waypoint>) l -> {
            if (!isARouteNotNull() || waypoints.size() < 2) {
                route.set(null);
                elevationProfile.set(null);
            } else {
                route.set(createRoute());
                elevationProfile.set(ElevationProfileComputer.elevationProfile(route.get(), 5));
            }
        });
    }

    /**
     * @param position the position on the segment (in meters)
     * @return the index of the segment of the position on the complete route
     */
    public int indexOfNonEmptySegmentAt(double position) {
        int index = route().get().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypoints.get(i).nodeId();
            int n2 = waypoints.get(i + 1).nodeId();
            if (n1 == n2) index += 1;
        }
        return index;
    }

    /**
     * @return the highlighted position on the route, as a property
     */
    public DoubleProperty highlightedPositionProperty() {
        return this.highlightedPosition;
    }

    /**
     * @return the highlighted position on the route, in meters
     */
    public double highlightedPosition() {
        return this.highlightedPosition.get();
    }

    /**
     * Set the highlighted position to the entered value
     *
     * @param pos the desired position (in meters)
     */
    public void setHighlightedPosition(double pos) {
        this.highlightedPosition.set(pos);
    }

    /**
     * @return the route, as a read only property
     */
    public ReadOnlyObjectProperty<Route> route() {
        return this.route;
    }

    /**
     * @return the elevation profile, as a ready only property
     */
    public ReadOnlyObjectProperty<ElevationProfile> elevationProfile() {
        return this.elevationProfile;
    }

    /**
     * @return A observable list with all the waypoints making the route
     */
    public ObservableList<Waypoint> getWaypoints() {
        return this.waypoints;
    }

    /**
     * Initialize the route
     *
     * @return a route with all the segment
     */
    private Route createRoute() {
        List<Route> allSegments = new ArrayList<>();
        for (int i = 0; i < waypoints.size() - 1; i++) {
            if (waypoints.get(i).nodeId() != waypoints.get(i + 1).nodeId()) {
                Pair<Integer, Integer> node = new Pair<>(waypoints.get(i).nodeId(), waypoints.get(i + 1).nodeId());
                if (cache.containsKey(node)) {
                    allSegments.add(cache.get(node));
                } else {
                    // check if the RAM cache is too big
                    if (cache.size() == RAM_CACHE_CAPACITY) {
                        cache.remove(cache.entrySet().iterator().next().getKey());
                    }
                    Route segment = rc.bestRouteBetween(node.getKey(), node.getValue());
                    cache.put(node, segment);
                    allSegments.add(segment);
                }
            }
        }
        return new MultiRoute(allSegments);
    }

    /**
     * @return weither or not the route is really existing
     */
    private boolean isARouteNotNull() {
        for (int i = 0; i < waypoints.size() - 1; i++) {
            if ((waypoints.get(i).nodeId() != waypoints.get(i + 1).nodeId())) {
                if (rc.bestRouteBetween(waypoints.get(i).nodeId(), waypoints.get(i + 1).nodeId()) == null) {
                    return false;
                }
            }
        }
        return true;
    }

}
