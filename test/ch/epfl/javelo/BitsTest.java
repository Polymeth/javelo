package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitsTest {

    @Test
    void extractSignedWorks() {
        var baseBinary = 0b11001010111111101011101010111110;
        var expected = 0b11111111111111111111111111111010;
        var actual = Bits.extractSigned(baseBinary, 8, 4);
        assertEquals(expected, actual);
    }

    @Test
    void extractUnsignedWorks() {
        var baseBinary = 0b11001010111111101011101010111110;
        var expected = 0b00000000000000000000000000001010;
        var actual = Bits.extractUnsigned(baseBinary, 8, 4);
        assertEquals(expected, actual);
    }

    @Test
    void extractUnsignedThrowsNotValid() {
        var baseBinary = 0b11001010111111101011101010111110;
        assertThrows(IllegalArgumentException.class, () -> {
           var actual = Bits.extractUnsigned(baseBinary, 35, 55);
        });
    }

    @Test
    void extractSignedThrowsNotValid() {
        var baseBinary = 0b11001010111111101011101010111110;
        assertThrows(IllegalArgumentException.class, () -> {
            var actual = Bits.extractUnsigned(baseBinary, 32, 3);
        });
    }
}