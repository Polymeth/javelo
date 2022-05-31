package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static ch.epfl.javelo.projection.SwissBounds.*;

/**
 * Creates the logic behind the sectors of the graph
 *
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 * @param buffer buffer containing all the information to make a GraphSector
 */
public record GraphSectors(ByteBuffer buffer) {
    private static final int SECTOR_BYTES = Integer.BYTES + Short.BYTES;
    private static final int OFFSET_E = 0;
    private static final int OFFSET_BYTES = OFFSET_E + Integer.BYTES;
    public static final double OFFSET_EAST = 2.7265625;
    public static final double OFFSET_NORTH = 1.7265625;
    public static final int M_TO_KM = 1000;
    public static final int MAX_NODE = 127;

    /**
     * @param center   Coordinates of center of radius (in PointCh format)
     * @param distance distance in meters from center
     * @return ArrayList of Sectors that are in the radius of the center
     */

    public List<Sector> sectorsInArea(PointCh center, double distance) {
        ArrayList<Sector> sectors = new ArrayList<>();

        double center_e = center.e();
        double center_n = center.n();
        double down_square_e = (center_e - distance);
        double down_square_n = (center_n - distance);
        double up_square_e = (center_e + distance);
        double up_square_n = (center_n + distance);

        // bottom right sector index
        int downsector_e = (int) ((down_square_e - MIN_E) / (OFFSET_EAST * M_TO_KM));
        downsector_e = Math2.clamp(0, downsector_e, MAX_NODE);
        int downsector_n = (int) ((down_square_n - MIN_N) / (OFFSET_NORTH * M_TO_KM));
        downsector_n = Math2.clamp(0, downsector_n, MAX_NODE);

        // top left sector index
        int upsector_e = (int) ((up_square_e - MIN_E) / (OFFSET_EAST * M_TO_KM)) + 1;
        upsector_e = Math2.clamp(0, upsector_e, MAX_NODE +1);
        int upsector_n = (int) ((up_square_n - MIN_N) / (OFFSET_NORTH * M_TO_KM)) + 1;
        upsector_n = Math2.clamp(0, upsector_n, MAX_NODE +1);

        for (int j = downsector_n; j < upsector_n; j++) {
            for (int i = downsector_e; i < upsector_e; i++) {
                int index = (MAX_NODE +1) * j + i;
                int bytes_index = index * SECTOR_BYTES;

                int firstNode = buffer.getInt(bytes_index);
                int lastNode = firstNode + Short.toUnsignedInt(buffer.getShort(bytes_index + OFFSET_BYTES));

                Sector sect = new Sector(firstNode, lastNode);
                sectors.add(sect);
            }
        }
        return sectors;
    }

    /**
     * Represent a sector, and it's associated start and end nodes
     */
    public record Sector(int startNodeId, int endNodeId) {
    }
}
