package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * Object representing a waypoint
 *
 * @param point the PointCh attached to the waypoint
 * @param nodeId the id of the node attached to the waypoint
 *
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public record Waypoint(PointCh point, int nodeId) {
}
