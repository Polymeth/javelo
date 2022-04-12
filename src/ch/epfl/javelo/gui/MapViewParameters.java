package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;

public record MapViewParameters(int zoomlevel, double originXcoord, double originYcoord) {

    public Point2D topLeft(){
        return new Point2D(originXcoord, originYcoord);
    }

    public MapViewParameters withMinXY(double xCoord, double yCoord){
        return new MapViewParameters(zoomlevel, xCoord, yCoord);
    }

    public PointWebMercator pointAt(double xCoord, double yCoord){ //todo par rapport au coin gauche ecran ? conversion a a faire ?
        double x = originXcoord + xCoord;
        double y = originYcoord + yCoord;
        return PointWebMercator.of(zoomlevel, x, y);
    }

    public int viewX(PointWebMercator point){
        return (int)(point.xAtZoomLevel(zoomlevel) - originXcoord);
    }

    public int viewY(PointWebMercator point){
        return (int)(point.yAtZoomLevel(zoomlevel) - originYcoord);
    }
}
