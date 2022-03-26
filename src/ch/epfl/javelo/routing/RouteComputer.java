package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.PriorityQueue;

// immuable
public final class RouteComputer {
    final Graph graph;

    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
    }

    public Route bestRouteBetween(int startNodeId, int endNodeId){
        Preconditions .checkArgument(startNodeId != endNodeId);

        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

        // ******************
        // disjktra algorithm
        // ******************

        double[] distance = new double[graph.nodeCount()];
        int[] previous = new int[graph.nodeCount()];

        PriorityQueue<WeightedNode> toExplore = new PriorityQueue<>();
        toExplore.add(new WeightedNode(startNodeId, 0));

        // initialisation des tableaux
        distance[startNodeId] = 0;
        Arrays.fill(distance, Float.POSITIVE_INFINITY);
        Arrays.fill(previous, 0);

        // trouver le plus court chemin
        WeightedNode currentNode;
        while(!(toExplore.isEmpty())) {
            currentNode = toExplore.remove();

            // si la node n'est pas celle d'arrivée
            if (!(currentNode.nodeId == endNodeId)) {

                // Itere sur le nombre d'edges sortant d'un node donné
                for (int i = 0; i < graph.nodeOutDegree(currentNode.nodeId); i++) {
                    double distanceToPoint = distance[currentNode.nodeId] + graph.edgeLength(graph.nodeOutEdgeId(currentNode.nodeId, i)); //distance du node id avant + longuer du graph courrant
                    int targetNodeId = graph.edgeTargetNodeId(graph.nodeOutEdgeId(currentNode.nodeId, i));

                    // si la distance est plus petite que la distance la plus petite connue
                    if (distanceToPoint < distance[targetNodeId]) {
                        distance[targetNodeId] = distanceToPoint;
                        previous[targetNodeId] = currentNode.nodeId;

                        // alors on explore ce node
                        toExplore.add(new WeightedNode(targetNodeId, (float)distanceToPoint));
                    }
                }
            }
        }

        // remonter le plus court chemin


        return null;
    }
}
