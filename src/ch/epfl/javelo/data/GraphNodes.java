package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.IntBuffer;

/**
 * Creates the logic behind the nodes of the graph
 *
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
    public int count() {
        return buffer.capacity() / NODE_INTS;
    }

    /**
     * @param nodeId the index of the node
     * @return returns the E coordinates of the entered node
     */
    public double nodeE(int nodeId) {
        return Q28_4.asDouble(buffer.get(nodeId * 3 + OFFSET_E));
    }

    /**
     * @param nodeId the index of the node
     * @return returns the N coordinates of the entered node
     */
    public double nodeN(int nodeId) {
        return Q28_4.asDouble(buffer.get(nodeId * 3 + OFFSET_N));
    }

    /**
     * @param nodeId the index of the node
     * @return returns the numbers of edges
     */
    public int outDegree(int nodeId) {
        return Bits.extractUnsigned(buffer.get(nodeId * 3 + OFFSET_OUT_EDGES), 28, 4);
    }

    /**
     * @param nodeId    the index of the node
     * @param edgeIndex the index of the edge you want in this node
     * @return returns the global index of the edge id you want on this node
     */
    public int edgeId(int nodeId, int edgeIndex) {
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        return Bits.extractUnsigned(buffer.get(nodeId * 3 + OFFSET_OUT_EDGES), 0, 28) + edgeIndex;
    }
}
