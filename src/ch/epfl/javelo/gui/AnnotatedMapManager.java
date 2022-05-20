package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

public final class AnnotatedMapManager {
    private final Graph routeGraph;
    private final TileManager tileManager;
    private final RouteBean route;
    private final Consumer<String> errorConsumer;

    private final Pane pane;
    private final DoubleProperty mousePositionOnRouteProperty;

    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean route, Consumer<String> errorConsumer) {
        this.routeGraph = graph;
        this.tileManager = tileManager;
        this.route = route;
        this.errorConsumer = errorConsumer;

        this.pane = new Pane();
        this.mousePositionOnRouteProperty = new SimpleDoubleProperty();

        // todo : c bon là ?
        MapViewParameters mapViewParameters =
                new MapViewParameters(12, 543200, 370650);
        ObjectProperty<MapViewParameters> mvp =
                new SimpleObjectProperty<>(mapViewParameters);

        ObservableList<Waypoint> waypoints = FXCollections.observableArrayList();

        RouteManager routeManager = new RouteManager(route, mvp, errorConsumer);
        WaypointsManager waypointsManager = new WaypointsManager(graph, mvp, waypoints, errorConsumer);
        BaseMapManager mapManager = new BaseMapManager(tileManager, waypointsManager, mvp);

        StackPane totalPane = new StackPane();
        totalPane.getStylesheets().add("map.css");
        Pane mapPane = mapManager.pane();
        Pane routePane = routeManager.pane();
        Pane waypointsPane = waypointsManager.pane();

        totalPane.getChildren().addAll(mapPane, routePane, waypointsPane);
    }

    public Pane pane() {
        return this.pane;
    }

    public ReadOnlyDoubleProperty mousePositionOnRouteProperty() {
        return mousePositionOnRouteProperty;
    }
}
