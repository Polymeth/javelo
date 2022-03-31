package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import java.util.*;
import java.util.List;

public final class RouteComputer {
    private final Graph graph;
    private final CostFunction costFunction;

    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;
    }

    public Route bestRouteBetween(int startNodeId, int endNodeId){
        Preconditions.checkArgument(startNodeId != endNodeId);

        float[] distance = new float[graph.nodeCount()];
        int[] previous = new int[graph.nodeCount()];

        PriorityQueue<WeightedNode> toExplore = new PriorityQueue<>();
        toExplore.add(new WeightedNode(startNodeId, 0));

        Arrays.fill(distance, Float.POSITIVE_INFINITY);
        distance[startNodeId] = 0;
        int debug = 0;
        WeightedNode currentNode;
        PointCh endPoint = graph.nodePoint(endNodeId);

        while(!(toExplore.isEmpty())) {
            // todo: attention au cas où le while arrive à un cas sans toexplore
            do {
                currentNode = toExplore.remove();
            } while (distance[currentNode.nodeId] == Float.NEGATIVE_INFINITY);

            if (currentNode.nodeId == endNodeId) {
                return constructPath(previous, startNodeId, endNodeId);
            }

            for (int i = 0; i < graph.nodeOutDegree(currentNode.nodeId); i++) {
                int edgeId = graph.nodeOutEdgeId(currentNode.nodeId, i);
                int targetNodeId = graph.edgeTargetNodeId(edgeId);
                double Hcost = endPoint.distanceTo(graph.nodePoint(currentNode.nodeId));
                double cost = costFunction.costFactor(currentNode.nodeId, edgeId);
                double distanceToPoint = distance[currentNode.nodeId]
                        + (graph.edgeLength(edgeId)) * cost;

                if (distanceToPoint < distance[targetNodeId]) {
                    distance[targetNodeId] = (float)distanceToPoint;
                    previous[targetNodeId] = currentNode.nodeId;
                    toExplore.add(new WeightedNode(targetNodeId, (float)(distanceToPoint+Hcost)));
                }
            }
            distance[currentNode.nodeId] = Float.NEGATIVE_INFINITY;
        }
        return null;
    }

    private Route constructPath(int[] previous, int startNodeId, int endNodeId) {
        WeightedNode rNode;
        List<Edge> path = new ArrayList<>();
        int id = endNodeId;
        while (id != startNodeId) {
            path.add(Edge.of(graph, 0, id, previous[id]));
            id = previous[id];
        }
        Collections.reverse(path);
        return new SingleRoute(path);
    }

    private record WeightedNode(int nodeId, float distance)
            implements Comparable<WeightedNode> {
        @Override
        public int compareTo(WeightedNode that) {
            return Float.compare(this.distance, that.distance);
        }
    }
}


