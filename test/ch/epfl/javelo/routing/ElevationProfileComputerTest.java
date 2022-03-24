package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileComputerTest {

    @Test
    void elevationProfileOnNormal() {

        // TODO: clean le code
        PointCh fromPoint = new PointCh(2485010, 1076000);
        PointCh toPoint = new PointCh(2485020, 1076000);

        // profile for edge 1
        float[] type3Array = new float[]{
                384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f,
        };
        DoubleUnaryOperator profile = Functions.sampled(type3Array, 10);

        // profile for edge 2
        float[] type3Array2 = new float[]{
                384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f,
        };
        DoubleUnaryOperator profile2 = Functions.sampled(type3Array2, 10);

        Edge edge1 = new Edge(0, 3, fromPoint, toPoint, 10, profile);
        Edge edge2 = new Edge(0, 3, fromPoint, toPoint, 10, profile2);

        ArrayList<Edge> testEdges = new ArrayList<Edge>();
        testEdges.add(edge1);
        //testEdges.add(edge2);
        SingleRoute route = new SingleRoute(testEdges);

        double maxStepLength = 1.8;
        int stepNumber = (int)Math.ceil(route.length() / maxStepLength) +1;
        double distanceBetweenPoints = route.length() / stepNumber;

        ElevationProfile Elevprofile = ElevationProfileComputer.elevationProfile(route, maxStepLength);

        float[] actualtype3Array = new float[stepNumber];
        float[] expectedtype3Array = new float[]{ 384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f,};

        for (int i = 0; i < Math.min(stepNumber, expectedtype3Array.length); i++){
            actualtype3Array[i] = (float)Elevprofile.elevationAt(i);

        }

        for (int i = 0; i < Math.min(stepNumber, expectedtype3Array.length); i++){
            assertEquals(type3Array[i], actualtype3Array[i], 1e-1);
        }



    }

    @Test
    void elevationProfileOnNanInFront() {

        // TODO: clean le code
        PointCh fromPoint = new PointCh(2485010, 1076000);
        PointCh toPoint = new PointCh(2485020, 1076000);

        // profile for edge 1
        float[] type3Array = new float[]{
                Float.NaN, Float.NaN, Float.NaN, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f,
        };
        DoubleUnaryOperator profile = Functions.sampled(type3Array, 10);

        // profile for edge 2
        float[] type3Array2 = new float[]{
                Float.NaN, Float.NaN, Float.NaN, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f,
        };
        DoubleUnaryOperator profile2 = Functions.sampled(type3Array2, 10);

        Edge edge1 = new Edge(0, 3, fromPoint, toPoint, 10, profile);
        Edge edge2 = new Edge(0, 3, fromPoint, toPoint, 10, profile2);

        ArrayList<Edge> testEdges = new ArrayList<Edge>();
        testEdges.add(edge1);
        testEdges.add(edge2);
        SingleRoute route = new SingleRoute(testEdges);

        double maxStepLength = 1.8;
        int stepNumber = (int)Math.ceil(route.length() / maxStepLength) +1;
        double distanceBetweenPoints = route.length() / stepNumber;

        ElevationProfile Elevprofile = ElevationProfileComputer.elevationProfile(route, maxStepLength);

        float[] actualtype3Array = new float[stepNumber];
        float[] expectedtype3Array = new float[]{
                384.5f, 384.5f, 384.5f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f,
        };
        for (int i = 0; i< Math.min(stepNumber, expectedtype3Array.length); i++){
            actualtype3Array[i] = (float)Elevprofile.elevationAt(i);

        }

        for (int i = 0; i < Math.min(stepNumber, expectedtype3Array.length); i++){
            assertEquals(expectedtype3Array[i], actualtype3Array[i], 1e-1);
        }



    }

    @Test
    void elevationProfileOnNanInEnd() {

        // TODO: clean le code
        PointCh fromPoint = new PointCh(2485010, 1076000);
        PointCh toPoint = new PointCh(2485020, 1076000);

        // profile for edge 1
        float[] type3Array = new float[]{
                 384.5f, 384.4375f, 384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f, Float.NaN, Float.NaN, Float.NaN
        };
        DoubleUnaryOperator profile = Functions.sampled(type3Array, 10);

        // profile for edge 2
        float[] type3Array2 = new float[]{
                384.5f, 384.4375f, 384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f, Float.NaN, Float.NaN, Float.NaN
        };
        DoubleUnaryOperator profile2 = Functions.sampled(type3Array2, 10);

        Edge edge1 = new Edge(0, 3, fromPoint, toPoint, 10, profile);
        Edge edge2 = new Edge(0, 3, fromPoint, toPoint, 10, profile2);

        ArrayList<Edge> testEdges = new ArrayList<Edge>();
        testEdges.add(edge1);
        testEdges.add(edge2);
        SingleRoute route = new SingleRoute(testEdges);

        double maxStepLength = 1.8;
        int stepNumber = (int)Math.ceil(route.length() / maxStepLength) +1;
        double distanceBetweenPoints = route.length() / (stepNumber );

        ElevationProfile Elevprofile = ElevationProfileComputer.elevationProfile(route, maxStepLength);

        float[] actualtype3Array = new float[stepNumber];
        float[] expectedtype3Array = new float[]{
                384.5f, 384.4375f, 384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f, 384.0625f, 384.0625f, 384.0625f
        };
        for (int i = 0; i < Math.min(stepNumber, expectedtype3Array.length); i+= 1){
            actualtype3Array[i] = (float)Elevprofile.elevationAt(i);

        }

        for (int i = 0; i < Math.min(stepNumber, expectedtype3Array.length) ; i++){
            assertEquals(expectedtype3Array[i], actualtype3Array[i], 1e-1);
        }



    }

    @Test
    void elevationProfileOnNanInMiddle() {

        // TODO: clean le code
        PointCh fromPoint = new PointCh(2485010, 1076000);
        PointCh toPoint = new PointCh(2485020, 1076000);

        // profile for edge 1
        float[] type3Array = new float[]{
                384.5f, 384.4375f, 384.375f, Float.NaN, Float.NaN, Float.NaN, 384.3125f, 384.25f, 384.125f, 384.0625f
        };
        DoubleUnaryOperator profile = Functions.sampled(type3Array, 10);

        // profile for edge 2
        float[] type3Array2 = new float[]{
                384.5f, 384.4375f, 384.375f, Float.NaN, Float.NaN, Float.NaN, 384.3125f, 384.25f, 384.125f, 384.0625f
        };
        DoubleUnaryOperator profile2 = Functions.sampled(type3Array2, 10);

        Edge edge1 = new Edge(0, 3, fromPoint, toPoint, 10, profile);
        Edge edge2 = new Edge(0, 3, fromPoint, toPoint, 10, profile2);

        ArrayList<Edge> testEdges = new ArrayList<Edge>();
        testEdges.add(edge1);
        testEdges.add(edge2);
        SingleRoute route = new SingleRoute(testEdges);

        double maxStepLength = 1.8;
        int stepNumber = (int)Math.ceil(route.length() / maxStepLength) +1;
        double distanceBetweenPoints = route.length() / stepNumber;

        ElevationProfile Elevprofile = ElevationProfileComputer.elevationProfile(route, maxStepLength);

        float[] actualtype3Array = new float[stepNumber];
        float[] expectedtype3Array = new float[]{
                384.5f, 384.4375f, 384.375f, 384.3125f, 384.25f, 384.125f, 384.3125f, 384.25f, 384.125f, 384.0625f
        };
        for (int i = 0; i < Math.min(stepNumber, expectedtype3Array.length); i+= 1){
            actualtype3Array[i] = (float)Elevprofile.elevationAt(i);

        }

        for (int i = 0; i < Math.min(stepNumber, expectedtype3Array.length); i++){
            assertEquals(expectedtype3Array[i], actualtype3Array[i], 1e-1);
        }



    }

    @Test
    void elevationProfileReturnsTheRightTabAllNan(){
        // TODO: clean le code
        PointCh fromPoint = new PointCh(2485010, 1076000);
        PointCh toPoint = new PointCh(2485020, 1076000);


        float[] elevationSamples = {(float) Double.NaN, (float) Double.NaN, (float) Double.NaN, (float) Double.NaN, (float) Double.NaN};
        DoubleUnaryOperator profile = Functions.sampled(elevationSamples, 10);
        DoubleUnaryOperator profile2 = Functions.sampled(elevationSamples, 10);

        ArrayList<Edge> testEdges = new ArrayList<Edge>();

        Edge edge1 = new Edge(0, 3, fromPoint, toPoint, 10, profile);
        testEdges.add(edge1);

        Edge edge2 = new Edge(0, 3, fromPoint, toPoint, 10, profile2);
        testEdges.add(edge2);

        SingleRoute route = new SingleRoute(testEdges);

        double maxStepLength = 3.0;
        int stepNumber = (int)Math.ceil(route.length() / maxStepLength) +1;
        double distanceBetweenPoints = route.length() / stepNumber;

        ElevationProfile Elevprofile = ElevationProfileComputer.elevationProfile(route, maxStepLength);

        float[] expected = {0f, 0f, 0f, 0f, 0f};
        float[] actual = new float[expected.length];
        //float[] actual = ElevationProfileComputer.tab(elevationSamples, 5);
        //assertArrayEquals(expected, actual);

        for (int i = 0; i < Math.min(stepNumber, expected.length); i+= 1){
            actual[i] = (float)Elevprofile.elevationAt(i);

        }

        assertArrayEquals(expected, actual);
    }
    @Test
    void elevationProfileReturnsTheRightTabFirstNan(){
        PointCh fromPoint = new PointCh(2485010, 1076000);
        PointCh toPoint = new PointCh(2485020, 1076000);

        float[] elevationSamples = {(float) Double.NaN, (float) Double.NaN, 2f, 3f, 4f};

        DoubleUnaryOperator profile = Functions.sampled(elevationSamples, 10);

        Edge edge1 = new Edge(0, 3, fromPoint, toPoint, 10, profile);
        ArrayList<Edge> testEdges = new ArrayList<Edge>();
        testEdges.add(edge1);
        SingleRoute route = new SingleRoute(testEdges);

        double maxStepLength = 1.8;
        int stepNumber = (int)Math.ceil(route.length() / maxStepLength) +1;
        double distanceBetweenPoints = route.length() / stepNumber;

        ElevationProfile Elevprofile = ElevationProfileComputer.elevationProfile(route, maxStepLength);

        float[] expected = {2f, 2f, 2f, 3f, 4f};
        float[] actual = new float[expected.length];

        for (int i = 0; i < Math.min(stepNumber, expected.length); i+=1){
            actual[i] = (float)Elevprofile.elevationAt(i);
        }

        assertArrayEquals(expected, actual);
    }

    @Test
    void elevationProfileOnNanInMiddle2() {

        // TODO: clean le code
        PointCh fromPoint = new PointCh(2485010, 1076000);
        PointCh toPoint = new PointCh(2485020, 1076000);

        // profile for edge 1
        float[] type3Array = new float[]{(float) Double.NaN, (float) Double.NaN,  0f, (float) Double.NaN, (float) Double.NaN, 3f, (float) Double.NaN, 5f, (float) Double.NaN};
        DoubleUnaryOperator profile = Functions.sampled(type3Array, 10);

        // profile for edge 2
        float[] type3Array2 = new float[]{(float) Double.NaN, (float) Double.NaN,  0f, (float) Double.NaN, (float) Double.NaN, 3f, (float) Double.NaN, 5f, (float) Double.NaN};
        DoubleUnaryOperator profile2 = Functions.sampled(type3Array, 10);

        Edge edge1 = new Edge(0, 3, fromPoint, toPoint, 10, profile);
        Edge edge2 = new Edge(0, 3, fromPoint, toPoint, 10, profile2);

        ArrayList<Edge> testEdges = new ArrayList<Edge>();
        testEdges.add(edge1);
        testEdges.add(edge2);
        SingleRoute route = new SingleRoute(testEdges);

        double maxStepLength = 1;
        int stepNumber = (int)Math.ceil(route.length() / maxStepLength) +1;
        double distanceBetweenPoints = route.length() / stepNumber;

        ElevationProfile Elevprofile = ElevationProfileComputer.elevationProfile(route, maxStepLength);

        float[] actualtype3Array = new float[stepNumber];
        float[] expectedtype3Array = new float[]{0f, 0f, 0f, 1f, 2f, 3f, 4f, 5f, 5f};

        for (int i = 0; i < Math.min(stepNumber, expectedtype3Array.length); i+= 1){
            actualtype3Array[i] = (float)Elevprofile.elevationAt(i);

        }

        for (int i = 0; i < Math.min(stepNumber, expectedtype3Array.length); i++){
            assertEquals(expectedtype3Array[i], actualtype3Array[i], 1e-1);
        }

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