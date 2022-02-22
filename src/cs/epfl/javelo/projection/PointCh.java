package cs.epfl.javelo.projection;

import cs.epfl.javelo.Preconditions;

/**
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public record PointCh(double e, double n) {
    public PointCh {
        Preconditions.checkArgument(SwissBounds.containsEN(e, n));
    }

    /**
     * @param that a point in the swiss system
     * @return returns the distance in meter from this point to the entered point
     */
    public double distanceTo(PointCh that) {
        //todo: je crois que la distance là est en km faut la convertir mais à vérif
        return Math.hypot(that.e - this.e, that.n - this.n);
    }

    /**
     * @param that a point in the swiss system
     * @return returns the squared distance in meter from this point to the entered point
     */
    public double squaredDistanceTo(PointCh that) {
        //todo: verif le sens des - (this avant that ptet idk)
        return Math.pow(distanceTo(that), 2);
    }

    /**
     * @return the longitude of the point in radians
     */
    public double lon() {
        return Ch1903.lon(e, n);
    }

    /**
     * @return the latitude of the point in radians
     */
    public double lat() {
        return Ch1903.lat(e, n);
    }
}
