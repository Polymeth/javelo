package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;
import org.w3c.dom.Attr;

import java.util.StringJoiner;

/**
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public record AttributeSet(long bits) {
    public AttributeSet {
        Preconditions.checkArgument(
                !((bits&(1L << 62)) != 0 || (bits&(1L << 63)) != 0)
        );
   }

    /**
     * @param attributes attributes you want to build your AttributeSet with
     * @return an AttributeSet built on the entered attributes
     */
    public static AttributeSet of(Attribute... attributes) {
        long temp = 0L;
        for (Attribute a : attributes) {
            temp = temp | (1L << a.ordinal());
        }
        return new AttributeSet(temp);
    }

    /**
     * @param attribute attribute you want to check
     * @return if the AttributeSet contains the entered attribute
     */
    public boolean contains(Attribute attribute) {
        long mask = 1L << attribute.ordinal();
        return (bits & mask) == mask;
    }

    /**
     * @param that the attribute you wanna test
     * @return true if attributeset has common elements with given attribuset that
     */
    public boolean intersects(AttributeSet that) {
        return !((bits&that.bits()) == 0);
    }

    @Override
    public String toString() {
        StringJoiner strJoin = new StringJoiner(",", "{", "}");
        for (Attribute att : Attribute.ALL) {
            if (this.contains(att)) strJoin.add(att.key() + "=" + att.value());
        }
        return strJoin.toString();
    }
}

