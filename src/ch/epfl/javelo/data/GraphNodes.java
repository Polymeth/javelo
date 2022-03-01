package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.awt.*;
import java.nio.IntBuffer;

public record GraphNodes(IntBuffer buffer) {

    public GraphNodes{
        buffer = IntBuffer.wrap(new int[]{PointCh.e(), PointCh.n()}) //todo choper la totalité des noeuds et extraire leur valeur e, n
    }

    public int count(){

    }

    public double nodeE(int nodeId){

    }

    public double nodeN(int nodeId){

    }

    public int outDegree(int nodeId){

    }

    public int edgeId(int nodeId, int edgeIndex){

    }
}
