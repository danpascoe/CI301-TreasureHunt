package application.pascoe.com.ci301.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GameplaySQLDatabase extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "GameplayGeoHunt.db";

    private static String TABLE_POSTIONS = "tbl_positions";
    private static String POSITIONS_COL_1 = "ID";
    private static String POSITIONS_COL_2 = "HUNT_ID";
    private static String POSITIONS_COL_3 = "LAT";
    private static String POSITIONS_COL_4 = "LNG";

    private static String TABLE_CLUES = "tbl_clues";
    private static String CLUES_COL_1 = "ID";
    private static String CLUES_COL_2 = "POSITION_ID";
    private static String CLUES_COL_3 = "CLUE";

    public GameplaySQLDatabase(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String POSITIONS_SQL = "CREATE TABLE " + TABLE_POSTIONS + "(" + POSITIONS_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + POSITIONS_COL_2 + " STRING, " + POSITIONS_COL_3+ " DOUBLE, " + POSITIONS_COL_4 + "DOUBLE)";
        String CLUES_SQL =  "CREATE TABLE " + TABLE_CLUES + "(" + CLUES_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CLUES_COL_2 + " INTEGER, " + CLUES_COL_3 + " TEXT)";
        db.execSQL(POSITIONS_SQL);
        db.execSQL(CLUES_SQL);
        insertMockData();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLUES);
        onCreate(db);
    }

    public void insertMockData(){
        double[] mockLatData = {50.871909, 50.867859, 50.868561, 50.869317, 50.8700337};
        double[] mockLngData = {0.574404, 0.573043, 0.576427, 0.578672, 0.579594};
        String HUNT_ID = "default";

        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i <= mockLatData.length; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(POSITIONS_COL_2, HUNT_ID);
            contentValues.put(POSITIONS_COL_3, mockLatData[i]);
            contentValues.put(POSITIONS_COL_4, mockLngData[i]);
            db.insert(TABLE_POSTIONS, null, contentValues);
        }
    }

    public void getPositions(){

    }

    public void getClues(){

    }

}
