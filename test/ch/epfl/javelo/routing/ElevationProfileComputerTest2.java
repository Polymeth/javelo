package ch.epfl.javelo.routing;



import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ElevationProfileComputerTest2 {
    @Test
    void elevationProfileReturnsTheRightTabAllNan(){
        float[] elevationSamples = {(float) Double.NaN, (float) Double.NaN, (float) Double.NaN, (float) Double.NaN, (float) Double.NaN};
        float[] expected = {0f, 0f, 0f, 0f, 0f};
        float[] actual = ElevationProfileComputer.tab(elevationSamples, 5);
        assertArrayEquals(expected, actual);
    }
    @Test
    void elevationProfileReturnsTheRightTabFirstNan(){
        float[] elevationSamples = {(float) Double.NaN, (float) Double.NaN, 2f, 3f, 4f};
        float[] expected = {2f, 2f, 2f, 3f, 4f};
        float[] actual = ElevationProfileComputer.tab(elevationSamples, 5);
        assertArrayEquals(expected, actual);
    }
    @Test
    void elevationProfileReturnsTheRightTabLastNan(){
        float[] elevationSamples = {1f, 2f, 3f, (float) Double.NaN,(float) Double.NaN};
        float[] expected = {1f, 2f, 3f, 3f, 3f};
        float[] actual = ElevationProfileComputer.tab(elevationSamples, 5);
        assertArrayEquals(expected, actual);
    }
    @Test
    void elevationProfileReturnsTheRightTabMiddleNan(){
        float[] elevationSamples = {0f, (float) Double.NaN, (float) Double.NaN, 3f, 5f};
        float[] expected = {0f, 1f, 2f, 3f, 5f};
        float[] actual = ElevationProfileComputer.tab(elevationSamples, 5);
        assertArrayEquals(expected, actual);
    }
    @Test
    void elevationProfileReturnsTheRightTabMiddleNan2(){
        float[] elevationSamples = {0f, (float) Double.NaN,  1f, (float) Double.NaN, 2f};
        float[] expected = {0f, 0.5f, 1f, 1.5f, 2f};
        float[] actual = ElevationProfileComputer.tab(elevationSamples, 5);
        assertArrayEquals(expected, actual);
    }
    @Test
    void elevationProfileReturnsTheRightTabMiddleNan3() {
        float[] elevationSamples = {(float) Double.NaN, (float) Double.NaN,  0f, (float) Double.NaN, (float) Double.NaN, 3f, (float) Double.NaN, 5f, (float) Double.NaN};
        float[] expected = {0f, 0f, 0f, 1f, 2f, 3f, 4f, 5f, 5f};
        float[] actual = ElevationProfileComputer.tab(elevationSamples, 9);
        assertArrayEquals(expected, actual);
    }
    @Test
    void elevationProfileReturnsTheRightTabValid() {
        float[] elevationSamples = {1f, 2f, 3f, 4f, 5f};
        float[] expected = {1f, 2f, 3f, 4f, 5f};
        float[] actual = ElevationProfileComputer.tab(elevationSamples, 5);
        assertArrayEquals(expected, actual);
    }
}
