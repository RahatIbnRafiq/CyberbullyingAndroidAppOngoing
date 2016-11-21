package com.example.cybersafetyapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.Settings;
import android.util.Log;

import java.io.File;

/**
 * Created by RahatIbnRafiq on 11/14/2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String NAME_DATABASE = "DATABASE_CYBERSAFETYAPP.db";
    public static final String NAME_TABLE_GUARDIAN_INFORMATION = "TABLE_GUARDIAN_INFORMATION";
    public static final String NAME_TABLE_VINE_MONITORING_USER_TABLE = "TABLE_VINE_MONITORING_INFORMATION";

    public static final String NAME_COL_EMAIL = "EMAIL";
    public static final String NAME_COL_PASSWORD = "PASSWORD";
    public static final String NAME_COL_PHONE = "PHONE";
    public static final String NAME_COL_USERNAME = "USERNAME";
    public static final String NAME_COL_USERID = "USERID";

    public DatabaseHelper(Context context)
    {
        super(context, NAME_DATABASE, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+NAME_TABLE_GUARDIAN_INFORMATION +" (EMAIL TEXT PRIMARY KEY NOT NULL, PASSWORD TEXT NOT NULL, PHONE TEXT NOT NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+NAME_TABLE_VINE_MONITORING_USER_TABLE +" (EMAIL TEXT NOT NULL, USERID TEXT NOT NULL , PRIMARY KEY(EMAIL,USERID))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+NAME_TABLE_GUARDIAN_INFORMATION);
        onCreate(db);

    }

    public static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+NAME_TABLE_GUARDIAN_INFORMATION,null);
    }

    public Cursor getMonitoring(String tablename, String email)
    {
        Log.i(UtilityVariables.tag,"Inside databasehelper, getmonitoring count function");
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM "+tablename+" WHERE EMAIL = ?", new String[] {email});
    }

    public boolean checkLoginGuardian(String email,String password)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor res = db.rawQuery("SELECT * FROM "+NAME_TABLE_GUARDIAN_INFORMATION+" WHERE EMAIL = ? AND PASSWORD = ?", new String[] {email, password});
            if(res.getCount()==1)
            {
                res.close();
                return true;
            }
            res.close();
            return false;
        }
        catch (Exception e)
        {
            return false;
        }
    }


    public boolean insertRegistrationData(String email,String password,String phone)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(NAME_COL_EMAIL, email);
            contentValues.put(NAME_COL_PASSWORD, password);
            contentValues.put(NAME_COL_PHONE, phone);
            long result = db.insert(NAME_TABLE_GUARDIAN_INFORMATION, null, contentValues);

            if(result==1)
                return true;
            return false;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public long insertMonitoringTable(String email,String userid,String tableName)
    {
        try {
            Log.i(UtilityVariables.tag," database helper insert monitoring table function");
            Log.i(UtilityVariables.tag,email+","+userid+","+tableName);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(NAME_COL_EMAIL, email);
            contentValues.put(NAME_COL_USERID, userid);
            return db.insert(tableName, null, contentValues);
        }
        catch (Exception e)
        {
            return -1;

        }
    }




}
