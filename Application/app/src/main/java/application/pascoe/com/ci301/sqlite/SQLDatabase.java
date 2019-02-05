package application.pascoe.com.ci301.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLDatabase extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "GeoHunt.db";
    public static String TABLE_NAME = "tbl_users";
    public static String COL_1 = "ID";
    public static String COL_2 = "USERNAME";
    public static String COL_3 = "PASSWORD";

    public SQLDatabase(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL = "CREATE TABLE " + TABLE_NAME + "(" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_2 + " TEXT, " + COL_3 + " TEXT)";
        db.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public String[] insertUser(String username, String password){
        if(checkUsername(username) != 0) {
            String returnInfo[] = {SQLStatus.FAILURE.toString(), "USERNAME ALREADY EXISTS"};
            return returnInfo;
        }else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_2, username);
            contentValues.put(COL_3, password);
            long result = db.insert(TABLE_NAME, null, contentValues);
            if (result != -1) {
                String returnInfo[] = {SQLStatus.SUCCESS.toString(), "USERNAME AND PASSWORD ADDED"};
                return returnInfo;
            } else {
                String returnInfo[] = {SQLStatus.FAILURE.toString(), "USER COULD NOT BE ADDED TO DATABASE"};
                return returnInfo;
            }
        }
    }

    public String[] getHashedPassword(String username){
        if(checkUsername(username) == 0) {
            String returnInfo[] = {SQLStatus.FAILURE.toString(), "USER DOES NOT EXISTS"};
            return returnInfo;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT " + COL_3 +  " FROM " + TABLE_NAME + " WHERE " + COL_2 + " = ?",new String[]{username});
        if(result.getCount() == 1){
            String returnInfo[] = {SQLStatus.SUCCESS.toString(), result.toString()};
            return returnInfo;
        }else {
            String returnInfo[] = {SQLStatus.FAILURE.toString(), "PASSWORD COULD NOT BE RETRIEVED"};
            return returnInfo;
        }
    }

    public int checkUsername(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT " + COL_2 + " FROM " + TABLE_NAME + " WHERE " + COL_2 + " = ?",new String[]{username});
        return result.getCount();
    }
}
