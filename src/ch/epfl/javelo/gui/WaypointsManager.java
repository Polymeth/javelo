package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.function.Consumer;

/**
 * Manages the display and the interaction with the waypoints
 *
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class WaypointsManager {

    private final Graph graph;
    private final ObjectProperty<MapViewParameters> property;
    private final ObservableList<Waypoint> waypoints;
    private final Consumer<String> error;
    private final Pane pane;

    /**
     * Creates an instance of a WaypointManager
     *
     * @param graph the road network
     * @param property the parameters of the displayed map
     * @param waypoints a list of waypoints
     * @param error an object to report an error
     */
    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> property, ObservableList<Waypoint> waypoints, Consumer<String> error){
        this.graph = graph;
        this.property = property;
        this.waypoints = waypoints;
        this.error = error;
        pane = new Pane();
        pane.setPrefSize(600, 300);

        pane.setPickOnBounds(false);

        // recreates waypoints when the list changes
        waypoints.addListener((ListChangeListener<Waypoint>) l-> createWaypoints());

        // recreates waypoints if the map changes
        property.addListener(p -> createWaypoints());
    }

    /**
     * returns the pane containing the waypoints
     *
     * @return the pane containing the waypoints
     */
    public Pane pane(){
        createWaypoints();
        return pane;
    }

    /**
     * add a new waypoint to the node of the graph that is closest to it
     *
     * @param x the x-coordinate of a point
     * @param y the y-coordinate of a point
     */
    public void addWaypoint(double x, double y) {
        PointWebMercator pointWBM = PointWebMercator.of(property.get().zoomlevel(), x, y);
        int nodeId = graph.nodeClosestTo(pointWBM.toPointCh(), 500);
        if (nodeId == -1){
            error.accept("Aucune route à proximité !");
        } else {
            waypoints.add(new Waypoint(graph.nodePoint(nodeId), nodeId));
        }

    }


    private Group createPin(int i){
        //todo ptet modulariser on verra
        SVGPath outsidePoint = new SVGPath();
        outsidePoint.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
        outsidePoint.getStyleClass().add("pin_outside");

        SVGPath insidePoint = new SVGPath();
        insidePoint.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");
        insidePoint.getStyleClass().add("pin_inside");

        Group group = new Group();
        group.getStyleClass().add("pin");
        group.getChildren().add(outsidePoint);
        group.getChildren().add(insidePoint);

        //set id of group with nodeId of waypoint
        group.setId(String.valueOf(waypoints.get(i).nodeId()));

        // set coordinates of group with coordinates of waypoint
        // transforms coordinates into PointWebMercator format
        PointWebMercator pointMercator = PointWebMercator.ofPointCh((waypoints.get(i).point()));
        group.setLayoutX(property.get().viewX(pointMercator));
        group.setLayoutY(property.get().viewY(pointMercator));

        // click
        ObjectProperty<Point2D> pointPressed = new SimpleObjectProperty<>();
        group.setOnMousePressed(e -> pointPressed.setValue(new Point2D(e.getX(), e.getY())));

        // drag
        group.setOnMouseDragged(e -> {
            double x = group.getLayoutX();
            double y = group.getLayoutY();
            if (pointPressed.get() != null) return;
            group.setLayoutX(e.getX() + x - pointPressed.get().getX());
            group.setLayoutY(e.getY() + y - pointPressed.get().getY());
        });

        // release
        group.setOnMouseReleased(e -> {
            if (e.isStillSincePress()) {
                waypoints.remove(waypoints.get(i));
            } else {
                PointCh pos = property.get().pointAt(group.getLayoutX(), group.getLayoutY()).toPointCh();
                int nodeId = graph.nodeClosestTo(pos, 500);
                if (nodeId == -1) {
                    error.accept("Aucune route à proximité !");
                } else {
                    waypoints.set(i, new Waypoint(graph.nodePoint(nodeId), nodeId));
                }
                createWaypoints();
            }
        });

        return group;
    }

    private void createWaypoints() {
        pane.getChildren().clear();
        for(int i = 0; i < waypoints.size(); i++){
            Group group = createPin(i);

            if(i != 0 && i != waypoints.size() -1){
                group.getStyleClass().add("middle");
            }
            //first point case
            else if (i == 0) {
                group.getStyleClass().add("first");
            }
            //last point case
            else if(i == waypoints.size() -1){
                group.getStyleClass().add("last");
            }

            pane.getChildren().add(group);
        }
    }
/*
    private Waypoint placeWaypoint(double x, double y){
        PointWebMercator pointWBM = PointWebMercator.of(property.get().zoomlevel(), x, y);
        PointCh pos = property.get().pointAt(x, y).toPointCh();
        int nodeid = graph.nodeClosestTo(pos, 500);
        if (nodeid == -1){
            error.accept("Aucune route à proximité !");
            return null;
        } else {
            return new Waypoint(pos, nodeid);
        }
    }

 */

}