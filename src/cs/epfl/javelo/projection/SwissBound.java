package cs.epfl.javelo.projection;

import cs.epfl.javelo.Math2;

/**
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class SwissBound {

    private SwissBound() {}

    final static double MIN_E = 2485000;
    final static double MAX_E = 2834000;
    final static double MIN_N = 1075000;
    final static double MAX_N = 1296000;
    final static double WIDTH = MAX_E - MIN_E;
    final static double HEIGHT = MAX_N - MIN_N;



    public static boolean containsEN(double e, double n) {
        //if(e == Math2.clamp(MIN_E, e, MAX_E)) //test si c'est worth it
        return (MIN_E <= e && MAX_E >= e && MIN_N >= n && MAX_N <= n); // change condition
    }
}
