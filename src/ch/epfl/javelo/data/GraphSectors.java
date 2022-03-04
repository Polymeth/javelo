package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static ch.epfl.javelo.projection.SwissBounds.MAX_E;
import static ch.epfl.javelo.projection.SwissBounds.MIN_E;

public record GraphSectors(ByteBuffer buffer) {

    private static final int constant_byte = Short.BYTES; //2 bytes
    private static final int constant_int = Integer.BYTES; //4 bytes

    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;


    //8bit_index 8bit_index 8bit_index 8bit_index 8bit_nbnoeud 8bit nbnoeuds

    /**
     *
     * @param center
     * @param distance
     * @return
     */
    public List<Sector> sectorsInArea(PointCh center, double distance){
        ArrayList<Sector> sectors = new ArrayList<Sector>();

        double center_e = center.e(); //centre du parametre
        double center_n = center.n();

        double down_square_e = center_e - distance;
        double down_square_n = center_n - distance;

        double up_square_e = center_e + distance;
        double up_square_n = center_n + distance;

        int downsector = (int)((down_square_e -MIN_E)/((2.7265625)*1000)); //index du secteur en bas a gauche
        int upsector = (int)(( MAX_E - up_square_e)/((2.7265625)*1000)); //index du secteur en haut a droite //Todo check les bouds avec MAXE et MINE

        for(double i = - distance; i <  distance; i++){
            sectors.add(buffer.getInt(i));
        }
        return sectors;

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
