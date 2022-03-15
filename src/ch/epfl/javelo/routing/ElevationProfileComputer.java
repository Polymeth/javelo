package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;

import java.util.ArrayList;
import java.util.List;

public final class ElevationProfileComputer {

    private ElevationProfileComputer() {}

    public ElevationProfile elevationProfile(Route route, double maxStepLength){
        //todo: preconditions+check max length
        ArrayList<Double> edgesElevations = new ArrayList<Double>();
        int stepNumber = (int)Math.ceil(route.length() / maxStepLength);
        double distanceBetweenPoints = route.length() / stepNumber;

        for (int i = 0; i < route.edges().size(); i++) {
            edgesElevations.add(route.edges().get(i).elevationAt(distanceBetweenPoints*i);
        }
    }
}
