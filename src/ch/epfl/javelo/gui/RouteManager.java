package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Edge;
import ch.epfl.javelo.routing.Route;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.function.Consumer;

public final class RouteManager {

    private final ReadOnlyObjectProperty<MapViewParameters> property;
    private final Consumer<String> error;
    private RouteBean bean;
    private final Pane pane;

    public RouteManager(RouteBean bean, ReadOnlyObjectProperty<MapViewParameters> mapParameters,  Consumer<String> error){
        this.property = mapParameters;
        pane = new Pane();
        this.error = error;

        pane.setPickOnBounds(false);

    }

    public Pane pane(){
        Polyline line = createLine();
        Circle circle = createCircle();

        pane.getChildren().add(line);
        pane.getChildren().add(circle);


        return pane;
    }

    public Polyline createLine(){
        Polyline polyline = new Polyline();
        polyline.setId("route");
        //todo niveau de zoom courant ? Xat ?

        Route route = bean.getRoute().get();
        for (Edge edge : route.edges()) {
            polyline.getPoints().addAll(edge.toPoint().lat(), edge.toPoint().lon());
            PointWebMercator pointMercator = PointWebMercator.ofPointCh((edge.fromPoint()));
            polyline.setLayoutX(property.get().viewX(pointMercator));
            polyline.setLayoutY(property.get().viewY(pointMercator));
        }
        //last point case
        PointWebMercator pointMercator = PointWebMercator.ofPointCh(route.edges().get(route.edges().size() - 1).toPoint());
        polyline.setLayoutX(property.get().viewX(pointMercator));
        polyline.setLayoutY(property.get().viewY(pointMercator));
        polyline.getPoints().addAll(route.edges().get(route.edges().size() - 1).toPoint().lat(), route.edges().get(route.edges().size() - 1).toPoint().lon());

        return polyline;
    }

    public Circle createCircle(){
        Circle circle = new Circle();
        circle.setId("highlight");
        circle.setCenterX(bean.highlightedPosition());
        //circle.setCenterY();
        circle.setRadius(5);

        circle.setOnMousePressed(e ->{
            //todo point2d vers position pour utiliser pointclosest to
            Point2D point = circle.localToParent(e.getX(), e.getY());
            bean.getRoute().get().nodeClosestTo(point)

            if(bean.getWaypoints().contains(point.))
            bean.getWaypoints().add()
            bean.getRoute().get().nodeClosestTo()
        });
        return circle;
    }
}
