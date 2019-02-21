package application.pascoe.com.ci301.sqlite;

        import android.content.Context;

public class GameplaySQLManager {

    public GameplaySQLManager(Context context){
        initDB(context);
    }
    private GameplaySQLDatabase db;

    private void initDB(Context context){
        db = new GameplaySQLDatabase(context);
    }
}
