package application.pascoe.com.ci301.gameplay;

        import android.content.Context;

        import com.google.android.gms.maps.model.LatLng;
        import com.google.maps.android.SphericalUtil;
        import application.pascoe.com.ci301.constants.Constants;
        import application.pascoe.com.ci301.sqlite.GameplaySQLManager;

public class Gameplay {

    public static LatLng[] LatLngArr;
    public static int[] positionID;
    private static GameplaySQLManager sqlManager;

    public static void GameInitiate(){

        getGameInfo();
    }

    public static void getGameInfo(){

    }

    public static boolean checkDistance(LatLng pos) {
        return false;
    }

    public static String getClue(){
        String[] currentClueSet = {};
        return "";
    }
}
