package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class ElevationProfileManager {
    private final ReadOnlyObjectProperty<ElevationProfile> profile;
    private final ReadOnlyDoubleProperty mousePositionOnProfile;

    private final BorderPane borderPane;
    private final ObjectProperty<Rectangle2D> profileRectangle = new SimpleObjectProperty<>();
    private final Polygon graph;
    private final Pane pane;

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profile, ReadOnlyDoubleProperty mousePositionOnProfile) throws NonInvertibleTransformException {
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
                profile.get().minElevation(),
                profile.get().maxElevation()));

        VBox vbox = new VBox(statText);
        vbox.setId("profile_data");
        borderPane.setBottom(vbox);

        // center
        pane = new Pane();
        Insets insets = new Insets(10, 40, 20, 10);

        Text test1 = new Text("0m");
        Text test2 = new Text("100m");

        test1.getStyleClass().addAll("grid_label", "horizontal");
        test2.getStyleClass().addAll("grid_label", "vertical");

        Group gridTexGroup = new Group();
        gridTexGroup.getChildren().addAll(test1, test2);

        Path path = new Path();
        path.setId("grid");

        graph = new Polygon();
        graph.setId("profile");

        Line highlightedPoint = new Line();
        pane.getChildren().addAll(path, gridTexGroup, highlightedPoint, graph);

        // profile drawing
        profileRectangle.set(Rectangle2D.EMPTY);

        profileRectangle.bind(Bindings.createObjectBinding(() -> {
            double width = pane.getWidth() - insets.left - insets.right;
            double height = pane.getHeight() - insets.top - insets.bottom;

            if (width < 0) width = 0;
            if (height < 0) height = 0;
            System.out.println("lol");
            return new Rectangle2D(40, 10, width, height);
        }, pane.widthProperty(), pane.heightProperty()));

        profileRectangle.addListener(e -> {
            try {
                drawElevations();
            } catch (NonInvertibleTransformException ex) {
                ex.printStackTrace();
            }
        });

        borderPane.setCenter(pane);
    }

    private Affine affineScreenToWorld(Rectangle2D rect){
        Affine affine = new Affine();

        affine.prependTranslation(-rect.getMinX(), -rect.getMinY());
        affine.prependScale( profile.get().length() / (rect.getMaxX() - rect.getMinX()),
                (profile.get().minElevation()-profile.get().maxElevation()) / (rect.getMaxY() - rect.getMinY()));
        affine.prependTranslation(0, profile.get().maxElevation());
        return affine;
    }

    private void drawElevations() throws NonInvertibleTransformException {
        graph.getPoints().clear();
        List<Double> pointsToAdd = new ArrayList<>();

        Affine screenToWorld = affineScreenToWorld(profileRectangle.get());
        Affine worldToScreen = screenToWorld.createInverse();

        for (int x = (int)profileRectangle.get().getMinX(); x < profileRectangle.get().getMaxX(); x++) {
            Point2D height = screenToWorld.transform(x, 0);

            Point2D transform = worldToScreen.transform(x, profile.get().elevationAt(height.getX()));
            pointsToAdd.add((double)x);
            pointsToAdd.add(transform.getY());

            System.out.println(x + ", " + transform.getY());
        }

        pointsToAdd.add(profileRectangle.get().getMaxX());
        pointsToAdd.add(profileRectangle.get().getMaxY());

        pointsToAdd.add(profileRectangle.get().getMinX());
        pointsToAdd.add(profileRectangle.get().getMaxY());

        graph.getPoints().addAll(pointsToAdd);
    }


    public Pane pane() {
        return this.borderPane;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return this.mousePositionOnProfile;
    }
}
