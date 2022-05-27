package ch.epfl.javelo;

/**
 * Utility class to manipulate Q28_4 bits format
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class Q28_4 {

    public static final int OFFSET_RIGHT = 4;

    private Q28_4() {}

    /**
     * @param i an integer
     * @return return the Q28_4 integer
     */
    public static int ofInt(int i) {
        return i<< OFFSET_RIGHT;
    }

    /**
     * @param q28_4 a Q28_4 integer
     * @return returns the Q28_4 decimal value as a double
     */
    public static double asDouble(int q28_4){
        return Math.scalb((double)q28_4, -OFFSET_RIGHT);
    }

    /**
     * @param q28_4 a Q28_4 integer
     * @return returns the Q28_4 decimal value as a float
     */
    public static float asFloat(int q28_4){
        return Math.scalb(q28_4, -OFFSET_RIGHT);
    }
}
