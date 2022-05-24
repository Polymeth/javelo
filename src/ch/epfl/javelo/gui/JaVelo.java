package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.function.Consumer;

public final class JaVelo extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("javelo-data").resolve("ch_west"));
        TileManager tileManager = new TileManager(Path.of("osm-cache"), "tile.openstreetmap.org");
        CostFunction costFunction = new CityBikeCF(graph);
        ErrorManager errorManager = new ErrorManager();

        RouteComputer rc = new RouteComputer(graph, costFunction);
        RouteBean routeBean = new RouteBean(rc);

        AnnotatedMapManager completeManager =
                new AnnotatedMapManager(graph, tileManager, routeBean, errorManager::displayError);

        DoubleProperty highlightProperty =
                new SimpleDoubleProperty(1500);


        // todo modifier?
        //highlightProperty.bind(
          //    profileManager.mousePositionOnProfileProperty());

        StackPane finalPane = new StackPane();
        BorderPane completePane = new BorderPane();
        completePane.setPrefSize(800, 600);

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.getItems().add(completeManager.pane());

        completePane.setCenter(splitPane);

        //completePane.setTop(errorManager.pane());

        ElevationProfileManager elevationProfileManager =
                new ElevationProfileManager(routeBean.elevationProfile(), routeBean.highlightedPositionProperty());

        MenuBar gpxMenubar = new MenuBar();
        Menu folderButton = new Menu("Fichier");
        MenuItem generateButton = new MenuItem("Exporter GPX");

        folderButton.getItems().add(generateButton);
        gpxMenubar.getMenus().add(folderButton);

        completePane.setTop(gpxMenubar);

        Pane profilePane = elevationProfileManager.pane();

        routeBean.getWaypoints().addListener((ListChangeListener<Waypoint>) e -> {
            splitPane.getItems().remove(profilePane);
            if  (routeBean.getWaypoints().size() >= 2) {
                splitPane.getItems().add(profilePane);
            }
        });

        finalPane.getChildren().addAll(completePane, errorManager.pane());
       // finalPane.getChildren().add(completePane);

        //Highlighted Position Binding
        routeBean.highlightedPositionProperty().bind(Bindings.createDoubleBinding(() -> {
            if (completeManager.mousePositionOnRouteProperty().get() >0){
                return completeManager.mousePositionOnRouteProperty().get();
            } else{
                return elevationProfileManager.mousePositionOnProfileProperty().get();
            }
        }, completeManager.mousePositionOnRouteProperty(), elevationProfileManager.mousePositionOnProfileProperty()));

        primaryStage.setTitle("JaVelo");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(finalPane));
        primaryStage.show();
    }
}
