package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import java.util.*;
import java.util.List;

/**
 * Creates a pathfinding computer class
 *
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class RouteComputer {
    private final Graph graph;
    private final CostFunction costFunction;

    /**
     * Creates a pathfinding computer class
     *
     * @param graph        any graph
     * @param costFunction the cost multiplication factor function for the route
     *                     pathfinding algorithm
     */
    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;
    }

    /**
     * Implementation of A* algorithm to find shortest path from a nodeID to a given nodeID
     * Also implements a cost function to go for an optimal path with a bike
     *
     * @param startNodeId nodeID of the start of the path
     * @param endNodeId   nodeID of the end of the path
     * @return best Route to the point
     * @throws IllegalArgumentException if the start node ID is the end node ID
     *                                  (there is no route in that case)
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId) {
        Preconditions.checkArgument(startNodeId != endNodeId);

        float[] distance = new float[graph.nodeCount()];
        int[] previous = new int[graph.nodeCount()];

        PriorityQueue<WeightedNode> toExplore = new PriorityQueue<>();
        toExplore.add(new WeightedNode(startNodeId, 0));

        Arrays.fill(distance, Float.POSITIVE_INFINITY);
        distance[startNodeId] = 0;
        WeightedNode currentNode;
        PointCh endPoint = graph.nodePoint(endNodeId);

        while (!toExplore.isEmpty()) {
            // remove explored nodes
            do {
                if (!toExplore.isEmpty()) {
                    currentNode = toExplore.remove();
                } else {
                    return constructPath(previous, startNodeId, endNodeId);
                }
            } while (distance[currentNode.nodeId] == Float.NEGATIVE_INFINITY);

            // check if we arrived
            if (currentNode.nodeId == endNodeId) {
                return constructPath(previous, startNodeId, endNodeId);
            }

            // pathfinding algorithm
            for (int i = 0; i < graph.nodeOutDegree(currentNode.nodeId); i++) {
                int edgeId = graph.nodeOutEdgeId(currentNode.nodeId, i);
                int targetNodeId = graph.edgeTargetNodeId(edgeId);
                double cost = costFunction.costFactor(currentNode.nodeId, edgeId);
                double distanceToPoint = distance[currentNode.nodeId]
                        + (graph.edgeLength(edgeId)) * cost;

                if (distanceToPoint < distance[targetNodeId]) {
                    double Hcost = endPoint.distanceTo(graph.nodePoint(targetNodeId));
                    distance[targetNodeId] = (float) distanceToPoint;
                    previous[targetNodeId] = currentNode.nodeId;
                    toExplore.add(new WeightedNode(targetNodeId, (float) (distanceToPoint + Hcost)));
                }
            }
            distance[currentNode.nodeId] = Float.NEGATIVE_INFINITY;
        }
        return null;
    }

    /**
     * @param previous    the array with the parent node of each node
     * @param startNodeId the first node id
     * @param endNodeId   the last node id
     * @return the route built of the pathfinding algorithm
     */
    private Route constructPath(int[] previous, int startNodeId, int endNodeId) {
        List<Edge> path = new ArrayList<>();
        int id = endNodeId;

        while (id != startNodeId) {
            for (int i = 0; i < graph.nodeOutDegree(previous[id]); i++) {
                int targetNodeId = graph.edgeTargetNodeId(graph.nodeOutEdgeId(previous[id], i));
                if (targetNodeId == id) {
                    path.add(Edge.of(graph, graph.nodeOutEdgeId(previous[id], i), previous[id], id));
                    break;
                }
            }
            id = previous[id];
        }
        Collections.reverse(path);
        return new SingleRoute(path);
    }

    /**
     * WeightedNode that have a weighted distance and an ID associated to it
     */
    private record WeightedNode(int nodeId, float distance)
            implements Comparable<WeightedNode> {
        @Override
        public int compareTo(WeightedNode that) {
            return Float.compare(this.distance, that.distance);
        }
    }

    /**
     * @param list list you want to reverse
     * @return reverse the order of the list (the last element becomes the first etc..)
     */
    private <T> List<T> reverseOrder(List<T> list) {
        for (int i = 0; i < list.size() / 2; i++) {
            T temp = list.get(i);
            list.set(i, list.get(list.size() - i - 1));
            list.set(list.size() - i - 1, temp);
        }
        return list;
    }

}


