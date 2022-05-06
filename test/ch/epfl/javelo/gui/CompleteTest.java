package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.gui.*;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.CostFunction;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.function.Consumer;

import static javafx.application.Application.launch;

public final class CompleteTest extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        Path cacheBasePath = Path.of("cache");
        String tileServerHost = "tile.openstreetmap.org";
        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);

        MapViewParameters mapViewParameters =
                new MapViewParameters(12, 543200, 370650);
        ObjectProperty<MapViewParameters> mapViewParametersP =
                new SimpleObjectProperty<>(mapViewParameters);
        ObservableList<Waypoint> waypoints =
                FXCollections.observableArrayList(
                        new Waypoint(new PointCh(2532697, 1152350), 159049),
                        new Waypoint(new PointCh(2538659, 1154350), 117669));
        Consumer<String> errorConsumer = new ErrorConsumer();

        // etape 9
        CostFunction CF = new CityBikeCF(graph);
        RouteComputer rc = new RouteComputer(graph, CF);
        RouteBean routeBean = new RouteBean(rc);
        routeBean.getWaypoints().addAll(waypoints);

       // routeBean.waypoints = waypoints;
        RouteManager manager = new RouteManager(routeBean, mapViewParametersP, errorConsumer);

        routeBean.highlightedPositionProperty().set(100);

        WaypointsManager waypointsManager =
                new WaypointsManager(graph,
                        mapViewParametersP,
                        routeBean.getWaypoints(),
                        errorConsumer);
        BaseMapManager baseMapManager =
                new BaseMapManager(tileManager,
                        waypointsManager,
                        mapViewParametersP);

        StackPane mainPane =
                new StackPane(baseMapManager.pane(),
                        waypointsManager.pane(), manager.pane());
        mainPane.getStylesheets().add("map.css");
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(300);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }

    private static final class ErrorConsumer
            implements Consumer<String> {
        @Override
        public void accept(String s) { System.out.println(s); }
    }
}