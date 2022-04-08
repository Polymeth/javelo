package ch.epfl.javelo;

/**
 * Utility class to check stuff and throw exceptions is false
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class Preconditions {

    private Preconditions() {}

    /**
     * Verify a condition and throws an exception if the condition isn't true
     * @param shouldBeTrue the condition that needs to be tested
     * @throws IllegalArgumentException if the entered condition is false
     */
    public static void checkArgument(boolean shouldBeTrue){
        if(!(shouldBeTrue)){
            throw new IllegalArgumentException();
        }
    }
}
