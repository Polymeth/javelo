package ch.epfl.javelo.projection;

/**
 * Utility class for Switzerland coordinates in the swiss coordinate system
 *
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class SwissBounds {
    public final static double MIN_E = 2485000; // the smallest east coordinate in switzerland
    public final static double MAX_E = 2834000; // the biggest east coordinate in switzerland
    public final static double MIN_N = 1075000; // the smallest north coordinate in switzerland
    public final static double MAX_N = 1296000; // the biggest north coordinate in switzerland
    public final static double WIDTH = MAX_E - MIN_E; // switzerland width
    public final static double HEIGHT = MAX_N - MIN_N; // switzerland height

    private SwissBounds() {
    }

    /**
     * @param e East coordinates (swiss system)
     * @param n North coordinates (swiss system)
     * @return true if the entered coordinates are located in switzerland
     */
    public static boolean containsEN(double e, double n) {
        return (e <= MAX_E && e >= MIN_E && n <= MAX_N && n >= MIN_N);
    }
}
