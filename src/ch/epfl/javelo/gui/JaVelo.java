package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.CostFunction;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.function.Consumer;

public final class JaVelo extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("javelo-data").resolve("ch_west"));
        TileManager tileManager = new TileManager(Path.of("osm-cache"), "tile.openstreetmap.org");
        CostFunction costFunction = new CityBikeCF(graph);
        //Consumer<String> errorConsumer = e -> "lol";

        RouteComputer rc = new RouteComputer(graph, costFunction);
        RouteBean routeBean = new RouteBean(rc);

        AnnotatedMapManager completeManager = new AnnotatedMapManager(graph, tileManager, routeBean, System.out::println);

        BorderPane completePane = new BorderPane();

        primaryStage.setMinWidth(600);
        primaryStage.setTitle("JaVelo");
        primaryStage.setMinHeight(300);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }
}
