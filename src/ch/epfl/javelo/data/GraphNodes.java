package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.awt.*;
import java.nio.IntBuffer;

/**
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public record GraphNodes(IntBuffer buffer) {

    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

    public int count(){
        return buffer.capacity()/NODE_INTS;
    }

    // E N ? E N ? E N ?
    public double nodeE(int nodeId){
        int index = nodeId * + OFFSET_E;
    }

    public double nodeN(int nodeId){
        return 0.00;
    }

    public int outDegree(int nodeId){
        return 0;
    }

    public int edgeId(int nodeId, int edgeIndex){
        return 0;
    }
}
