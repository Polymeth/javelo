package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.*;

import java.io.IOException;

/**
 * The system to display a map
 *
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class BaseMapManager {
    private static final int TILE_SIZE = 256;
    private static final int MIN_ZOOM = 9;
    private static final int MAX_ZOOM = 19;
    private static final int SCROLL_OFFSET = 250;

    private final TileManager tileManager;
    private final ObjectProperty<MapViewParameters> property;
    private boolean redrawNeeded;

    private final Pane pane;
    private final Canvas canvas;

    /**
     * Create a new map display system
     *
     * @param tileManager      the tile manager, usually to use a cache system
     * @param waypointsManager the waypoints manager
     * @param property         a property contain a map view parameter
     */
    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, ObjectProperty<MapViewParameters> property) {
        this.tileManager = tileManager;
        this.property = property;
        this.redrawNeeded = true;

        pane = new Pane();
        canvas = new Canvas();
        pane.getChildren().add(canvas);
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        // mouse click
        Property<Point2D> pressedPosition = new SimpleObjectProperty<>();
        pane.setOnMouseClicked(e -> {
            if (e.isStillSincePress()) {
                waypointsManager.addWaypoint(
                        property.get().originXcoord() + e.getX(),
                        property.get().originYcoord() + e.getY());
            }
        });
        pane.setOnMousePressed(e -> pressedPosition.setValue(new Point2D(e.getX(), e.getY())));

        // mouse drag
        pane.setOnMouseDragged(e -> {
            if (!e.isStillSincePress()) {
                MapViewParameters mvp = property.get().withMinXY(
                        property.get().originXcoord() + (pressedPosition.getValue().getX() - e.getX()),
                        property.get().originYcoord() + (pressedPosition.getValue().getY() - e.getY())
                );
                property.set(mvp);
                pressedPosition.setValue(new Point2D(e.getX(), e.getY()));
                redrawOnNextPulse();
            }
        });

        // mouse scroll
        SimpleLongProperty minScrollTime = new SimpleLongProperty();
        pane.setOnScroll(e -> {
            // trackpad compatible zoom
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + SCROLL_OFFSET);
            double zoomDelta = Math.signum(e.getDeltaY());

            int currentZoom = property.get().zoomlevel();
            int newZoom = (int) Math2.clamp(MIN_ZOOM, this.property.get().zoomlevel() + zoomDelta, MAX_ZOOM);

            if (currentZoom != newZoom) {
                double mouseX = property.get().pointAt(e.getX(), e.getY()).xAtZoomLevel(newZoom);
                double mouseY = property.get().pointAt(e.getX(), e.getY()).yAtZoomLevel(newZoom);
                double diffX = mouseX - e.getX();
                double diffY = mouseY - e.getY();

                this.property.set(new MapViewParameters(newZoom,
                        diffX,
                        diffY
                ));
                redrawOnNextPulse();
            }
        });

        // canvas redrawing if changing
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        canvas.heightProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            redrawOnNextPulse();
        });
        canvas.widthProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            redrawOnNextPulse();
        });

        redrawOnNextPulse();
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        GraphicsContext context = canvas.getGraphicsContext2D();
        try {
            Point2D topLeft = property.get().topLeft();
            Point2D bottomRight = new Point2D(property.get().topLeft().getX() + canvas.getWidth(),
                    property.get().topLeft().getY() + canvas.getHeight());

            for (int y = (int) topLeft.getY() / TILE_SIZE; y <= (int) bottomRight.getY() / TILE_SIZE; y++) {
                for (int x = (int) topLeft.getX() / TILE_SIZE; x <= (int) bottomRight.getX() / TILE_SIZE; x++) {
                    TileManager.TileId toDraw = new TileManager.TileId(property.get().zoomlevel(), x, y);
                    int imageX = x * TILE_SIZE - (int) topLeft.getX();
                    int imageY = y * TILE_SIZE - (int) topLeft.getY();
                    context.drawImage(tileManager.imageForTileAt(toDraw), imageX, imageY);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @return a pane with the map
     */
    public Pane pane() {
        return this.pane;
    }

    /**
     * Redraw the whole pane on next jfx pulse
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

}
