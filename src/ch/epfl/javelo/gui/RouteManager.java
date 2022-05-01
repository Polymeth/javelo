package ch.epfl.javelo.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;

import java.util.function.Consumer;

public final class RouteManager {

    private final ReadOnlyObjectProperty<MapViewParameters> property;
    private Consumer<String> error;
    private RouteBean bean;
    private final Pane pane;

    public RouteManager(RouteBean bean, ReadOnlyObjectProperty<MapViewParameters> mapParameters,  Consumer<String> error){
        this.property = mapParameters;
        pane = new Pane();
        this.error = error;
    }

    public Pane pane(){
        return pane;
    }
}
