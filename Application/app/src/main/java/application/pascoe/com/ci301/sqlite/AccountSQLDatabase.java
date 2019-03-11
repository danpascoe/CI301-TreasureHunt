//https://stackoverflow.com/questions/39350466/does-sqlitedatabaserawquery-escape-the-selectionargs-is-it-safe

package application.pascoe.com.ci301.sqlite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import application.pascoe.com.ci301.utility.Status;

public class AccountSQLDatabase extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "UserGeoHunt.db";
    private static String TABLE_USERS = "tbl_users";
    private static String USERS_COL_1 = "ID";
    private static String USERS_COL_2 = "USERNAME";
    private static String USERS_COL_3 = "PASSWORD";

    public AccountSQLDatabase(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String USER_SQL = "CREATE TABLE " + TABLE_USERS + "(" + USERS_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + USERS_COL_2 + " TEXT, " + USERS_COL_3 + " TEXT)";
        db.execSQL(USER_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public String[] insertUser(String username, String password){
        if(checkUsername(username) != 0) {
            String returnInfo[] = {Status.FAILED.toString(), "USERNAME ALREADY EXISTS"};
            return returnInfo;
        }else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(USERS_COL_2, username);
            contentValues.put(USERS_COL_3, password);
            long result = db.insert(TABLE_USERS, null, contentValues);
            if (result != -1) {
                String returnInfo[] = {Status.SUCCESS.toString(), "USERNAME AND PASSWORD ADDED"};
                return returnInfo;
            } else {
                String returnInfo[] = {Status.FAILED.toString(), "USER COULD NOT BE ADDED TO DATABASE"};
                return returnInfo;
            }
        }
    }

    public String[] getHashedPassword(String username){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + USERS_COL_3 +  " FROM " + TABLE_USERS + " WHERE " + USERS_COL_2 + " = ?", new String[]{username});

        if(cursor.moveToFirst()) {
            String hashedPassword = cursor.getString(0);
            String returnInfo[] = {Status.SUCCESS.toString(), hashedPassword};
            return returnInfo;
        }else{
            String returnInfo[] = {Status.FAILED.toString(), "PASSWORD COULD NOT BE RETRIEVED"};
            return returnInfo;
        }
    }

    public int checkUsername(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT " + USERS_COL_2 + " FROM " + TABLE_USERS + " WHERE " + USERS_COL_2 + " = ?",new String[]{username});
        int resultCount = result.getCount();
        return resultCount;
    }
}
