package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.Collections;

import static java.lang.Short.toUnsignedInt;

/**
 * Creates the logic behind the edges of the graph
 *
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 * @param edgesBuffer buffer with all the edges
 * @param profileIds buffer with all the IDs of the profiles
 * @param elevations buffer with all the elevations at points
 */
public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {
    private static final int EMPTY = 0;
    private static final int UNCOMPRESSED = 1;
    private static final int COMPRESSED_Q_4_4 = 2;
    private static final int COMPRESSED_Q_0_4 = 3;

    private static final byte OFFSET_LENGTH = Integer.BYTES; // 4
    private static final byte OFFSET_ELEVATION = OFFSET_LENGTH + Short.BYTES; // 6
    private static final byte OFFSET_ATTRIBUTES = OFFSET_ELEVATION + Short.BYTES; // 8
    private static final byte OFFSET_EDGE = OFFSET_ATTRIBUTES + Short.BYTES; // 10

    /**
     * @param edgeId id of the edge you want to study
     * @return true if the edge is going to the inverse of the direction of its OSM data
     */
    public boolean isInverted(int edgeId) {
        return (edgesBuffer.getInt(edgeId * OFFSET_EDGE) < 0);
    }

    /**
     * @param edgeId id of the edge you want to study
     * @return the index of the first node corresponding to the edge
     */
    public int targetNodeId(int edgeId) {
        int value = edgesBuffer.getInt(edgeId * OFFSET_EDGE);
        return (isInverted(edgeId)) ? ~value : value;
    }

    /**
     * @param edgeId id of the edge you want to study
     * @return the length (in meter) of the edge
     */
    public double length(int edgeId) {
        return Q28_4.asDouble(toUnsignedInt(edgesBuffer.getShort(OFFSET_LENGTH + edgeId * OFFSET_EDGE)));
    }

    /**
     * @param edgeId id of the edge you want to study
     * @return the elevation gain of the edge
     */
    public double elevationGain(int edgeId) {
        return Q28_4.asDouble(toUnsignedInt(edgesBuffer.getShort(OFFSET_ELEVATION + edgeId * OFFSET_EDGE)));
    }

    /**
     * @param edgeId id of the edge you want to study
     * @return returns the index of the first attribute
     */
    public int attributesIndex(int edgeId) {
        return toUnsignedInt(edgesBuffer.getShort(OFFSET_ATTRIBUTES + edgeId * OFFSET_EDGE));
    }

    /**
     * @param edgeId id of the edge you want to study
     * @return true if the edge's samples have a profile
     */
    public boolean hasProfile(int edgeId) {
        return Bits.extractUnsigned(profileIds.get(edgeId), 30, 2) != 0;
    }

    /**
     * @param edgeId id of the edge you want to study
     * @return returns the decompressed
     */
    public float[] profileSamples(int edgeId) {
        int firstIndex = Bits.extractUnsigned(profileIds.get(edgeId), 0, 30);
        int samplesNumber = 1 + Math2.ceilDiv(toUnsignedInt(edgesBuffer.getShort(OFFSET_LENGTH + edgeId * OFFSET_EDGE)),
                Q28_4.ofInt(2));

        float[] decompressed = new float[samplesNumber];

        switch (Bits.extractUnsigned(profileIds.get(edgeId), 30, 2)) {
            case EMPTY:
                return new float[0];
            case UNCOMPRESSED:
                for (int i = 0; i < samplesNumber; i++) {
                    decompressed[i] = Q28_4.asFloat(toUnsignedInt(elevations.get(firstIndex + i)));
                }
                return isInverted(edgeId) ? reverseOrder(decompressed) : decompressed;
            case COMPRESSED_Q_4_4:
                return decompressSamples(samplesNumber, firstIndex, edgeId, 8);
            case COMPRESSED_Q_0_4:
                return decompressSamples(samplesNumber, firstIndex, edgeId, 4);
            default:
                return null;
        }
    }

    /**
     * @param samplesNumber the number of samples
     * @param firstIndex    the first index of the samples in the buffer
     * @param edgeId        the id of the edge you want to study
     * @param type          4 or 8 depending on the compression
     * @return the decompressed samples in the right order depending on the compression type
     */
    private float[] decompressSamples(int samplesNumber, int firstIndex, int edgeId, int type) {
        float[] decompressed = new float[samplesNumber];
        int samplesCount = 0;

        for (int i = 0; i < Math2.ceilDiv(samplesNumber - 1, 16 / type) + 1; i++) {
            if (i == 0) {
                decompressed[i] = Q28_4.asFloat(elevations.get(firstIndex));
            } else {
                for (int j = 0; j < 16 / type; j++) {
                    samplesCount++;
                    if (samplesCount < samplesNumber) {
                        int start = (type == 4) ? 12 - 4 * j : 8 - 8 * j;
                        float difference = Q28_4.asFloat(Bits.extractSigned(elevations.get(firstIndex + i), start, type));
                        decompressed[samplesCount] = (decompressed[samplesCount - 1]) + difference;
                    }
                }
            }
        }
        return isInverted(edgeId) ? reverseOrder(decompressed) : decompressed;
    }

    /**
     * @param list list you want to reverse
     * @return reverse the order of the list (the last element becomes the first etc..)
     */
    public float[] reverseOrder(float[] list) {
        for(int i = 0; i < list.length / 2; i++) {
            float temp = list[i];
            list[i] = list[list.length - i - 1];
            list[list.length - i - 1] = temp;
        }
        return list;
    }
}


