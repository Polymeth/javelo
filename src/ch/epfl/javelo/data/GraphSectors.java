package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static ch.epfl.javelo.projection.SwissBounds.*;

public record GraphSectors(ByteBuffer buffer) {

    private static final int constant_byte = Short.BYTES; //2 bytes
    private static final int constant_int = Integer.BYTES; //4 bytes
    private static final int SECTOR_BYTES = Integer.BYTES + Short.BYTES;

    private static final int OFFSET_E = 0; //todo redefinir les constantes en fonction de bytes et non d'int
    private static final int OFFSET_BYTES = OFFSET_E + Integer.BYTES;
    private static final int OFFSET_OUT_EDGES = OFFSET_BYTES + 1;
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

        int downsector_e = (int)((down_square_e -MIN_E)/((2.7265625)*1000)); //index du secteur en bas a gauche
        int downsector_n = (int)((down_square_n -MIN_N)/((2.7265625)*1000)); //index du secteur en bas a gauche

        int upsector_e = (int)(( MAX_E - up_square_e)/((2.7265625)*1000)); //index du secteur en haut a droite //Todo check les bounds avec MAXE et MINE
        int upsector_n = (int)(( MAX_N - up_square_n)/((2.7265625)*1000));




        for(int i = downsector_e; i < upsector_e; i++){
            for(int j = downsector_n; j < upsector_n; j ++){
                int index = 128* j + i;
                int bytes_index = index * SECTOR_BYTES;
                int firstnode = buffer.getInt(bytes_index); //todo verifier unsigned
                int lastnodes = firstnode + Short.toUnsignedInt(buffer.getShort(bytes_index + OFFSET_BYTES)); //id first node + last 2 bytes of buffer (aka number of nodes)

                Sector sect = new Sector(firstnode, lastnodes);
                sectors.add(sect);
            }
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
