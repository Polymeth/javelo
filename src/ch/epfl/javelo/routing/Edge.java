package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

/**
 * Create an edge which a part of the route between two nodes
 *
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length,
                   DoubleUnaryOperator profile) {

    /**
     * Creates an edge using two nodeId
     *
     * @param graph      graph you want the instance of edges
     * @param edgeId     given id of edge
     * @param fromNodeId given id of starting node
     * @param toNodeId   given id of end node
     * @return instance of Edge with given parameters
     */
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {
        return new Edge(fromNodeId, toNodeId,
                graph.nodePoint(fromNodeId), graph.nodePoint(toNodeId),
                graph.edgeLength(edgeId), graph.edgeProfile(edgeId));
    }

    /**
     * @param point any point
     * @return the closest position of the desired point on the edge
     */
    public double positionClosestTo(PointCh point) {
        return Math2.projectionLength(fromPoint.e(), fromPoint.n(),
                toPoint.e(), toPoint.n(), point.e(), point.n());
    }

    /**
     * @param position the position (in meters)
     * @return a PointCh at the entered distance on the edge
     */
    public PointCh pointAt(double position) {
        if (length == 0) return fromPoint;
        double progression = position / length;
        return new PointCh(Math2.interpolate(fromPoint.e(), toPoint.e(), progression),
                Math2.interpolate(fromPoint.n(), toPoint.n(), progression));
    }

    /**
     * @param position the position (in meters)
     * @return the elevation at the desired position
     */
    public double elevationAt(double position) {
        return profile.applyAsDouble(position);
    }
}
