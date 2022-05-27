package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.CostFunction;
import ch.epfl.javelo.routing.GpxGenerator;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;

/**
 * The main program
 *
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class JaVelo extends Application {

    /**
     * Launches the JavaFX application
     *
     * @param args program arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Start and initiliaze the JavaFX application & scene
     *
     * @param primaryStage JFX primary stage
     * @throws Exception if the JFX stage fails to display
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        TileManager tileManager = new TileManager(Path.of("osm-cache"), "tile.openstreetmap.org");
        CostFunction costFunction = new CityBikeCF(graph);
        ErrorManager errorManager = new ErrorManager();

        RouteComputer rc = new RouteComputer(graph, costFunction);
        RouteBean routeBean = new RouteBean(rc);
        DoubleProperty highlightedPositionProperty = new SimpleDoubleProperty();

        AnnotatedMapManager completeManager =
                new AnnotatedMapManager(graph, tileManager, routeBean, errorManager::displayError);
        ElevationProfileManager elevationProfileManager =
                new ElevationProfileManager(routeBean.elevationProfile(), highlightedPositionProperty);

        StackPane finalPane = new StackPane();
        BorderPane completePane = new BorderPane();
        completePane.setPrefSize(800, 600);

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.getItems().add(completeManager.pane());

        completePane.setCenter(splitPane);

        // export button
        MenuBar gpxMenubar = new MenuBar();
        Menu folderButton = new Menu("Fichier");
        MenuItem generateButton = new MenuItem("Exporter GPX");
        generateButton.disableProperty().bind(routeBean.route().isNull());
        generateButton.setOnAction(e -> {
            try {
                GpxGenerator.writeGpx("javelo.gpx", routeBean.route().get(), routeBean.elevationProfile().get());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        folderButton.getItems().add(generateButton);
        gpxMenubar.getMenus().add(folderButton);
        completePane.setTop(gpxMenubar);

        Pane profilePane = elevationProfileManager.pane();

        routeBean.getWaypoints().addListener((ListChangeListener<Waypoint>) e -> {
            splitPane.getItems().remove(profilePane);
            if (routeBean.elevationProfile().get() != null && routeBean.route().get() != null) {
                splitPane.getItems().add(profilePane);
            }
        });
        finalPane.getChildren().addAll(completePane, errorManager.pane());

        // bindings
        DoubleBinding highlightedPosBind = Bindings.createDoubleBinding(() -> {
            if (completeManager.mousePositionOnRouteProperty().get() >= 0) {
                return completeManager.mousePositionOnRouteProperty().get();
            } else {
                return elevationProfileManager.mousePositionOnProfileProperty().get();
            }
        }, completeManager.mousePositionOnRouteProperty(), elevationProfileManager.mousePositionOnProfileProperty());

        highlightedPositionProperty.bind(highlightedPosBind);
        routeBean.highlightedPositionProperty().bind(highlightedPositionProperty);

        // scene creation
        primaryStage.setTitle("JaVelo");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(finalPane));
        primaryStage.show();
    }
}
