package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;

public record MapViewParameters(int zoomlevel, double OriginXcoord, double OriginYcoord) {

    public Point2D topLeft(){
        return new Point2D(OriginXcoord, OriginYcoord);
    }

    public MapViewParameters withMinXY(double xcoord, double ycoord){
        return new MapViewParameters(zoomlevel, xcoord, ycoord);
    }

    public PointWebMercator pointAt(double xcoord, double ycoord){ //todo par rapport au coin gauche ecran ? conversion a a faire ?
        double X = OriginXcoord + xcoord;
        double Y = OriginYcoord + ycoord;

        return  PointWebMercator.of(zoomlevel, X, Y);
    }

    public int viewX(PointWebMercator point){
        return (int)(point.xAtZoomLevel(zoomlevel) - OriginXcoord);
    }

    public int viewY(PointWebMercator point){
        return (int)(point.yAtZoomLevel(zoomlevel) - OriginYcoord);
    }
}
