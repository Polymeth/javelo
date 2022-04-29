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
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;


import java.util.function.Consumer;

public final class WaypointsManager {

    private Graph graph;
    private ObjectProperty<MapViewParameters> property;
    private ObservableList<Waypoint> waypoints;
    Consumer<String> error;

    private final Pane pane;


    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> property, ObservableList<Waypoint> waypoints, Consumer<String> error){
        this.graph = graph;
        this.property = property;
        this.waypoints = waypoints;
        this.error = error;




        pane = new Pane();
        pane.setPrefSize(600, 300);

        pane.setPickOnBounds(false);

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



        ObjectProperty<Point2D> pointPressed = new SimpleObjectProperty<>();
        ObjectProperty<Boolean> isBeingDragged = new SimpleObjectProperty<>();
        isBeingDragged.set(false);

        group.setOnMousePressed(e -> {
            pointPressed.setValue(new Point2D(e.getX(), e.getY()));
        });

        group.setOnMouseDragged(e -> {
            double x = group.getLayoutX();
            double y = group.getLayoutY();
            group.setLayoutX(e.getX() + x - pointPressed.get().getX());
            group.setLayoutY(e.getY() + y - pointPressed.get().getY());
        });

        group.setOnMouseReleased(e -> {
            if (e.isStillSincePress()) {
                waypoints.remove(waypoints.get(i));
                System.out.println("deleting point");
            } else {
                //PointWebMercator newPoint = PointWebMercator.of(property.get().zoomlevel(), property.get().originXcoord() + e.getX(), property.get().originYcoord() + e.getY());
               // System.out.println("new point is x: " + mousePoint + "; " + finalY);
                //

                PointCh pos = property.get().pointAt(group.getLayoutX(), group.getLayoutY()).toPointCh();
                int nodeid = graph.nodeClosestTo(pos, 500);
                if (nodeid == -1){
                    error.accept("Aucune route à proximité !");
                    createWaypoints();
                } else {
                    waypoints.set(i, new Waypoint(graph.nodePoint(nodeid), nodeid));
                    createWaypoints();
                }

                //System.out.println("sont affichés être en " + property.get().viewX(newPoint) + " ;" + property.get().viewY(newPoint));

                //PointCh pointchtest = newPoint.toPointCh();
                //int nodeId = graph.nodeClosestTo(newPoint.point(), 500); //todo fix node id

                //waypoints.set(i, newPoint);
                //createWaypoints();

            }

            // Waypoint[point=PointCh[e=2535651.836318822, n=1155128.3486538585], nodeId=107825]
        });

        /*group.setOnMouseReleased(e -> {
            double finalX = property.get().originXcoord() + e.getX();
            double finalY = property.get().originYcoord() + e.getY();
            PointWebMercator newPoint = PointWebMercator.of(property.get().zoomlevel(), finalX, finalY);

        return group;
    }

    private void createWaypoints() {
        pane.getChildren().clear();
        for(int i = 0; i < waypoints.size(); i++){
            System.out.println("ntm");
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

    public Waypoint placeWaypoint(double x, double y){
        PointWebMercator pointWBM = PointWebMercator.of(property.get().zoomlevel(), x, y);
        PointCh pos = property.get().pointAt(x, y).toPointCh();
        System.out.println(" waypoint");
        int nodeid = graph.nodeClosestTo(pos, 500);
        if (nodeid == -1){
            error.accept("Aucune route à proximité !");
            return null;
        } else {
            return new Waypoint(pos, nodeid);
        }
    }

    public void addWaypoint(double x, double y) {
        //todo utiliser x at zoom level ? ou view x
        PointWebMercator pointWBM = PointWebMercator.of(property.get().zoomlevel(), x, y);
        System.out.println("adding waypoint");
        int nodeid = graph.nodeClosestTo(pointWBM.toPointCh(), 500);
        if (nodeid == -1){
            error.accept("Aucune route à proximité !");
        } else {
            waypoints.add(new Waypoint(pointWBM.toPointCh(), nodeid));
        }

    }

}
