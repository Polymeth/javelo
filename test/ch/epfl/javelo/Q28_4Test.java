package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Q28_4Test {

    @Test
    void ofInt() {
        var value = 0b10011100;
        var actual = Q28_4.asFloat(value);
        var expected = -6.25;
        assertEquals(expected, actual);
    }

    @Test
    void asDouble() {
    }

    @Test
    void asFloat() {
    }
}