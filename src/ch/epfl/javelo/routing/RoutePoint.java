package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;

public record RoutePoint(PointCh point,double position, double distanceToReference) {

    public static final RoutePoint NONE = new RoutePoint(null, NaN, POSITIVE_INFINITY);


    public RoutePoint withPositionShiftedBy(double positionDifference){
        return null;
    }

    public RoutePoint min(RoutePoint that){
        return null;
    }

    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference){
        return null;
    }
}
