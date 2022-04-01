package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;

/**
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public record RoutePoint(PointCh point,double position, double distanceToReference) {
    public static final RoutePoint NONE = new RoutePoint(null, NaN, POSITIVE_INFINITY);

    /**
     * @param positionDifference positive or negative difference
     * @return a RoutePoint located at the position of your point plus the difference
     */
    public RoutePoint withPositionShiftedBy(double positionDifference){
        return new RoutePoint(point, position + positionDifference, distanceToReference);
    }

    /**
     * @param that any RoutePoint
     * @return itself if the distance to reference is lower than the entered point one, returns the entered point otherwhise
     */
    public RoutePoint min(RoutePoint that){
        return (this.distanceToReference < that.distanceToReference) ? this : that;
    }

    /**
     * @param thatPoint any point
     * @param thatPosition any position
     * @param thatDistanceToReference any distance to reference
     * @return itself if the distance to reference is lower than the entered one, otherwise it creates a new point with the entered arguments
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference){
        return (this.distanceToReference < thatDistanceToReference)
                ? this
                : new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
    }
}
