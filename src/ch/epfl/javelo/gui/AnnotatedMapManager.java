package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.Stack;
import java.util.function.Consumer;

public final class AnnotatedMapManager {
    private final Graph routeGraph;
    private final TileManager tileManager;
    private final RouteBean route;
    private final Consumer<String> errorConsumer;

    private final StackPane pane;
    private final DoubleProperty mousePositionOnRouteProperty;

    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean route, Consumer<String> errorConsumer) {
        this.routeGraph = graph;
        this.tileManager = tileManager;
        this.route = route;
        this.errorConsumer = errorConsumer;
        this.mousePositionOnRouteProperty = new SimpleDoubleProperty();

        // todo : c bon là ?
        MapViewParameters mapViewParameters =
                new MapViewParameters(12, 543200, 370650);
        ObjectProperty<MapViewParameters> mvp =
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
        pane.getChildren().add(mapPane);
        pane.getChildren().add(routePane);
        pane.getChildren().add(waypointsPane);
        pane.getStylesheets().add("map.css");
    }

    public Pane pane() {
        return this.pane;
    }

    public ReadOnlyDoubleProperty mousePositionOnRouteProperty() {
        return mousePositionOnRouteProperty;
    }
}
