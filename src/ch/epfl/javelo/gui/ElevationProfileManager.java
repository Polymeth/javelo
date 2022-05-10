package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

import java.awt.*;

public final class ElevationProfileManager {
    private final ReadOnlyObjectProperty<ElevationProfile> profile;
    private final ReadOnlyDoubleProperty mousePositionOnProfile;

    private final BorderPane borderPane;

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profile, ReadOnlyDoubleProperty mousePositionOnProfile) {
        this.profile = profile;
        this.mousePositionOnProfile = mousePositionOnProfile;

        borderPane = new BorderPane();
        borderPane.getStylesheets().add("elevation_profile.css");

        // bottom
        Text statText = new Text();
        statText.setText(String.format("Longueur : %.1f km" +
                        "     Montée : %.0f m" +
                        "     Descente : %.0f m" +
                        "     Altitude : de %.0f m à %.0f m",
                profile.get().length(),
                profile.get().totalAscent(),
                profile.get().totalDescent(),
                profile.get().maxElevation(),
                profile.get().minElevation()));

        VBox vbox = new VBox(statText);
        vbox.setId("profile_data");
        borderPane.setBottom(vbox);

        // center
        Text test1 = new Text("0m");
        Text test2 = new Text("100m");

        test1.getStyleClass().addAll("grid_label", "horizontal");
        test2.getStyleClass().addAll("grid_label", "vertical");

        Group gridTexGroup = new Group();
        gridTexGroup.getChildren().addAll(test1, test2);

        Path path = new Path();
        path.setId("grid");

        Polygon graph = new Polygon();
        graph.setId("profile");

        Line highlightedPoint = new Line();

        Pane pane = new Pane();
        pane.getChildren().addAll(path, gridTexGroup, highlightedPoint, graph);
        Insets insets = new Insets(10, 10, 20, 40);
        borderPane.setCenter(pane);

    }


    public Pane pane() {
        return this.borderPane;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return this.mousePositionOnProfile;
    }
}
