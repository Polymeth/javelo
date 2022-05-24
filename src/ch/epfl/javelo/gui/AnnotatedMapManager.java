package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

public final class AnnotatedMapManager {
    private final Graph routeGraph;
    private final TileManager tileManager;
    private final RouteBean route;
    private final Consumer<String> errorConsumer;

    private final StackPane pane;
    private SimpleObjectProperty<Point2D> mousePositionPoint2D;
    private final DoubleProperty mousePositionOnRouteProperty;
    private ObjectProperty<MapViewParameters> mvp;
    private final double DISTANCE = 15;

    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean route, Consumer<String> errorConsumer) {
        this.routeGraph = graph;
        this.tileManager = tileManager;
        this.route = route;
        this.errorConsumer = errorConsumer;
        this.mousePositionPoint2D = new SimpleObjectProperty<>(new Point2D(0, 0));
        this.mousePositionOnRouteProperty = new SimpleDoubleProperty();

        // todo : c bon là ?
        MapViewParameters mapViewParameters =
                new MapViewParameters(12, 543200, 370650);
        mvp =
                new SimpleObjectProperty<>(mapViewParameters);

        RouteManager routeManager = new RouteManager(route, mvp);
        WaypointsManager waypointsManager = new WaypointsManager(graph, mvp, route.getWaypoints(), errorConsumer);
        BaseMapManager mapManager = new BaseMapManager(tileManager, waypointsManager, mvp);

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);

        Pane mapPane = mapManager.pane();
        Pane routePane = routeManager.pane();
        Pane waypointsPane = waypointsManager.pane();

        pane = new StackPane();

        // Mouse Position Handler //todo pane ou route ?
        pane.setOnMouseMoved(e -> {
            Point2D point = new Point2D(e.getX(), e.getY());
            mousePositionPoint2D.set(point);
        });



        mousePositionOnRouteProperty.bind(Bindings.createDoubleBinding(() -> {
            if (route.route().get() != null) {
                MapViewParameters mapParameters = mvp.get();
                PointCh point = mapParameters.pointAt(mousePositionPoint2D.get().getX(), mousePositionPoint2D.get().getY()).toPointCh();

                RoutePoint closestPoint = route.route().get().pointClosestTo(point);
                double posX = mapParameters.viewX(PointWebMercator.ofPointCh(closestPoint.point()));
                double posY = mapParameters.viewY(PointWebMercator.ofPointCh(closestPoint.point()));

                if (Math2.norm((mousePositionPoint2D.get().getX() - posX), (mousePositionPoint2D.get().getY() - posY)) < DISTANCE) {
                    return closestPoint.position();
                } else {
                    return Double.NaN;
                }
            }
            else
                return Double.NaN;
        }, mousePositionPoint2D, mvp, route.route()));
        pane.getChildren().addAll(mapPane, routePane, waypointsPane);
        pane.getStylesheets().add("map.css");
    }

    public Pane pane() {
        return this.pane;
    }

    private void pointClosestRoute(){
        MapViewParameters mapParameters = mvp.get();
        PointCh point = mapParameters.pointAt(mousePositionPoint2D.get().getX(), mousePositionPoint2D.get().getY()).toPointCh();

        RoutePoint closestPoint = route.route().get().pointClosestTo(point);
        double posX = mapParameters.viewX(PointWebMercator.ofPointCh(closestPoint.point()));
        double posY = mapParameters.viewY(PointWebMercator.ofPointCh(closestPoint.point()));

        if(Math2.norm((mousePositionPoint2D.get().getX() - posX), (mousePositionPoint2D.get().getY() - posY)) < DISTANCE){
            mousePositionOnRouteProperty.set(closestPoint.position());
        } else {
            mousePositionOnRouteProperty.set(Double.NaN);
        }
    }

    public DoubleProperty mousePositionOnRouteProperty() {
        return mousePositionOnRouteProperty;
    }
}
