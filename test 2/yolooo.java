

import ch.epfl.javelo.Functions;
import org.junit.jupiter.api.Test;

import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class yolooo {
    @Test
    public void constantWorksOnKnownValues() {
        double y= 5.0;
        DoubleUnaryOperator f = Functions.constant(y);
        for (int i = -5; i < 5; i++) {
            assertEquals(5.0, f.applyAsDouble(i));
        }
    }

    @Test
    public void sampledWorksOnKnownSamples() {
        float[] samples = new float[] {1.0f, 0.5f, 3.0f};
        double xMax = 2.0;
        DoubleUnaryOperator f = Functions.sampled(samples, xMax);
        assertEquals(1.0, f.applyAsDouble(-1));
        assertEquals(3.0, f.applyAsDouble(3));
        assertEquals(1.75, f.applyAsDouble(1.5));
    }
}