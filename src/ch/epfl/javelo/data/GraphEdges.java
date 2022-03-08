package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations)
{
    //   0  |  1  |  2  |  3  |  4  |  5  |  6  |  7  |   8  |   9  |
    //   8  |  8  |  8  |  8  |  8  |  8  |  8  |  8  |   8  |   8  |
    // inv+target             | longueur  |  denivlé  | osm shit

    public boolean isInverted(int edgeId) {
        return Bits.extractUnsigned(edgesBuffer.getInt(edgeId*2), 31, 1) == 1;
    }

    public int targetNodeId(int edgeId) {
        int value = Bits.extractUnsigned(edgesBuffer.getInt(edgeId*2), 0, 32);
        if (isInverted(edgeId)) {
            return ~value;
        } else {
            return value;
        }
    }

    public double length(int edgeId) {
        return Q28_4.asDouble(edgesBuffer.getShort(4+edgeId*10));
    }

    public double elevationGain(int edgeId) {
        return Q28_4.asDouble(edgesBuffer.getShort(6+edgeId*10));
    }

    public int attributesIndex(int edgeId) {
        return Short.toUnsignedInt(edgesBuffer.getShort(8+edgeId*10));
    }

    public boolean hasProfile(int edgeId) {
        return Bits.extractUnsigned(profileIds.get(0), 30, 2) != 0;
    }

    public float[] profileSamples(int edgeId) {
        
    }
}
