package application.pascoe.com.ci301;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

public class Gameplay {

    public static double[] clueLocationsLat = {50.871};
    public static double[] clueLocationsLng = {0.578};
    private String[] clues = {};
    private static int currentClue = 0;

    public static boolean checkDistance(LatLng currentPos){
        LatLng cluePos = new LatLng(clueLocationsLat[currentClue], clueLocationsLng[currentClue]);
        double distanceCheck = SphericalUtil.computeDistanceBetween(currentPos, cluePos);
        if(distanceCheck <= Constants.MIN_PLAYER_TO_CLUE_DISTANCE){
            //currentClue++;
            return true;
        }else {
            return false;
        }
    }
}
