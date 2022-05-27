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

/**
 * The manager for the whole map
 *
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class AnnotatedMapManager {
    private static final int DEFAULT_ZOOM_LEVEL = 12;
    private static final int DEFAULT_X_COORDS = 543200;
    private static final int DEFAULT_Y_COORDS = 370650;

    private final StackPane pane;
    private final SimpleObjectProperty<Point2D> mousePositionPoint2D;
    private final DoubleProperty mousePositionOnRouteProperty;
    private final ObjectProperty<MapViewParameters> mvp;
    private final double DISTANCE = 15;

    /**
     * The manager for the whole map
     *
     * @param graph         any graph containg the map's information
     * @param tileManager   any Tile Manager
     * @param route         a bean which will contain the route
     * @param errorConsumer an error consumer for error management
     */
    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean route, Consumer<String> errorConsumer) {
        this.mousePositionPoint2D = new SimpleObjectProperty<>(new Point2D(0, 0));
        this.mousePositionOnRouteProperty = new SimpleDoubleProperty();

        MapViewParameters mapViewParameters =
                new MapViewParameters(DEFAULT_ZOOM_LEVEL, DEFAULT_X_COORDS, DEFAULT_Y_COORDS);
        mvp = new SimpleObjectProperty<>(mapViewParameters);

        RouteManager routeManager = new RouteManager(route, mvp);
        WaypointsManager waypointsManager = new WaypointsManager(graph, mvp, route.getWaypoints(), errorConsumer);
        BaseMapManager mapManager = new BaseMapManager(tileManager, waypointsManager, mvp);

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);

        Pane mapPane = mapManager.pane();
        Pane routePane = routeManager.pane();
        Pane waypointsPane = waypointsManager.pane();

        pane = new StackPane();
        pane.getStylesheets().add("map.css");
        pane.getChildren().addAll(mapPane, routePane, waypointsPane);

        // mouse moving
        pane.setOnMouseMoved(e -> {
            Point2D point = new Point2D(e.getX(), e.getY());
            mousePositionPoint2D.set(point);
        });

        mousePositionOnRouteProperty.bind(Bindings.createDoubleBinding(() -> {
            if (route.route().get() != null) {
                MapViewParameters mapParameters = mvp.get();
                PointCh point = mapParameters.pointAt(mousePositionPoint2D.get().getX(), mousePositionPoint2D.get().getY()).toPointCh();

                if (point == null) System.out.println("zebi");
                RoutePoint closestPoint = route.route().get().pointClosestTo(point);
                PointWebMercator pwb = PointWebMercator.ofPointCh(closestPoint.point());
                double posX = mapParameters.viewX(pwb);
                double posY = mapParameters.viewY(pwb);

                if (Math2.norm((mousePositionPoint2D.get().getX() - posX), (mousePositionPoint2D.get().getY() - posY)) < DISTANCE) {
                    return closestPoint.position();
                } else {
                    return Double.NaN;
                }
            } else {
                return Double.NaN;
            }
        }, mousePositionPoint2D, mvp, route.route()));
    }

    /**
     * @return a pane with the map, the waypoints and the route
     */
    public Pane pane() {
        return this.pane;
    }

    /**
     * @return a jfx propert containg the mouse position on the route, if it exists (otherwise Null)
     */
    public DoubleProperty mousePositionOnRouteProperty() {
        return mousePositionOnRouteProperty;
    }
}
