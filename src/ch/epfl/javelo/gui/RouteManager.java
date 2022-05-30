package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Route;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;

/**
 * The system to draw the route
 *
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class RouteManager {
    private final ReadOnlyObjectProperty<MapViewParameters> property;
    private final RouteBean bean;
    private final Pane pane;

    private final Polyline polyline;
    private final Circle circle;
    private int actualZoom;

    /**
     * Initiliaze the route manager
     *
     * @param routeBean     a jfx bean containing the current route
     * @param mapParameters a property containg the map view parameters
     */
    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapParameters) {
        this.property = mapParameters;
        this.pane = new Pane();
        this.bean = routeBean;
        this.polyline = new Polyline();
        this.circle = new Circle();

        this.actualZoom = property.get().zoomlevel();
        this.pane.getChildren().add(polyline);
        this.pane.setPickOnBounds(false);
        this.polyline.setLayoutX(-mapParameters.get().topLeft().getX());
        this.polyline.setLayoutY(-mapParameters.get().topLeft().getY());
        this.polyline.setId("route");

        circle.setId("highlight");
        circle.setRadius(5);
        circle.setVisible(false);

        // mouse click
        circle.setOnMousePressed(e -> {
            Point2D point = circle.localToParent(e.getX(), e.getY());

            PointWebMercator pointWBM = PointWebMercator.of(property.get().zoomlevel(),
                    property.get().pointAt(point.getX(), point.getY()).xAtZoomLevel(property.get().zoomlevel()),
                    property.get().pointAt(point.getX(), point.getY()).yAtZoomLevel(property.get().zoomlevel()));
            double pos = routeBean.route().get().pointClosestTo(pointWBM.toPointCh()).position();
            int nodeid = routeBean.route().get().nodeClosestTo(pos);

            if (nodeid != 0) {
                int index = routeBean.indexOfNonEmptySegmentAt(pos);
                routeBean.getWaypoints().add(index + 1, new Waypoint(pointWBM.toPointCh(), nodeid));
            }
        });

        mapParameters.addListener(p -> {
            if (actualZoom != property.get().zoomlevel()) {
                createLine();
                actualZoom = property.get().zoomlevel();
            } else {
                polyline.setLayoutX(-mapParameters.get().topLeft().getX());
                polyline.setLayoutY(-mapParameters.get().topLeft().getY());
            }
            createCircle();
        });

        routeBean.getWaypoints().addListener((ListChangeListener<Waypoint>) p -> {
            createLine();
            createCircle();
        });

        routeBean.highlightedPositionProperty().addListener(o -> createCircle());

        circle.visibleProperty().bind(routeBean.highlightedPositionProperty().greaterThanOrEqualTo(0));
        polyline.visibleProperty().bind(routeBean.elevationProfile().isNotNull());
    }

    /**
     * @return a pane with the route
     */
    public Pane pane() {
        createLine();
        createCircle();
        pane.getChildren().add(circle);
        return pane;
    }

    /**
     * Creates the line representing the route
     */
    private void createLine() {
        Route route = bean.route().get();
        polyline.getPoints().clear();

        if (bean.route().get() != null) {
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

    /**
     * Creates the circle representing the highlighted position
     */
    public void createCircle() {
        if (bean.route().get() != null) {
            if(Double.isNaN(bean.highlightedPosition())) return;
            PointCh point = bean.route().get().pointAt(bean.highlightedPosition());
            PointWebMercator circleWBM = PointWebMercator.ofPointCh(point);
            circle.setCenterX(
                    circleWBM.xAtZoomLevel(property.get().zoomlevel()) -
                            property.get().pointAt(0, 0).xAtZoomLevel(property.get().zoomlevel()));
            circle.setCenterY(
                    circleWBM.yAtZoomLevel(property.get().zoomlevel()) -
                            property.get().pointAt(0, 0).yAtZoomLevel(property.get().zoomlevel()));
        }
    }
}
