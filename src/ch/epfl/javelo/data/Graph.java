package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.*;
import java.util.function.DoubleUnaryOperator;

/**
 * Creates a whole graph with all the edge, node, sector and attribute data
 *
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class Graph {
    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;

    /**
     * Creates a whole graph with all the edge, node, sector and attribute data
     *
     * @param nodes         the GraphNodes of the graph
     * @param sectors       the GraphSectors of the graph
     * @param edges         the GraphEdges of the graph
     * @param attributeSets the complete list of AttributeSets of the graph
     */
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets) {
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = List.copyOf(attributeSets);
    }

    /**
     * @param basePath path of the folder where the .bin files are stored
     * @return a JaVelo graph made from the files data
     * @throws IOException if the files don't exist
     */
    public static Graph loadFrom(Path basePath) throws IOException {
        Path[] paths = {
                Path.of("nodes.bin"),
                Path.of("profile_ids.bin"),
                Path.of("sectors.bin"),
                Path.of("edges.bin"),
                Path.of("elevations.bin"),
                Path.of("attributes.bin")
        };

        IntBuffer nodesBuffer, profileIdsBuffer;
        ByteBuffer sectorsBuffer, edgesBuffer;
        ShortBuffer elevationsBuffer;
        LongBuffer attributesBuffer;

        try (FileChannel channel = FileChannel.open(basePath.resolve(paths[0]))) {
            nodesBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asIntBuffer();
        }
        try (FileChannel channel = FileChannel.open(basePath.resolve(paths[1]))) {
            profileIdsBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asIntBuffer();
        }
        try (FileChannel channel = FileChannel.open(basePath.resolve(paths[2]))) {
            sectorsBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asReadOnlyBuffer();
        }
        try (FileChannel channel = FileChannel.open(basePath.resolve(paths[3]))) {
            edgesBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asReadOnlyBuffer();
        }
        try (FileChannel channel = FileChannel.open(basePath.resolve(paths[4]))) {
            elevationsBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asShortBuffer();
        }
        try (FileChannel channel = FileChannel.open(basePath.resolve(paths[5]))) {
            attributesBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }

        GraphNodes graphNodes = new GraphNodes(nodesBuffer);
        GraphSectors graphSectors = new GraphSectors(sectorsBuffer);
        GraphEdges graphEdges = new GraphEdges(edgesBuffer, profileIdsBuffer, elevationsBuffer);
        ArrayList<AttributeSet> attributeSet = new ArrayList<>();
        for (int i = 0; i < attributesBuffer.capacity(); i++) {
            attributeSet.add(new AttributeSet(attributesBuffer.get(i)));
        }

        return new Graph(graphNodes, graphSectors, graphEdges, attributeSet);
    }

    /**
     * @param nodeId any nodeId
     * @return a PointCh for this node
     */
    public PointCh nodePoint(int nodeId) {
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    /**
     * @return the number of nodes
     */
    public int nodeCount() {
        return nodes.count();
    }

    /**
     * @param nodeId any nodeId
     * @return the number of out edges
     */
    public int nodeOutDegree(int nodeId) {
        return nodes.outDegree(nodeId);
    }

    /**
     * @param nodeId    any nodeId
     * @param edgeIndex any edgeIndex
     * @return the index of the edge at index nodeId on the entered edge
     */
    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return nodes.edgeId(nodeId, edgeIndex);
    }

    /**
     * @param point          any point
     * @param searchDistance a search distance
     * @return the closest node from the entered point within the search distance
     */
    public int nodeClosestTo(PointCh point, double searchDistance) {
        List<GraphSectors.Sector> sectorsWithinPoint = sectors.sectorsInArea(point, searchDistance);
        int startNodeId, endNodeId, minId = -1;
        double minDistance = Math.pow(searchDistance , 2);

        for (GraphSectors.Sector sector : sectorsWithinPoint) {
            startNodeId = sector.startNodeId();
            endNodeId = sector.endNodeId();

            for (int j = startNodeId; j < endNodeId; j++) {
                PointCh targetPoint = nodePoint(j);
                double distance = targetPoint.squaredDistanceTo(point);
                if (distance < minDistance) {
                    minDistance = distance;
                    minId = j;
                }
            }
        }
        return minId;
    }

    /**
     * @param edgeId any edgeId
     * @return the target nodeId at the entered edgeId
     */
    public int edgeTargetNodeId(int edgeId) {
        return edges.targetNodeId(edgeId);
    }

    /**
     * @param edgeId any edgeId
     * @return wether or not the edge is inverted
     */
    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }

    /**
     * @param edgeId any edgeId
     * @return return the AttributeSet of the corresponding edge
     */
    public AttributeSet edgeAttributes(int edgeId) {
        return attributeSets.get(edges.attributesIndex(edgeId));
    }

    /**
     * @param edgeId any edgeId
     * @return the length of the edge
     */
    public double edgeLength(int edgeId) {
        return edges.length(edgeId);
    }

    /**
     * @param edgeId any edgeId
     * @return the elevation gain of the edge
     */
    public double edgeElevationGain(int edgeId) {
        return edges.elevationGain(edgeId);
    }

    /**
     * @param edgeId any edgeId
     * @return the function corresponding to the profile interpolation of the edge
     */
    public DoubleUnaryOperator edgeProfile(int edgeId) {
        if (!edges.hasProfile(edgeId)) {
            return (x) -> Double.NaN;
        } else {
            return Functions.sampled(edges.profileSamples(edgeId), edges.length(edgeId));
        }
    }
}
