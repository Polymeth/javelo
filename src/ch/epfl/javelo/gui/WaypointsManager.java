package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.WebMercator;
import javafx.geometry.Point2D;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;


import java.util.List;
import java.util.function.Consumer;

public final class WaypointsManager {

    private Graph graph;
    private ObjectProperty<MapViewParameters> property;
    private ObservableList<Waypoint> waypoints;
    Consumer<String> error;

    private final Pane pane;
    private final Canvas canvas;


    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> property, ObservableList<Waypoint> waypoints, Consumer<String> error){
        this.graph = graph;
        this.property = property;
        this.waypoints = waypoints;
        this.error = error;

        pane = new Pane();
        canvas = new Canvas();
        pane.getChildren().add(canvas);
        pane.setPrefSize(600, 300);
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        waypoints.addListener((ListChangeListener<Waypoint>) l-> {
            createWaypoints();
            System.out.println("creating waypoints");
            waypoints.forEach(System.out::println);
        });

        pane.setPickOnBounds(false);

        //pane.setOnMouseClicked();

    }

    public Pane pane(){
        //todo marqueur dedans ou dehors boucle for
        //todo modulariser pour recalculer selon le nieveau de zoom
        /*
        SVGPath outsidePoint = new SVGPath();
        outsidePoint.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
        outsidePoint.getStyleClass().add("pin_outside");

        SVGPath insidePoint = new SVGPath();
        insidePoint.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");
        insidePoint.getStyleClass().add("pin_inside");
         */
        createWaypoints();
        return pane;
    }

    private Group drawPin(double x, double y) {
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

        group.setLayoutX(property.get().originXcoord() + x);
        group.setLayoutY(property.get().originYcoord() + y);

        return group;
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

        //set coordinates of group with coordinates of waypoint
        //Transforms coordinates into PointWebMercator format
        PointWebMercator pointMercator = PointWebMercator.ofPointCh((waypoints.get(i).point()));
        group.setLayoutX(property.get().viewX(pointMercator));
        group.setLayoutY(property.get().viewY(pointMercator));

        group.setOnMouseReleased(e -> {
            if (e.isStillSincePress()) {
                waypoints.remove(waypoints.get(i));
                System.out.println("test1");
            }
        });

        ObjectProperty<Point2D> pointPressed = new SimpleObjectProperty<>();
        ObjectProperty<Boolean> isBeingDragged = new SimpleObjectProperty<>();
        isBeingDragged.set(false);

        group.setOnMousePressed(e -> {
            pointPressed.setValue(new Point2D(e.getX(), e.getY()));
        });

        group.setOnMouseDragged(e -> {
            double x = group.getLayoutX();
            double y = group.getLayoutY();
            //group.setLayoutX();
            drawPin(e.getX(), e.getY());
        });

        group.setOnMouseReleased(e -> {

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

    public void addWaypoint(double x, double y) {
        //todo utiliser x at zoom level ? ou view x
        PointWebMercator pointWBM = PointWebMercator.of(property.get().zoomlevel(), x, y);

        int nodeid = graph.nodeClosestTo(pointWBM.toPointCh(), 500);
        if (nodeid == -1){
            error.accept("Aucune route à proximité !");
        } else {
            waypoints.add(new Waypoint(pointWBM.toPointCh(), nodeid));
        }

    }

}
