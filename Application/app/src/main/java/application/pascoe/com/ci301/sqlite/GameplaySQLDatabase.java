package application.pascoe.com.ci301.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

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
        super(context, DATABASE_NAME, null, 7);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String POSITIONS_SQL = "CREATE TABLE " + TABLE_POSTIONS + "(" + POSITIONS_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + POSITIONS_COL_2 + " TEXT, " + POSITIONS_COL_3+ " DOUBLE, " + POSITIONS_COL_4 + " DOUBLE)";
        String CLUES_SQL =  "CREATE TABLE " + TABLE_CLUES + "(" + CLUES_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CLUES_COL_2 + " INTEGER, " + CLUES_COL_3 + " TEXT)";
        db.execSQL(POSITIONS_SQL);
        db.execSQL(CLUES_SQL);
        insertMockData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLUES);
        onCreate(db);
    }

    private void insertMockData(SQLiteDatabase db){
        String HUNT_ID = "default";
        double[] mockLatData = {50.871909, 50.867859, 50.868561, 50.869317, 50.8700337};
        double[] mockLngData = {0.574404, 0.573043, 0.576427, 0.578672, 0.579594};

        String[] mockClues = {"Top Ash. Road", "Outside Kirsty's", "On the Road"
                , "Mid Ash. Road", "Outside Grans"
                , "My Primary School", "Blacklands Primary", "Osbourne Close"
                , "Bus Stop"
                , "Woodbrook Road", "Home"};

        int[] mockCluePosID = {1, 1, 1, 2, 2, 3, 3, 3, 4, 5, 5 };
        for (int i = 0; i < mockLatData.length; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(POSITIONS_COL_2, HUNT_ID);
            contentValues.put(POSITIONS_COL_3, mockLatData[i]);
            contentValues.put(POSITIONS_COL_4, mockLngData[i]);
            db.insert(TABLE_POSTIONS, null, contentValues);
        }

        for (int i = 0; i < mockClues.length; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CLUES_COL_2, mockCluePosID[i]);
            contentValues.put(CLUES_COL_3, mockClues[i]);
            db.insert(TABLE_CLUES, null, contentValues);
        }
    }

    public double[] getLat(String HUNT_ID){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + POSITIONS_COL_3 +  " FROM " + TABLE_POSTIONS + " WHERE " + POSITIONS_COL_2 + " = ?",new String[]{HUNT_ID});
        int count = cursor.getCount();
        double[] LatQryResults = new double[count];
        int i = 0;
        while(cursor.moveToNext()){
            Double Lat = cursor.getDouble(cursor.getColumnIndex(POSITIONS_COL_3));
            LatQryResults[i] = Lat;
            i++;
        }
        return LatQryResults;
    }

    public double[] getLng(String HUNT_ID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + POSITIONS_COL_4 +  " FROM " + TABLE_POSTIONS + " WHERE " + POSITIONS_COL_2 + " = ?",new String[]{HUNT_ID});
        int count = cursor.getCount();
        double[] LngQryResults = new double[count];
        int i = 0;
        while(cursor.moveToNext()){
            Double Lng = cursor.getDouble(cursor.getColumnIndex(POSITIONS_COL_4));
            LngQryResults[i] = Lng;
            i++;
        }
        return LngQryResults;
    }

    public int[] getPositionID(String HUNT_ID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + POSITIONS_COL_1 +  " FROM " + TABLE_POSTIONS + " WHERE " + POSITIONS_COL_2 + " = ?",new String[]{HUNT_ID});
        int count = cursor.getCount();
        int[] PositionID = new int[count];
        int i = 0;
        while(cursor.moveToNext()){
            int ID = cursor.getInt(cursor.getColumnIndex(POSITIONS_COL_1));
            PositionID[i] = ID;
            i++;
        }
        return PositionID;
    }

    public String[] getClues(int currentPositionID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + CLUES_COL_3 +  " FROM " + TABLE_CLUES + " WHERE " + CLUES_COL_2+ " = ?", new String[]{String.valueOf(currentPositionID)});
        int count = cursor.getCount();
        String[] clues = new String[count];
        int i = 0;
        while(cursor.moveToNext()){
            clues[i] = cursor.getString(cursor.getColumnIndex(CLUES_COL_3));
            i++;
        }
        return clues;
    }

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "message" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);

            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {

                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }
    }
}