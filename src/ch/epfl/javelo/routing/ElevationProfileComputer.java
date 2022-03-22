package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ElevationProfileComputer {

    private ElevationProfileComputer() {}

    public static ElevationProfile elevationProfile(Route route, double maxStepLength){

        Preconditions.checkArgument(maxStepLength > 0); //check si maxsteplength est positif
        //todo: preconditions+check max length



        int stepNumber = (int)Math.ceil(route.length() / maxStepLength) +1; //nombre d'echantilons
        double distanceBetweenPoints = route.length() / stepNumber; //distance entre les points

        ArrayList<Double> edgesElevations = new ArrayList<Double>();
        float[] edgesElevations2 = new float[stepNumber];

        //remplissage du tableau
        for (int i = 0; i < stepNumber; i++) {
            //edgesElevations.add(route.elevationAt(distanceBetweenPoints*i));
            edgesElevations2[i] = (float)route.elevationAt(distanceBetweenPoints*i);
        }

        //cas si que des Nan
        int counter = 0;
        for(int i=0; i< edgesElevations2.length; i++){
            if(Float.isNaN(edgesElevations2[i])){
                counter++;
            }
        }
        if(counter == edgesElevations2.length){
            for (int i=0; i< edgesElevations2.length; i++){
                edgesElevations2[i] = 0;
            }
        }

        //todo verifier condition pour trouver les nan
        //BOucher les trous en tete en prenant la première valeur non nan et en bouchant
        for(int i = 0; Float.isNaN(edgesElevations2[i]); i++){
            Arrays.fill(edgesElevations2, 0, i+1, edgesElevations2[i+1]);
        }

        /*
        for(int i = 0; i < edgesElevations2.length; i++){
            if(!Float.isNaN((float)edgesElevations2[i]) && i != 0){ //première valeur non nan
                    Arrays.fill(edgesElevations2, 0, i, edgesElevations2[i]);
            }
        }

         */


        //Boucher les trous en fin en prenant la première valeur non nan
        for(int j = edgesElevations2.length-1; Float.isNaN(edgesElevations2[j]); j--){
            Arrays.fill(edgesElevations2, j, edgesElevations2.length , edgesElevations2[j-1]);
        }
        /*
        for(int j = edgesElevations2.length-1 ; j == 0; j--){
            if(!Float.isNaN((float)edgesElevations2[j]) && j != edgesElevations2.length-1){
                Arrays.fill(edgesElevations2, j,edgesElevations2.length-1 , edgesElevations2[j]);
            }
        }

         */
        int counter2 = 0;
        int begin = 0;
        int end = 0;
        float v2 = 0f;
        float v1 = 0f;
        boolean alreadyFound = false;
        //boucher les trous du milleu en interpollant avec les points avant et apres
        for(int k = 0; k < edgesElevations2.length; k++){
            if(Float.isNaN(edgesElevations2[k])){
                begin = k-1;
                v1 = edgesElevations2[k-1];
                for (int i = begin + 1; i < edgesElevations2.length; i++) {
                    if(!Float.isNaN(edgesElevations2[i]) && !alreadyFound){
                        end =i;
                        v2 = edgesElevations2[i];
                        alreadyFound = true;
                    }
                }
                for(int p = begin + 1; p < end; p++){ //todo fix interpolate
                    edgesElevations2[p] = (float)Math2.interpolate(v1, v2, (double)(p-begin)/(end-begin));
                }

            }


            /*
            if(Float.isNaN((float)edgesElevations2[k])){
                edgesElevations2[k] = (float)Math2.interpolate(edgesElevations2[k-1], edgesElevations2[k+1], 0.5);
            }

             */
        }


        ElevationProfile profile = new ElevationProfile(route.length(), edgesElevations2);

        return profile;
    }
     public static float[] tab(float[] edgesElevations2, int samples){
        int counter = 0;
        for(int i=0; i< edgesElevations2.length; i++){
            if(Float.isNaN(edgesElevations2[i])){
                counter++;
            }
        }
        if(counter == edgesElevations2.length){
            for (int i=0; i< edgesElevations2.length; i++){
                edgesElevations2[i] = 0;
            }
        }

        //todo verifier condition pour trouver les nan
        //BOucher les trous en tete en prenant la première valeur non nan et en bouchant
        for(int i = 0; Float.isNaN(edgesElevations2[i]); i++){
            Arrays.fill(edgesElevations2, 0, i+1, edgesElevations2[i+1]);
        }

        /*
        for(int i = 0; i < edgesElevations2.length; i++){
            if(!Float.isNaN((float)edgesElevations2[i]) && i != 0){ //première valeur non nan
                    Arrays.fill(edgesElevations2, 0, i, edgesElevations2[i]);
            }
        }

         */


        //Boucher les trous en fin en prenant la première valeur non nan
        for(int j = edgesElevations2.length-1; Float.isNaN(edgesElevations2[j]); j--){
            Arrays.fill(edgesElevations2, j, edgesElevations2.length , edgesElevations2[j-1]);
        }
        /*
        for(int j = edgesElevations2.length-1 ; j == 0; j--){
            if(!Float.isNaN((float)edgesElevations2[j]) && j != edgesElevations2.length-1){
                Arrays.fill(edgesElevations2, j,edgesElevations2.length-1 , edgesElevations2[j]);
            }
        }

         */
        int counter2 = 0;
        int begin = 0;
        int end = 0;
        float v2 = 0f;
        float v1 = 0f;
        boolean alreadyFound = false;
        //boucher les trous du milleu en interpollant avec les points avant et apres
        for(int k = 0; k < edgesElevations2.length; k++){
            if(Float.isNaN(edgesElevations2[k])){
                begin = k-1;
                v1 = edgesElevations2[k-1];
                for (int i = begin + 1; i < edgesElevations2.length; i++) {
                    if(!Float.isNaN(edgesElevations2[i]) && !alreadyFound){
                        end =i;
                        v2 = edgesElevations2[i];
                        alreadyFound = true;
                    }
                }
                for(int p = begin + 1; p < end; p++){ //todo fix interpolate
                    edgesElevations2[p] = (float)Math2.interpolate(v1, v2, (double)(p-begin)/(end-begin));
                    alreadyFound = false;
                }

            }


            /*
            if(Float.isNaN((float)edgesElevations2[k])){
                edgesElevations2[k] = (float)Math2.interpolate(edgesElevations2[k-1], edgesElevations2[k+1], 0.5);
            }

             */
        }


        return edgesElevations2;
    }
}
