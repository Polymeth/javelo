package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.WebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.*;

import java.awt.*;
import java.io.IOException;

public final class BaseMapManager {
    private final TileManager tileManager;
    private final ObjectProperty<MapViewParameters> property;
    private boolean redrawNeeded;

    private final Pane pane;
    private final Canvas canvas;

    public BaseMapManager(TileManager tileManager, ObjectProperty<MapViewParameters> property) {
        this.tileManager = tileManager;
        this.property = property;
        this.redrawNeeded = true;

        pane = new Pane();
        canvas = new Canvas();
        pane.getChildren().add(canvas);
        pane.setPrefSize(600, 300);
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        canvas.widthProperty().addListener(o -> System.out.printf("New canvas width: %.2f\n", canvas.getWidth()));
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        System.out.println("canvas size: " + canvas.getWidth());
        redrawOnNextPulse();
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        //redrawNeeded = false;
        pane();
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    public Pane pane() {
        GraphicsContext context = canvas.getGraphicsContext2D();
        try {
            Point2D topLeft = property.get().topLeft();
            Point2D bottomLeft = new Point2D(property.get().topLeft().getX(),
                    property.get().topLeft().getY() + canvas.getHeight());
            Point2D topRight = new Point2D(property.get().topLeft().getX() + canvas.getWidth(),
                    property.get().topLeft().getY());
            Point2D bottomRight = new Point2D(property.get().topLeft().getX() + canvas.getWidth(),
                    property.get().topLeft().getY() + canvas.getHeight());

            for(int y = (int)topLeft.getY()/256; y <= (int)bottomLeft.getY()/256; y++) {
                for(int x = (int)topLeft.getX()/256; x <= (int)topRight.getX()/256; x++) {
                   // System.out.println(x + "; " + y);
                    TileManager.TileId toDraw = new TileManager.TileId(12, x, y);
                    int imageX = x*256 - (int)topLeft.getX();
                    int imageY = y*256 - (int)topLeft.getY();
                    context.drawImage(tileManager.imageForTileAt(toDraw), imageX, imageY);
                }
            }
          //  System.out.println("------");



           // System.out.println(topLeft.getX() + " " + topLeft.getY());

            TileManager.TileId toDraw = new TileManager.TileId(12, 0, 0);
            //context.drawImage(tileManager.imageForTileAt(toDraw), 0, 0);


        } catch (IOException e) {
            System.out.println("error");
        }
        return this.pane;
    }
}
