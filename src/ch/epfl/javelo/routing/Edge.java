package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length, DoubleUnaryOperator profile) {

    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {

        PointCh frompoint1 = graph.nodePoint(fromNodeId);
        PointCh topoint1 = graph.nodePoint(toNodeId);
        double edgelength = graph.edgeLength(edgeId);
        DoubleUnaryOperator profile = graph.edgeProfile(edgeId);

        Edge edge = new Edge(fromNodeId, toNodeId, frompoint1, topoint1, edgelength, profile);

        return edge;
    }

    public double positionClosestTo(PointCh point) {
        double ax = fromPoint.e()
        double ay = fromPoint.n()
        double bx = toPoint.e()
        double by = toPoint.n()
        double px = point.e()
        double py = point.n()


        return Math2.projectionLength(ax, ay, bx, by, px, py);
    }

    public PointCh pointAt(double position) {
        return null;
    }

    public double elevationAt(double position) {
        return 0;
    }
}
