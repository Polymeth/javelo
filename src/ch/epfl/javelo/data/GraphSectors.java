package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
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
     * @return ArrayList of Sectors that are in the radius of the center
     */
    public List<Sector> sectorsInArea(PointCh center, double distance){
        ArrayList<Sector> sectors = new ArrayList<Sector>();

        double center_e = center.e(); //centre du parametre
        double center_n = center.n();

        double down_square_e = (center_e - distance);
        double down_square_n = (center_n - distance);

        double up_square_e = (center_e + distance);
        double up_square_n = (center_n + distance);

        //index du secteur en bas a gauche
        int downsector_e = (int)((down_square_e -MIN_E)/((2.7265625)*1000));
        downsector_e = Math2.clamp(0, downsector_e, 127);

        int downsector_n = (int)((down_square_n -MIN_N)/((1.7265625)*1000));
        downsector_n = Math2.clamp(0, downsector_n, 127);

        //index du secteur en haut a droite
        int upsector_e = (int)(( up_square_e - MIN_E)/((2.7265625)*1000))+1;
        upsector_e = Math2.clamp(0, upsector_e, 128);

        int upsector_n = (int)(( up_square_n- MIN_N)/((1.7265625)*1000))+1;
        upsector_n = Math2.clamp(0, upsector_n, 128);


        for(int j = downsector_n; j < upsector_n; j ++){
            for(int i = downsector_e; i < upsector_e; i++){
            int index = 128* j + i;
            int bytes_index = index * SECTOR_BYTES;

            int firstnode = buffer.getInt(bytes_index);
            int lastnodes = firstnode + Short.toUnsignedInt(buffer.getShort(bytes_index + OFFSET_BYTES)); //id first node + last 2 bytes of buffer (aka number of nodes)

            Sector sect = new Sector(firstnode, lastnodes);
            sectors.add(sect);
             }
        }

        return sectors;

    }

    public record Sector(int startNodeId, int endNodeId){

    }

}
