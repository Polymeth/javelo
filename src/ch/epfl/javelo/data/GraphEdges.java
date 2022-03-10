package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations)
{
    private static final int EMPTY = 0;
    private static final int UNCOMPRESSED = 1;
    private static final int COMPRESSED_Q_4_4 = 2;
    private static final int COMPRESSED_Q_0_4 = 3;

    /**
     * @param edgeId id of the edge you want to study
     * @return if the OSM
     */
    public boolean isInverted(int edgeId) {
        return Bits.extractUnsigned(edgesBuffer.getInt(edgeId*10), 31, 1) == 1;
    }

    public int targetNodeId(int edgeId) {
        int value = Bits.extractUnsigned(edgesBuffer.getInt(edgeId*10), 0, 32);
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
        int firstIndex = Bits.extractUnsigned(profileIds.get(edgeId), 0, 30);
        int samplesNumber = 1 + Math2.ceilDiv(Short.toUnsignedInt(edgesBuffer.getShort(4 + edgeId * 10)), Q28_4.ofInt(2)); // number of samples
        float[] decompressed = new float[samplesNumber];

        switch (Bits.extractUnsigned(profileIds.get(edgeId), 30, 2)) {
            case EMPTY:
                return new float[0];
            case UNCOMPRESSED:
                for (int i = 0; i < samplesNumber; i++) {
                    decompressed[i] = Q28_4.asFloat(Bits.extractSigned(elevations.get(firstIndex + i), 0, 16));
                }
                return decompressed;
            case COMPRESSED_Q_4_4:
                return decompressSamples(samplesNumber, firstIndex, edgeId, 8);
            case COMPRESSED_Q_0_4:
                return decompressSamples(samplesNumber, firstIndex, edgeId, 4);
        }
        return new float[0];
    }

    private float[] decompressSamples(int samplesNumber, int firstIndex, int edgeId, int type) {
        float[] decompressed = new float[samplesNumber];
        int samplesCount = 0;
        // type = 4, 8
        for (int i = 0; i < Math2.ceilDiv(samplesNumber-1, 16/type)+1; i++) {
            if (i == 0) {
                System.out.println("yolo: " + elevations.get(firstIndex));
                decompressed[i] = Q28_4.asFloat(elevations.get(firstIndex));
            } else {
                for (int j = 0; j < 16/type; j++) {
                    samplesCount++;
                    if (samplesCount < samplesNumber) {

                        int start = (type == 4) ? 12-4*j : 8-8*j;
                        System.out.println("infdex de mort : " + firstIndex + i);
                        float difference = Q28_4.asFloat(Bits.extractSigned(elevations.get(firstIndex + i), start, type));
                        decompressed[samplesCount] = (decompressed[samplesCount-1])+difference;
                    }
                }
            }
        }
        return isInverted(edgeId) ? reverseOrder(decompressed) : decompressed;
    }

    private float[] reverseOrder(float[] list) {
        float[] newList = new float[list.length];
        for (int i = 0; i < list.length; i++) {
            newList[i] = list[list.length-i-1];
        }
        return newList;
    }
    
}
