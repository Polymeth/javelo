package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;

/**
 *
 *
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 * @param zoomlevel
 * @param originXcoord x-coordinate of the top-left corner of the displayed map
 * @param originYcoord y-coordinate of the top-left corner of the displayed map
 */
public record MapViewParameters(int zoomlevel, double originXcoord, double originYcoord) {
    /**
     *
     * @return the point in the top left corner (constant for a given Map Section)
     */
    public Point2D topLeft(){
        return new Point2D(originXcoord, originYcoord);
    }

    /**
     *
     * @param xCoord the x-coordinate for a given point
     * @param yCoord the y-coordinate for a given point
     * @return the Map Parameters at the Zoom Level
     */
    public MapViewParameters withMinXY(double xCoord, double yCoord){
        return new MapViewParameters(zoomlevel, xCoord, yCoord);
    }

    /**
     *
     * @param xCoord the x-coordinate
     * @param yCoord the y-coordinate
     * @return at point (In PointWebMercator) at the given coordinate, if it exits
     */
    public PointWebMercator pointAt(double xCoord, double yCoord){
        double x = originXcoord + xCoord;
        double y = originYcoord + yCoord;
        return PointWebMercator.of(zoomlevel, x, y);
    }

    /**
     *
     * @param point at Point (In PointWebMercator)
     * @return the x-coordinate at the zoom level
     */
    public double viewX(PointWebMercator point){
        return (point.xAtZoomLevel(zoomlevel) - originXcoord);
    }

    /**
     *
     * @param point at Point (In PointWebMercator)
     * @return the y-coordinate at the zoom level
     */
    public double viewY(PointWebMercator point){
        return (point.yAtZoomLevel(zoomlevel) - originYcoord);
    }
}
