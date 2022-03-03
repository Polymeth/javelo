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

    /**
     * @return count the number of nodes
     */
    public int count(){
        return buffer.capacity()/NODE_INTS;
    }

    /**
     * @param nodeId the index of the node
     * @return returns the E coordinates of the enetered node
     */
    public double nodeE(int nodeId){
        return buffer.get(nodeId * 3 + OFFSET_E);
    }

    /**
     * @param nodeId the index of the node
     * @return returns the N coordinates of the enetered node
     */
    public double nodeN(int nodeId){
        return buffer.get(nodeId * 3 + OFFSET_N);
    }

    /**
     * @param nodeId the index of the node
     * @return returns the numbers of ARETE WTF EN ANGLAIS IS THAT
     */
    public int outDegree(int nodeId){
        return buffer.get(nodeId * 3 + OFFSET_OUT_EDGES);
    }

    public int edgeId(int nodeId, int edgeIndex){
        System.out.println(Integer.toBinaryString(outDegree(nodeId) >> 28));
        return (outDegree(nodeId) << 4) >> 4;
    }
}
