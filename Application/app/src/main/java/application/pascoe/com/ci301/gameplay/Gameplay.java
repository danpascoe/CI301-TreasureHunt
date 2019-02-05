package application.pascoe.com.ci301.gameplay;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import application.pascoe.com.ci301.constants.Constants;

public class Gameplay {

    public static double[] clueLocationsLat = {50.871909, 50.867859, 50.868561, 50.869317, 50.8700337 , 0.4545};
    public static double[] clueLocationsLng = {0.574404, 0.573043, 0.576427, 0.578672, 0.579594, 75.000};
    private static String[] clues = {"Outside Kirsty's House", "Outside Grans House", "Primary School", "Kepel Road Bus Stop", "Home", "OUT OF RANGE CLUE FOR DEBUG", "YOU DID IT!"};
    private static int currentClue = 0;

    public static int GameInitiate(){
        /*
        * CODE TO GET THE POSITIONS AND CLUES WOULD BE ADDED HERE
        */
        int totalPositions = clueLocationsLat.length;
        return totalPositions;
    }

    public static boolean checkDistance(LatLng currentPos){
        LatLng cluePos = new LatLng(clueLocationsLat[currentClue], clueLocationsLng[currentClue]);
        double distanceCheck = SphericalUtil.computeDistanceBetween(currentPos, cluePos);
        if(distanceCheck <= Constants.MIN_PLAYER_TO_CLUE_DISTANCE){
            currentClue++;
            return true;
        }else {
            return false;
        }
    }

    public static String[] getClue() {
        String currentClueText = Integer.toString(currentClue + 1);
        String[] arrClue = {currentClueText, clues[currentClue]};
        return arrClue;
    }
}
