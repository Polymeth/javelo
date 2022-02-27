package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

/**
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public record AttributeSet(long bits) {
    public AttributeSet {
        long mask63 = 1L << 63;
        long mask62 = 1L << 62;
        long mask6263 = mask62 | mask63;

        System.out.println("mas: " + Long.toBinaryString(mask6263));
        System.out.println("num: " + Long.toBinaryString(bits));
        System.out.println("aft: " + Long.toBinaryString(bits & mask6263));

        Preconditions.checkArgument((bits & mask6263) != mask6263);
        Preconditions.checkArgument((bits & mask6263) != mask63);
        Preconditions.checkArgument((bits & mask6263) != mask62);
    }

    public AttributeSet of(Attribute... attributes) {
        return null;
    }
}
