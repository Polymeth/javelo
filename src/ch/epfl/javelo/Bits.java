package ch.epfl.javelo;

import javax.swing.text.AbstractDocument;

/**
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
     */
    public static int extractSigned(int value, int start, int length) {
        Preconditions.checkArgument(length <= 32 && length >= 0 && start >= 0 && start < 31 && start+length <= 32);
        int temp = value << 32 - start - length;
        return temp >> 32 - length;
    }

    /**
     * @param value a 32 bit maximum array
     * @param start the index of the first bit of the desired sequence
     * @param length the index of the desired sequence
     * @return extract an unsigned version of a certain number of bits located in the bit sequence
     */
   public static int extractUnsigned(int value, int start, int length){
        Preconditions.checkArgument(length < 32 && length >= 0 && start >= 0 && start <= 31 && start+length <= 32);
        int temp = value << 32 - start - length;
        return temp >>> 32-length;
   }
}
