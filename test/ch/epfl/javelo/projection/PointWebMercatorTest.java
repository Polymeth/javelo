package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class PointWebMercatorTest {

    private static final double DELTA = 1;

    @Test
    void of() {
    }

    @Test
    void ofPointCh() {
    }

    @Test
    void xAtZoomLevel() {
        PointWebMercator test = new PointWebMercator(0.518275214444,0.353664894749);
        var expected = 69561722;
        var actual = test.xAtZoomLevel(19);
        assertEquals(expected, actual, DELTA);
    }

    @Test
    void yAtZoomLevel() {
        PointWebMercator test = new PointWebMercator(0.518275214444,0.353664894749);
        var expected = 47468099;
        var actual = test.yAtZoomLevel(19);
        assertEquals(expected, actual, DELTA);
    }

    @Test
    void lon() {
        PointWebMercator test = new PointWebMercator(0.518275214444,0.353664894749);
        var expected = 6.579772;
        var actual = test.lon();
        assertEquals(expected, actual, DELTA);
    }

    @Test
    void lat() {
        PointWebMercator test = new PointWebMercator(0.518275214444,0.353664894749);
        var expected = 46.5218976;
        var actual = test.lat();
        assertEquals(expected, actual, DELTA);
    }

    @Test
    void toPointCh() {
        PointWebMercator test = new PointWebMercator(0.518275214444,0.353664894749);
        PointCh converted = test.toPointCh();
        PointCh expected = new PointCh(Ch1903.e(Math.toRadians(test.lon()), Math.toRadians(test.lat())), Ch1903.n(Math.toRadians(test.lon()), Math.toRadians(test.lat())));

        assertEquals(expected, converted);
    }


}