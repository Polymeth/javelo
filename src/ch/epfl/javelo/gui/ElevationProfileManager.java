package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

import java.util.ArrayList;
import java.util.List;

public final class ElevationProfileManager {
    private static final int TOP_AND_RIGHT_MARGIN = 10;
    private static final int BOTTOM_MARGIN = 20;
    private static final int LEFT_MARGIN = 40;

    private final BorderPane borderPane;
    private final ObjectProperty<Rectangle2D> profileRectangle;
    private final Polygon graph;
    private final Pane pane;
    private final Path path;
    private final Group gridTexGroup;
    private final Insets insets;
    private final Line highlightedLine;
    private final Text statText;

    private final ObjectProperty<Affine> screenToWorld;
    private final ObjectProperty<Affine> worldToScreen;
    private final ReadOnlyObjectProperty<ElevationProfile> profile;

    private final ReadOnlyDoubleProperty highlightedPositionProperty;
    private final DoubleProperty mousePositionOnProfileProperty;
    private final DoubleProperty lastProfileLength;

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profile, ReadOnlyDoubleProperty highlightedPositionProperty) throws NonInvertibleTransformException {
        this.profile = profile;
        this.highlightedPositionProperty = highlightedPositionProperty;
        this.mousePositionOnProfileProperty = new SimpleDoubleProperty(Double.NaN);
        this.lastProfileLength = new SimpleDoubleProperty(Double.NaN);
        insets = new Insets(TOP_AND_RIGHT_MARGIN, TOP_AND_RIGHT_MARGIN, BOTTOM_MARGIN, LEFT_MARGIN);

        borderPane = new BorderPane();
        borderPane.getStylesheets().add("elevation_profile.css");

        // bottom pane
        statText = new Text();
        worldToScreen = new SimpleObjectProperty<>(new Affine());
        screenToWorld = new SimpleObjectProperty<>(new Affine());
        VBox vbox = new VBox(statText);
        vbox.setId("profile_data");
        borderPane.setBottom(vbox);

        // center pane
        pane = new Pane();
        gridTexGroup = new Group();
        path = new Path();
        path.setId("grid");
        graph = new Polygon();
        graph.setId("profile");
        highlightedLine = new Line();

        pane.getChildren().addAll(graph, path, gridTexGroup, highlightedLine);

        // profile drawing
        profileRectangle = new SimpleObjectProperty<>();
        profileRectangle.set(Rectangle2D.EMPTY);

        createTransformations();
        createBindings();

        profileRectangle.bind(Bindings.createObjectBinding(() -> {
            double width = pane.getWidth() - insets.getLeft() - insets.getRight();
            double height = pane.getHeight() - insets.getTop() - insets.getBottom();

            if (width < 0) width = 0;
            if (height < 0) height = 0;

            return new Rectangle2D(insets.getLeft(), insets.getTop(), width, height);
        }, pane.widthProperty(), pane.heightProperty()));

        pane.setOnMouseMoved(e -> {
            Point2D pos = new Point2D(e.getX(), e.getY());
            if (profileRectangle.get().contains(pos)) {
                mousePositionOnProfileProperty.set(screenToWorld.get().transform(e.getX(), 0).getX());
            } else {
                mousePositionOnProfileProperty.set(Double.NaN);
            }
        });

        // mouse exit
        pane.setOnMouseExited(e -> mousePositionOnProfileProperty.set(Double.NaN));

        pane.widthProperty().addListener(e -> {
            try {
                redrawCompleteProfile();
            } catch (NonInvertibleTransformException ex) {
                ex.printStackTrace();
            }
        });

        pane.heightProperty().addListener(e -> {
            try {
                redrawCompleteProfile();
            } catch (NonInvertibleTransformException ex) {
                ex.printStackTrace();
            }
        });

        profile.addListener(e -> {
            // not recreate the grid if it doesn't already exists`
            if (profile.get() != null) {
                if (profile.get().length() == lastProfileLength.get()
                        || Double.isNaN(lastProfileLength.get())
                        || Double.isNaN(profile.get().length())) {
                    lastProfileLength.set(profile.get().length());
                    return;
                }
            }

            try {
                redrawCompleteProfile();
            } catch (NonInvertibleTransformException ex) {
                ex.printStackTrace();
            }
        });

        borderPane.setCenter(pane);
    }

    private void createBindings() {
        highlightedLine.layoutXProperty().bind(Bindings.createDoubleBinding(() ->
                        worldToScreen.get().transform(highlightedPositionProperty.get(), 0).getX(),
                highlightedPositionProperty
        ));
        highlightedLine.startYProperty().bind(Bindings.select(profileRectangle, "minY"));
        highlightedLine.endYProperty().bind(Bindings.select(profileRectangle, "maxY"));
        highlightedLine.visibleProperty().bind(Bindings.greaterThanOrEqual(highlightedPositionProperty, 0));
    }

    private void createTransformations() throws NonInvertibleTransformException {
        if (profile.get() != null) {
            Affine affine = new Affine();

            affine.prependTranslation(-profileRectangle.get().getMinX(), -profileRectangle.get().getMinY());
            affine.prependScale(profile.get().length() / (profileRectangle.get().getMaxX() - profileRectangle.get().getMinX()),
                    (profile.get().minElevation() - profile.get().maxElevation()) / (profileRectangle.get().getMaxY() - profileRectangle.get().getMinY()));
            affine.prependTranslation(0, profile.get().maxElevation());

            screenToWorld.set(affine);
            worldToScreen.set(affine.createInverse());
            System.out.println("seted");
        }
    }

    private void createGrid() throws NonInvertibleTransformException {
        path.getElements().clear();
        if (profile.get() != null) {
            path.getElements().clear();
            gridTexGroup.getChildren().clear();
            int[] POS_STEPS =
                    {1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000};
            int[] ELE_STEPS =
                    {5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000};

            /** VERTICAL LINES **/
            //double steps = Math.ceil(profileRectangle.get().getWidth() / 50);

            int selectedStep = 1000;
            double distanceBetween = 0;

            for (int step : POS_STEPS) {
                distanceBetween = Math.abs(worldToScreen.get().deltaTransform(step, 0).getX());
                if (distanceBetween >= 50) {
                    selectedStep = step;
                    break;
                }
            }

            double steps = Math.ceil(profile.get().length() / distanceBetween);

            for (int i = 0; i < steps; i++) {
                MoveTo move = new MoveTo();
                LineTo line = new LineTo();

                Text label = new Text(Double.toString(Math2.ceilDiv(i * selectedStep, 1000)));
                label.textOriginProperty().set(VPos.TOP);
                label.setFont(Font.font("Avenir", 10));
                label.getStyleClass().addAll("grid_label", "vertical");

                // System.out.println((i * selectedStep));
                label.setX(profileRectangle.get().getMinX() + i * distanceBetween - 0.5 * label.prefWidth(0));
                label.setY(profileRectangle.get().getMaxY());

                line.setX(profileRectangle.get().getMinX() + i * distanceBetween);
                line.setY(profileRectangle.get().getMaxY());
                move.setX(profileRectangle.get().getMinX() + i * distanceBetween);
                move.setY(profileRectangle.get().getMinY());


                gridTexGroup.getChildren().add(label);
                path.getElements().addAll(move, line);
            }

            /** HORIZONTAL LINES **/
            // todo changer cette boucle wtf
            int selectedStepH = 5;
            double distanceBetweenH = 0;
            for (int step : ELE_STEPS) {
                distanceBetweenH = Math.abs(worldToScreen.get().deltaTransform(0, step).getY());
                if (distanceBetweenH >= 25) {
                    selectedStepH = step;
                    break;
                }
            }


            double stepH = (profile.get().maxElevation() - profile.get().minElevation()) / selectedStepH;

            for (int i = 0; i < stepH; i++) {
                MoveTo move = new MoveTo();
                LineTo line = new LineTo();

                int d = (int) profile.get().minElevation() / selectedStepH;
                double diffBetweenSteppedValue = profile.get().minElevation() - d * selectedStepH;
                double firstLineHeight = Math.abs(worldToScreen.get().deltaTransform(0, diffBetweenSteppedValue).getY());
                double firstDisplayedHeight = profile.get().minElevation() - diffBetweenSteppedValue;

                double y = profileRectangle.get().getMaxY() - firstLineHeight - i * distanceBetweenH;
                double meters = (i + 1) * selectedStepH + firstDisplayedHeight;

                Text label = new Text(Integer.toString((int) meters));
                label.textOriginProperty().set(VPos.TOP);
                label.setFont(Font.font("Avenir", 10));
                label.getStyleClass().addAll("grid_label", "horizontal");

                label.setX(profileRectangle.get().getMinX() - label.prefWidth(0) - 2);
                label.setY(y - 0.5 * label.prefWidth(0)); // todo fix the position (not centered)

                move.setY(y);
                move.setX(profileRectangle.get().getMinX());
                line.setY(y);
                line.setX(profileRectangle.get().getMaxX());

                path.getElements().addAll(move, line);
                gridTexGroup.getChildren().add(label);
            }
        }
    }

    private void drawElevations() throws NonInvertibleTransformException {
        if (profile.get() != null) {
            graph.getPoints().clear();
            List<Double> pointsToAdd = new ArrayList<>();

            pointsToAdd.add(profileRectangle.get().getMinX());
            pointsToAdd.add(profileRectangle.get().getMaxY());

            for (int x = (int) profileRectangle.get().getMinX(); x < profileRectangle.get().getMaxX(); x++) {
                Point2D height = screenToWorld.get().transform(x, 0);

                Point2D transform = worldToScreen.get().transform(0, profile.get().elevationAt(height.getX()));
                pointsToAdd.add((double) x);
                pointsToAdd.add(transform.getY());
            }
            pointsToAdd.add(profileRectangle.get().getMaxX());
            pointsToAdd.add(profileRectangle.get().getMaxY());

            graph.getPoints().addAll(pointsToAdd);
        }
    }

    private void drawStatistics() {
        if (profile.get() != null) {
            statText.setText(String.format("Longueur : %.1f km" +
                            "     Montée : %.0f m" +
                            "     Descente : %.0f m" +
                            "     Altitude : de %.0f m à %.0f m",
                    profile.get().length() / 1000,
                    profile.get().totalAscent(),
                    profile.get().totalDescent(),
                    profile.get().minElevation(),
                    profile.get().maxElevation()));
        }
    }

    public void redrawCompleteProfile() throws NonInvertibleTransformException {
        createTransformations();
        drawElevations();
        createGrid();
        drawStatistics();
    }

    public Pane pane() {
        return this.borderPane;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mousePositionOnProfileProperty;
    }
}
