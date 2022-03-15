package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public final class Graph {

    private GraphNodes nodes;
    private GraphSectors sectors;
    private GraphEdges edges;
    private List<AttributeSet> attributeSets;

    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets){
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = List.copyOf(attributeSets);
    }

    /**
     * @param basePath path of the folder where the .bin files are stored
     * @return a JaVelo graph made from the files data
     * @throws IOException
     */
    public static Graph loadFrom(Path basePath) throws IOException {
        Path nodesPath = Path.of("nodes.bin");

        // todo: ne pas mettre dans une liste?
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

        // todo faire une fonction avec le try with ressources
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

        //Arrays.stream(attributesBuffer.array() )

        //todo : lambda
        for (int i = 0; i < attributesBuffer.capacity(); i++) {
            attributeSet.add(new AttributeSet(attributesBuffer.get(i)));
        }

        return new Graph(graphNodes, graphSectors, graphEdges, attributeSet);
    }

    public PointCh nodePoint(int nodeId){
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    public int nodeCount(){
        return nodes.count();
    }

    public int nodeOutDegree(int nodeId){
        return nodes.outDegree(nodeId);
    }

    public int nodeOutEdgeId(int nodeId, int edgeIndex){
        return nodes.edgeId(nodeId, edgeIndex);
    }

    public int nodeClosestTo(PointCh point, double searchDistance){
        List<GraphSectors.Sector> sectorsWithinPoint = sectors.sectorsInArea(point, searchDistance);
        int startNodeId, endNodeId, minId = -1;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < sectorsWithinPoint.size(); i++) {
            startNodeId = sectorsWithinPoint.get(i).startNodeId();
            endNodeId = sectorsWithinPoint.get(i).endNodeId();
            for (int j = startNodeId; j <= endNodeId; j++) {
                PointCh targetPoint = new PointCh(nodes.nodeE(j), nodes.nodeN(j));
                double distance = targetPoint.squaredDistanceTo(point);
                if (distance < minDistance) {
                    minDistance = distance;
                    minId = j;
                }
            }
        }
        return minId;
    }

    public int edgeTargetNodeId(int edgeId){
        return edges.targetNodeId(edgeId);
    }

    public boolean edgeIsInverted(int edgeId){
        return edges.isInverted(edgeId);
    }

    public AttributeSet edgeAttributes(int edgeId){
        return attributeSets.get(edges.attributesIndex(edgeId));
    }

    public double edgeLength(int edgeId){
        return edges.length(edgeId);
    }

    public double edgeElevationGain(int edgeId){
        return edges.elevationGain(edgeId);
    }

    public DoubleUnaryOperator edgeProfile(int edgeId){
        if (!edges.hasProfile(edgeId)) {
            return (x) -> Double.NaN;
        } else {
            return Functions.sampled(edges.profileSamples(edgeId), edges.length(edgeId));
        }
    }

}
