package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.WebMercator;
import javafx.beans.property.ObjectProperty;
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


        for(int i = 0; i < waypoints.size(); i++){

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

            //group.setLayoutX((waypoints.get(i).point().e()) - property.get().originXcoord()); //todo quel système de coordonées ?
           // group.setLayoutY((waypoints.get(i).point().n()) - property.get().originYcoord());

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

        ObservableList<Node> jul = pane.getChildren();
        return pane;
    }

    public void addWaypoint(double x, double y){

    }
}
