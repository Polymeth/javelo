package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

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
    private final Path path;

    private final ObjectProperty<Affine> screenToWorld = new SimpleObjectProperty<>();
    private final ObjectProperty<Affine> worldToScreen = new SimpleObjectProperty<>();

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
                profile.get().length()/1000,
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

        path = new Path();
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

            return new Rectangle2D(40, 10, width, height);
        }, pane.widthProperty(), pane.heightProperty()));

        profileRectangle.addListener(e -> {
            System.out.println("mddr");
            try {
                drawElevations();
                createGrid();
                System.out.println("redrawing");
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

        screenToWorld.set(affineScreenToWorld(profileRectangle.get()));
        worldToScreen.set(affineScreenToWorld(profileRectangle.get()).createInverse());

        pointsToAdd.add(profileRectangle.get().getMinX());
        pointsToAdd.add(profileRectangle.get().getMaxY());

        for (int x = (int)profileRectangle.get().getMinX(); x < profileRectangle.get().getMaxX(); x++) {
            Point2D height = screenToWorld.get().transform(x, 0);

            Point2D transform = worldToScreen.get().transform(0, profile.get().elevationAt(height.getX()));
            pointsToAdd.add((double)x);
            pointsToAdd.add(transform.getY());

            //System.out.println(x + ", " + transform.getY());
        }

        pointsToAdd.add(profileRectangle.get().getMaxX());
        pointsToAdd.add(profileRectangle.get().getMaxY());


        graph.getPoints().addAll(pointsToAdd);
    }

    private void createGrid(){
        path.getElements().clear();
        int[] POS_STEPS =
                { 1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000 };
        int[] ELE_STEPS =
                { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };

        /** VERTICAL LINES **/
        double steps = Math.ceil(profileRectangle.get().getWidth() / 50);

        double distanceBetween = 0;
        for (int step : POS_STEPS) {
            distanceBetween = worldToScreen.get().deltaTransform(step, 0).getX();
            if (distanceBetween >= 50) {
                break;
            }
        }

        for (int i = 0; i < steps; i++){
            MoveTo move = new MoveTo();
            LineTo line = new LineTo();
            move.setX(profileRectangle.get().getMinX() + i*distanceBetween);
            move.setY(profileRectangle.get().getMinY());
            line.setX(profileRectangle.get().getMinX() + i*distanceBetween);
            line.setY(profileRectangle.get().getMaxY());

            path.getElements().addAll(move, line);
        }

        /** HORIZONTAL LINES **/
        double stepsH = Math.ceil(profileRectangle.get().getHeight() / 25); // todo 25
        double distanceH = (profile.get().maxElevation() - profile.get().minElevation()) / stepsH;

        int selectedStepH = 5;
        double distanceBetweenH = 0;
        for (int step : ELE_STEPS) {
            distanceBetweenH = Math.abs(worldToScreen.get().deltaTransform(0, step).getY());
            if (distanceBetweenH >= 25) {
                selectedStepH = step;
                break;
            }
        }

        for (int i = 0; i < stepsH; i++){
            MoveTo move = new MoveTo();
            LineTo line = new LineTo();

            int d = (int)profile.get().minElevation() / selectedStepH;
            double length = profile.get().minElevation() - d*selectedStepH;
            double firstLine = Math.abs(worldToScreen.get().deltaTransform(0, length).getY());

            move.setY(profileRectangle.get().getMaxY() - firstLine - i*distanceBetweenH);
            move.setX(profileRectangle.get().getMinX());
            line.setY(profileRectangle.get().getMaxY() - firstLine - i*distanceBetweenH);
            line.setX(profileRectangle.get().getMaxX());
            /*if (i == 1) {
                move.setY(profileRectangle.get().getMaxY() - firstLine);
                move.setX(profileRectangle.get().getMinX());
                line.setY(profileRectangle.get().getMaxY() - firstLine);
                line.setX(profileRectangle.get().getMaxX());
            } else {
               /*move.setY(profileRectangle.get().getMaxY() - i*distanceBetweenH);
                move.setX(profileRectangle.get().getMinX());
                line.setY(profileRectangle.get().getMaxY() - i*distanceBetweenH);
                line.setX(profileRectangle.get().getMaxX());
            }*/

            path.getElements().addAll(move, line);
        }

    }

    public Pane pane() {
        return this.borderPane;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return this.mousePositionOnProfile;
    }
}
