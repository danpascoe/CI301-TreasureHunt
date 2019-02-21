package application.pascoe.com.ci301.gameplay;

import android.content.Context;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import application.pascoe.com.ci301.constants.Constants;
import application.pascoe.com.ci301.sqlite.GameplaySQLDatabase;

public class Gameplay {

    private GameplaySQLDatabase db;
    private LatLng[] LatLngArr;
    private int[] positionID;
    private int currentPosition;
    private int totalPositions;

    public void GameInitiate(Context context, String huntID){
        db = new GameplaySQLDatabase(context);
        getGameInfo(huntID);
        currentPosition = 0;
    }

    private void getGameInfo(String huntID){
        if(huntID != "default"){ huntID = "default";}
        LatLngArr = getPositions(huntID);
    }

    private LatLng[] getPositions(String HUNT_ID){
        double[] Lat = db.getLat(HUNT_ID);
        double[] Lng = db.getLng(HUNT_ID);
        LatLng[] positions = new LatLng[Lat.length]; //Lat.Count

        if(Lat.length != 0){
            for (int i = 0; i < Lat.length ; i++) {
                positions[i] = new LatLng(Lat[i], Lng[i]);
            }
        }
        positionID = db.getPositionID(HUNT_ID);
        totalPositions = positions.length;
        return positions;
    }

    public boolean checkDistance(LatLng currentUserPosition) {
        double distanceCheck = SphericalUtil.computeDistanceBetween(currentUserPosition, LatLngArr[currentPosition]);
        if(distanceCheck <= Constants.MIN_PLAYER_TO_CLUE_DISTANCE) {
            currentPosition++;
            return true;
        }else{
            return false;
        }
    }

    public String[] getClues(){
        String[] currentClueSet = db.getClues(positionID[currentPosition]);
        return currentClueSet;
    }
}