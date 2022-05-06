package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Edge;
import ch.epfl.javelo.routing.Route;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class RouteManager {

    private final ReadOnlyObjectProperty<MapViewParameters> property;
    private final Consumer<String> error;
    private final RouteBean bean;
    private final Pane pane;

    private final Polyline polyline; // todo legal ?
    private int actualZoom;

    public RouteManager(RouteBean bean, ReadOnlyObjectProperty<MapViewParameters> mapParameters, Consumer<String> error) {
        this.property = mapParameters;
        this.pane = new Pane();
        this.error = error;
        this.bean = bean;
        this.polyline = new Polyline();

        this.pane.getChildren().add(polyline);
        this.polyline.setLayoutX(-mapParameters.get().topLeft().getX());
        this.polyline.setLayoutY(-mapParameters.get().topLeft().getY());
        this.pane.setPickOnBounds(false);
        this.actualZoom = property.get().zoomlevel();
        polyline.setId("route");

        mapParameters.addListener(p -> {
            if (actualZoom != property.get().zoomlevel()) {
                createLine();
                actualZoom = property.get().zoomlevel();
            } else {
                polyline.setLayoutX(-mapParameters.get().topLeft().getX());
                polyline.setLayoutY(-mapParameters.get().topLeft().getY());
            }
        });

        bean.getWaypoints().addListener((ListChangeListener<Waypoint>) p -> {
            createLine();
        });
    }

    public Pane pane() {
        createLine();
        Circle circle = createCircle();
        pane.getChildren().add(circle);
        return pane;
    }

    public void createLine() {
        System.out.println(polyline.getLayoutX());
        Route route = bean.getRoute().get();
        polyline.getPoints().clear();

        if (bean.getRoute().get() != null) {
            List<Double> routePoints = new ArrayList<>();
            for (PointCh point : route.points()) {
                PointWebMercator pointMercator = PointWebMercator.ofPointCh(point);
                routePoints.add(pointMercator.xAtZoomLevel(property.get().zoomlevel()));
                routePoints.add(pointMercator.yAtZoomLevel(property.get().zoomlevel()));
            }
            polyline.getPoints().addAll(routePoints);
            polyline.setLayoutX(-property.get().topLeft().getX());
            polyline.setLayoutY(-property.get().topLeft().getY());
        }
    }

    public Circle createCircle(){
        Circle circle = new Circle();
        circle.setId("highlight");
        //creer pointWBM pour avoir les coordonées x et y
        PointWebMercator circleWBM = PointWebMercator.ofPointCh( bean.getRoute().get().pointAt(bean.highlightedPosition()));
        circle.setCenterX(circleWBM.x());
        circle.setCenterY(circleWBM.y());
        circle.setRadius(5);

        circle.setOnMousePressed(e ->{
            //todo point2d vers position pour utiliser pointclosest to
            Point2D point = circle.localToParent(e.getX(), e.getY());
            PointWebMercator pointWBM = PointWebMercator.of(property.get().zoomlevel(), point.getX(), point.getY());
            double pos = bean.getRoute().get().pointClosestTo(pointWBM.toPointCh()).position();

            int nodeid = bean.getRoute().get().nodeClosestTo(pos);

            boolean nodeAlreadyExists = false;

            for (Waypoint wp : bean.getWaypoints()){
                if (wp.nodeId() == nodeid){
                    error.accept("Un point de passage est déjà présent à cet endroit !");
                    nodeAlreadyExists = true;
                }
            }

            if(!nodeAlreadyExists){
                bean.getWaypoints().add(new Waypoint(pointWBM.toPointCh(), nodeid));
            }

        });
        return circle;
    }
}
