package ch.epfl.javelo.gui;

import javafx.scene.layout.Pane;

public final class ErrorManager {

    private final Pane pane;

    public ErrorManager() {
        this.pane = new Pane();
    }

    public Pane pane() {
        return this.pane;
    }

    public void displayError(String error) {

    }

}
