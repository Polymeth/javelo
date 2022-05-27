package ch.epfl.javelo;

/**
 * Utility class to manipulate bits and bits vectors
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class Bits {
    private Bits() {}

    /**
     * @param value a 32 bit maximum array
     * @param start the index of the first bit of the desired sequence
     * @param length the index of the desired sequence
     * @return extract a signed version of a certain number of bits located in the bit sequence
     * @throws IllegalArgumentException if the extracted length is bigger than 32 or lower than 0,
     *                                  if the start index is strictly bigger than 31 or lower than 0,
     *                                  if the start + length value is bigger than 32 (otherwise there is
     *                                  an overflow)
     */
    public static int extractSigned(int value, int start, int length) {
        Preconditions.checkArgument(length <= Integer.SIZE && length >= 0 && start >= 0
                && start < Integer.SIZE -1 && start+length <= Integer.SIZE);
        int temp = value << Integer.SIZE - start - length;
        return temp >> Integer.SIZE - length;
    }

    /**
     * @param value a 32 bit maximum array
     * @param start the index of the first bit of the desired sequence
     * @param length the index of the desired sequence
     * @return extract an unsigned version of a certain number of bits located in the bit sequence
     * @throws IllegalArgumentException if the extracted length is bigger than 32 or lower than 0,
     *                                  if the start index is strictly bigger than 31 or lower than 0,
     *                                  if the start + length value is bigger than 32 (otherwise there is
     *                                  an overflow)
     */
   public static int extractUnsigned(int value, int start, int length){
        Preconditions.checkArgument(length < Integer.SIZE && length >= 0 && start >= 0
                && start <= Integer.SIZE -1 && start+length <= Integer.SIZE);
        int temp = value << Integer.SIZE - start - length;
        return temp >>> Integer.SIZE-length;
   }
}
