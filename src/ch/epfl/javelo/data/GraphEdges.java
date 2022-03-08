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
    //   0  |  1  |  2  |  3  |  4  |  5  |  6  |  7  |   8  |   9  |
    //   8  |  8  |  8  |  8  |  8  |  8  |  8  |  8  |   8  |   8  |
    // inv+target             | longueur  |  denivlé  | osm shit

    private static final int EMPTY = 0;
    private static final int UNCOMPRESSED = 1;
    private static final int COMPRESSED_Q_4_4 = 2;
    private static final int COMPRESSED_Q_0_4 = 3;

    //todo: correct index
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

    // 16 | 4 4 4 4 | 4 4 4 4 | 4

    public float[] profileSamples(int edgeId) {
        // taille du tableau = nbEchantillon
        int firstIndex = Bits.extractUnsigned(profileIds.get(edgeId), 0, 30);
        int samplesNumber = 1 + Math2.ceilDiv(Short.toUnsignedInt(edgesBuffer.getShort(4 + edgeId * 10)), Q28_4.ofInt(2)); // number of samples
        int count = 0;

        switch (Bits.extractUnsigned(profileIds.get(edgeId), 30, 2)) {
            case EMPTY:
                return new float[0];
            case UNCOMPRESSED:
               /* for (int i = 0; i < samplesNumber; i++) {
                    int lol = elevations.get(firstIndex + i);
                    System.out.println(lol);
                }*/
                break;
            case COMPRESSED_Q_4_4:
                break;

            case COMPRESSED_Q_0_4:
                float[] decompressed = new float[samplesNumber];
                for (int i = 0; i < Math2.ceilDiv(samplesNumber-1, 4)+1; i++) {
                    if (i == 0) {
                        decompressed[i] = Q28_4.asFloat(elevations.get(firstIndex));
                    } else {
                        for (int j = 0; j < 4; j++) {
                            float difference = Q28_4.asFloat(Bits.extractSigned(elevations.get(firstIndex + i), 12-4*j, 4));
                            decompressed[i+j+3*(i-1)] = (decompressed[i+j+3*(i-1)-1])+difference;
                            //System.out.println(decompressed[i+j+3*(i-1)] + " & " + difference);
                        }
                    }
                }
               /** for (int i = 0; i < samplesNumber; i++) {
                    if (i == 0) {
                        yolo[i] = Q28_4.asFloat(elevations.get(firstIndex));
                    } else { //todo: horrible boucle
                        for (int j = 0; j < 3; j++) {
                            yolo[] = Q28_4.asFloat(Bits.extractSigned(elevations.get(firstIndex+i), 4*j, 4));
                        }
                    }

                }*/
                return decompressed;
        }
        return new float[0];
    }
}
