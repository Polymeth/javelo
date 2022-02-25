package ch.epfl.javelo.projection;

/**
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class SwissBounds {
    public final static double MIN_E = 2485000;
    public final static double MAX_E = 2834000;
    public final static double MIN_N = 1075000;
    public final static double MAX_N = 1296000;
    public final static double WIDTH = MAX_E - MIN_E;
    public final static double HEIGHT = MAX_N - MIN_N;

    private SwissBounds() {}

    /**
     * @param e East coordinates (swiss system)
     * @param n North coordinates (swiss system)
     * @return true if the entered coordinates are located in switzerland //todo: flemme de bien écrire anglais
     */
    public static boolean containsEN(double e, double n) {
        //if(e == Math2.clamp(MIN_E, e, MAX_E)) //test si c'est worth it
        return (e <= MAX_E && e >= MIN_E && n <= MAX_N && n >= MIN_N);
    }
}
