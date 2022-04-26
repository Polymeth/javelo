package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.*;
import java.io.IOException;

public final class BaseMapManager {
    private final TileManager tileManager;
    private final WaypointsManager waypointsManager;
    private final ObjectProperty<MapViewParameters> property;
    private boolean redrawNeeded;

    private final Pane pane;
    private final Canvas canvas;

    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, ObjectProperty<MapViewParameters> property) {
        this.tileManager = tileManager;
        this.property = property;
        this.redrawNeeded = true;
        this.waypointsManager = waypointsManager;

        pane = new Pane();
        canvas = new Canvas();
        pane.getChildren().add(canvas);
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        SimpleLongProperty minScrollTime = new SimpleLongProperty();

        pane.setOnMouseClicked(e -> System.out.println("clicking mdr"));

        pane.setOnScroll(e -> {
            // trackpad compatible zoom
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 250);
            double zoomDelta = Math.signum(e.getDeltaY());
            int oldZoom = property.get().zoomlevel();

            int newZoom = (int)Math2.clamp(8, this.property.get().zoomlevel() + zoomDelta, 19);
            System.out.println(newZoom);
            double multiplier = (oldZoom < newZoom) ? 2 : 0.5;

            //double distanceFromTopLeft =

            this.property.set(new MapViewParameters(newZoom,
                    property.get().originXcoord()*multiplier,
                    property.get().originYcoord()*multiplier));
            redrawOnNextPulse();
        });

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        redrawOnNextPulse();
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        //redrawNeeded = false;
        GraphicsContext context = canvas.getGraphicsContext2D();
        try {
            Point2D topLeft = property.get().topLeft();
            Point2D bottomRight = new Point2D(property.get().topLeft().getX() + canvas.getWidth(),
                    property.get().topLeft().getY() + canvas.getHeight());

            for(int y = (int)topLeft.getY()/256; y <= (int)bottomRight.getY()/256; y++) {
                for(int x = (int)topLeft.getX()/256; x <= (int)bottomRight.getX()/256; x++) {
                    //System.out.println("zoom " + property.get().zoomlevel() + ", x: " + x + ", y: " + y);
                    TileManager.TileId toDraw = new TileManager.TileId(property.get().zoomlevel(), x, y);

                    int imageX = x*256 - (int)topLeft.getX();
                    int imageY = y*256 - (int)topLeft.getY();
                    context.drawImage(tileManager.imageForTileAt(toDraw), imageX, imageY);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    public Pane pane() {
        waypointsManager.pane().eventDispatcherProperty().bind(this.pane.eventDispatcherProperty());
        return this.pane;
    }

}
