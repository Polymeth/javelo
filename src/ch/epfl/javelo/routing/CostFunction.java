package ch.epfl.javelo.routing;

/**
 * Interface to create cost multiplication factor function
 *
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public interface CostFunction {

    /**
     * the cost factor function
     *
     * @param nodeId any nodeId
     * @param edgeId any edgeId
     * @return the cost multiplication factor for the desired edge
     */
    double costFactor(int nodeId, int edgeId);
}
