package ch.epfl.javelo.projection;

/**
 * Creates a point in Ch1903 coordinates
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class Ch1903 {

    private Ch1903() {}

    /**
     * @param lon longitude
     * @param lat latitude
     * @return returns the East coordinates in the swiss system using latitude and longitude values
     */
    public static double e(double lon, double lat) {
        lat = Math.toDegrees(lat);
        lon = Math.toDegrees(lon);
        double lambda1 = Math.pow(10, -4)*(3600*lon-26782.5);
        double phi1 = Math.pow(10, -4)*(3600*lat-169028.66);

        return 2600072.37 + 211455.93*lambda1 - 10938.51*lambda1*phi1
                - 0.36*lambda1*Math.pow(phi1, 2) - 44.54*Math.pow(lambda1, 3);
    }

    /**
     * @param lon longitude
     * @param lat latitude
     * @return returns the North coordinates in the swiss system using latitude and longitude values
     */
    public static double n(double lon, double lat) {
        lat = Math.toDegrees(lat);
        lon = Math.toDegrees(lon);
        double lambda1 = Math.pow(10, -4)*(3600*lon - 26782.5);
        double phi1 = Math.pow(10, -4)*(3600*lat - 169028.66);

        return 1200147.07 + 308807.95*phi1 + 3745.25*Math.pow(lambda1, 2)
                + 76.63*Math.pow(phi1, 2) - 194.56*Math.pow(lambda1, 2)*phi1
                + 119.79*Math.pow(phi1, 3);
    }

    /**
     * @param e East coordinates (swiss system)
     * @param n North coordinates (swiss system)
     * @return returns the longitude value of a location using swiss system
     */
    public static double lon(double e, double n) {
        double x = Math.pow(10, -6)*(e - 2600000);
        double y = Math.pow(10, -6)*(n - 1200000);
        double lambda0 = 2.6779094 + 4.728982*x + 0.791484*x*y + 0.1306*x*Math.pow(y, 2)
                - 0.0436*Math.pow(x, 3);

        return Math.toRadians(lambda0 * 100/36);
    }

    /**
     * @param e East coordinates (swiss system)
     * @param n North coordinates (swiss system)
     * @return returns the latitude value of a location using swiss system
     */
    public static double lat(double e, double n) {
        double x = Math.pow(10, -6)*(e - 2600000);
        double y = Math.pow(10, -6)*(n - 1200000);
        double phi0 = 16.9023892 + 3.238272*y - 0.270978*Math.pow(x, 2)
                - 0.002528*Math.pow(y, 2) - 0.0447*Math.pow(x, 2)*y - 0.0140*Math.pow(y, 3);

        return Math.toRadians(phi0 * 100/36);
    }
}
