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



    //8bit_index 8bit_index 8bit_index 8bit_index 8bit_nbnoeud 8bit nbnoeuds

    /**
     *
     * @param center Coordinates of center of radius (in PointCh format)
     * @param distance distance in meters from center
     * @return ArrayList of Sectors that have a
     */
    public List<Sector> sectorsInArea(PointCh center, double distance){
        ArrayList<Sector> sectors = new ArrayList<Sector>();

        //todo check les index si inferieur a 0 ou  superieur a 127

        double center_e = center.e(); //centre du parametre
        double center_n = center.n();

        double down_square_e = center_e - distance;
        double down_square_n = center_n - distance;

        double up_square_e = center_e + distance;
        double up_square_n = center_n + distance;

        int downsector_e = (int)((down_square_e -MIN_E)/((2.7265625)*1000)); //index du secteur en bas a gauche
        if(downsector_e < 0){
            downsector_e = 0;
        }else if (downsector_e > 127){
            downsector_e = 127;
        }
        int downsector_n = (int)((down_square_n -MIN_N)/((2.7265625)*1000)); //index du secteur en bas a gauche
        if(downsector_n < 0){
            downsector_n = 0;
        }else if (downsector_n > 127){
            downsector_n = 127;
        }

        int upsector_e = (int)(( up_square_e - MAX_E)/((2.7265625)*1000));//index du secteur en haut a droite //Todo check les bounds avec MAXE et MINE
        if(upsector_e < 0){
            upsector_e = 0;
        }else if (upsector_e > 127){
            upsector_e = 127;
        }

        int upsector_n = (int)(( up_square_n- MAX_N)/((2.7265625)*1000));
        if(upsector_n < 0){
            upsector_n = 0;
        }else if (upsector_n > 127){
            upsector_n = 127;
        }




        for(int i = downsector_e; i < upsector_e; i++){
            for(int j = downsector_n; j < upsector_n; j ++){
                int index = 128* j + i;
                int bytes_index = index * SECTOR_BYTES;
                int firstnode = buffer.getInt(bytes_index); //todo verifier unsigned
                System.out.println("Firstnode = " + firstnode);
                int lastnodes = firstnode + Short.toUnsignedInt(buffer.getShort(bytes_index + OFFSET_BYTES)); //id first node + last 2 bytes of buffer (aka number of nodes)
                System.out.println("Lastnode = " + lastnodes);
                Sector sect = new Sector(firstnode, lastnodes);
                sectors.add(sect);
            }
        }

        return sectors;

    }

    public record Sector(int startNodeId, int endNodeId){

    }

}
