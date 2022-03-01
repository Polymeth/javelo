package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.nio.ByteBuffer;
import java.util.List;

public record GraphSectors(ByteBuffer buffer) {

    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

    public List<Sector> sectorsInArea(PointCh center, double distance){

    }

    public int getFirstNodeId(int nodeId){ //todo index d'octets ?
        return buffer.getInt(nodeId);
    }

    public short getNumberOfNodes(short nodeId){
        return buffer.getShort(nodeId);
    }

    public record Sector(int startNodeId, int endNodeId){

    }

}
