package application.pascoe.com.ci301.gameplay;

        import android.content.Context;

        import com.google.android.gms.maps.model.LatLng;
        import com.google.maps.android.SphericalUtil;
        import application.pascoe.com.ci301.constants.Constants;
        import application.pascoe.com.ci301.sqlite.SQLManager;

public class Gameplay {

    public static LatLng[] LatLngList;
    public static LatLng[] clueLocations = {new LatLng(50.871909,0.574404)
                                            ,new LatLng(50.867859,0.573043)
                                            ,new LatLng(50.868561,0.576427)
                                            ,new LatLng(50.869317,0.578672)
                                            ,new LatLng(50.8700337,0.579594)};

    private static String[] clues = {"Outside Kirsty's House", "Outside Gran's House", "Primary School", "Keppel Road Bus Stop", "Home", "OUT OF RANGE CLUE FOR DEBUG", "YOU DID IT!"};
    private static int currentClue = 0;
    private static SQLManager SQLManager;

    public static int GameInitiate(Context context){

        SQLManager = SQLManager.getInstance(context);

        getPositionList();

        return clueLocations.length;
    }

    public static boolean checkDistance(LatLng currentPos){
        double distanceCheck = SphericalUtil.computeDistanceBetween(currentPos, clueLocations[currentClue]);
        if(distanceCheck <= Constants.MIN_PLAYER_TO_CLUE_DISTANCE){
            currentClue++;
            return true;
        }else {
            return false;
        }
    }

    public static void getPositionList(){

    }


    public static String[] getClue() {
        String currentClueText = Integer.toString(currentClue + 1);
        String[] arrClue = {currentClueText, clues[currentClue]};
        return arrClue;
    }



}
