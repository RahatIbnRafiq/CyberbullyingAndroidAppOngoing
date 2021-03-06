package com.example.cybersafetyapp.HelperClassesPackage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.cybersafetyapp.UtilityPackage.UtilityVariables;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by RahatIbnRafiq on 11/14/2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String NAME_DATABASE = "DATABASE_CYBERSAFETYAPP.db";
    public static final String NAME_TABLE_GUARDIAN_INFORMATION = "TABLE_GUARDIAN_INFORMATION";


    public static final String NAME_TABLE_VINE_MONITORING_USER_TABLE = "TABLE_VINE_MONITORING_USER_INFORMATION";
    public static final String NAME_TABLE_VINE_MONITORING_POST_TABLE = "TABLE_VINE_MONITORING_POST_INFORMATION";


    public static final String NAME_TABLE_INSTAGRAM_MONITORING_USER_TABLE = "TABLE_INSTAGRAM_MONITORING_USER_INFORMATION";
    public static final String NAME_TABLE_INSTAGRAM_MONITORING_POST_TABLE = "TABLE_INSTAGRAM_MONITORING_POST_INFORMATION";

    public static final String NAME_COL_EMAIL = "EMAIL";
    public static final String NAME_COL_PASSWORD = "PASSWORD";
    public static final String NAME_COL_PHONE = "PHONE";
    public static final String NAME_COL_USERNAME = "USERNAME";
    public static final String NAME_COL_USERID = "USERID";
    public static final String NAME_COL_POSTID = "POSTID";
    public static final String NAME_COL_CREATEDTIME = "CREATEDTIME";
    public static final String NAME_COL_LASTTIMECHECKED = "LASTTIMECHECKED";
    public static final String NAME_COL_LINK = "LINK";

    public static final String NAME_COL_VINE_TOKEN = "VINE_TOKEN";
    public static final String NAME_COL_INSTAGRAM_TOKEN = "INSTAGRAM_TOKEN";
    public static final String NAME_COL_TWITTER_TOKEN = "TWITTER_TOKEN";
    public static final String NAME_COL_FACEBOOK_TOKEN = "FACEBOOK_TOKEN";

    public DatabaseHelper(Context context)
    {
        super(context, NAME_DATABASE, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+NAME_TABLE_GUARDIAN_INFORMATION +" (EMAIL TEXT PRIMARY KEY NOT NULL, PASSWORD TEXT NOT NULL, PHONE TEXT NOT NULL, VINE_TOKEN TEXT, INSTAGRAM_TOKEN TEXT, TWITTER_TOKEN TEXT, FACEBOOK_TOKEN TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+NAME_TABLE_VINE_MONITORING_USER_TABLE +" (EMAIL TEXT NOT NULL, USERID TEXT NOT NULL , USERNAME TEXT NO NULL, PRIMARY KEY(EMAIL,USERID))");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+NAME_TABLE_INSTAGRAM_MONITORING_USER_TABLE +" (EMAIL TEXT NOT NULL, USERID TEXT NOT NULL , USERNAME TEXT NOT NULL, PRIMARY KEY(EMAIL,USERID))");

        db.execSQL("CREATE TABLE IF NOT EXISTS "+NAME_TABLE_INSTAGRAM_MONITORING_POST_TABLE +" (EMAIL TEXT NOT NULL, POSTID TEXT NOT NULL , LINK TEXT NOT NULL, " +
                "CREATEDTIME TEXT NOT NULL, LASTTIMECHECKED TEXT NOT NULL, PRIMARY KEY(EMAIL,POSTID))");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+NAME_TABLE_VINE_MONITORING_POST_TABLE +" (EMAIL TEXT NOT NULL, POSTID TEXT NOT NULL , LINK TEXT NOT NULL, " +
                "CREATEDTIME TEXT NOT NULL, LASTTIMECHECKED TEXT NOT NULL, PRIMARY KEY(EMAIL,POSTID))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+NAME_TABLE_GUARDIAN_INFORMATION);
        db.execSQL("DROP TABLE IF EXISTS "+NAME_TABLE_VINE_MONITORING_USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+NAME_TABLE_INSTAGRAM_MONITORING_USER_TABLE);
        onCreate(db);

    }


    public void insertAccessTokenValue(String tableName,String email, String token, String columnName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE "+NAME_TABLE_GUARDIAN_INFORMATION+" SET "+ columnName+"='"+token+"' WHERE EMAIL='"+email+"'" ;
        db.execSQL(sql);
        db.close();
    }


    public void updateLastTimeCheckedForPost(String tableName,String postid, String lastTimeChecked)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //Log.i(UtilityVariables.tag,"inside updateLastTimeCheckedForPost function of database helper");
        String sql = "UPDATE "+tableName+" SET "+ NAME_COL_LASTTIMECHECKED+"='"+lastTimeChecked+"' WHERE POSTID ='"+postid+"'" ;
        db.execSQL(sql);
        db.close();
    }

    public static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    public Cursor getAllData(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+tableName,null);
    }

    public Cursor getMonitorInformationByGuardianEmail(String tablename, String email)
    {
        //Log.i(UtilityVariables.tag,"Inside databasehelper, getMonitorInformationByGuardianEmail function");
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM "+tablename+" WHERE EMAIL = ?", new String[] {email});
    }

    public String getAccessTokenForGuardian(String email,String columnName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+NAME_TABLE_GUARDIAN_INFORMATION+" WHERE EMAIL = ?", new String[] {email});
        if(res != null)
        {
            if(res.moveToFirst())
            {
                do {
                    return res.getString(res.getColumnIndex(columnName));

                }while(res.moveToNext());
            }
        }
        return "";

    }


    public ArrayList<String> getMonitoringPostIDsByEmail(String email,String tablename)
    {
       // Log.i(UtilityVariables.tag,"Inside databasehelper, getMonitoringPostIDsByEmail  function");
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+tablename+" WHERE EMAIL = ?", new String[] {email});
        ArrayList<String> postids= new ArrayList<>();
        if(res != null)
        {
            if(res.moveToFirst())
            {
                do {
                    postids.add(res.getString(res.getColumnIndex(DatabaseHelper.NAME_COL_POSTID)));

                }while(res.moveToNext());
            }
        }

        return postids;
    }

    public Hashtable<String,String> getMonitoringPostIDLastTimeCheckedByEmail(String email,String tablename)
    {
        //Log.i(UtilityVariables.tag,"Inside databasehelper, getMonitoringPostIDsByEmail  function");
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+tablename+" WHERE EMAIL = ?", new String[] {email});
        Hashtable<String,String> posts= new Hashtable<String,String>();
        if(res != null)
        {
            if(res.moveToFirst())
            {
                do {
                    //postids.add(res.getString(res.getColumnIndex(DatabaseHelper.NAME_COL_POSTID)));
                    posts.put(res.getString(res.getColumnIndex(DatabaseHelper.NAME_COL_POSTID)),
                            res.getString(res.getColumnIndex(DatabaseHelper.NAME_COL_LASTTIMECHECKED)));

                }while(res.moveToNext());
            }
        }

        return posts;
    }


    public Hashtable<String,String> getMonitoringInformationDetailByGuardianEmail(String tablename, String email)
    {
        //Log.i(UtilityVariables.tag,"Inside databasehelper, getMonitoringInformationDetailByGuardianEmail  function");
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+tablename+" WHERE EMAIL = ?", new String[] {email});
        Hashtable<String,String> monitorInformation= new Hashtable<String,String>();
        if(res != null)
        {
            if(res.moveToFirst())
            {
                do {
                    monitorInformation.put(res.getString(res.getColumnIndex(DatabaseHelper.NAME_COL_USERID)),res.getString(res.getColumnIndex(DatabaseHelper.NAME_COL_USERNAME)));

                }while(res.moveToNext());
            }
        }
        return monitorInformation;
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
            contentValues.put(NAME_COL_FACEBOOK_TOKEN, "");
            contentValues.put(NAME_COL_INSTAGRAM_TOKEN, "");
            contentValues.put(NAME_COL_TWITTER_TOKEN, "");
            contentValues.put(NAME_COL_VINE_TOKEN, "");
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

    public long deleteMonitoredUserFromTable(String tableName,String email, String userid)
    {
        try {
            //Log.i(UtilityVariables.tag," remove monitored user from table");
            //Log.i(UtilityVariables.tag,email+","+userid+","+tableName);
            String where = NAME_COL_EMAIL + " = ? and "+NAME_COL_USERID+" = ?";
            String[] whereArgs = { email,userid };

            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(tableName, where, whereArgs);
        }
        catch (Exception e)
        {
            return -1;

        }
    }

    public long insertMonitoringTable(String email,String userid,String username, String tableName)
    {
        try {
            //Log.i(UtilityVariables.tag," database helper insert monitoring table function");
            Log.i(UtilityVariables.tag,email+","+userid+","+tableName);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(NAME_COL_EMAIL, email);
            contentValues.put(NAME_COL_USERID, userid);
            contentValues.put(NAME_COL_USERNAME, username);
            return db.insert(tableName, null, contentValues);
        }
        catch (Exception e)
        {
            return -1;

        }
    }

    public long insertMonitoringPostTable(Post post, String tablename, String email)
    {
        try {
            //Log.i(UtilityVariables.tag," database helper insert monitoringpost table function");
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(NAME_COL_EMAIL, email);
            contentValues.put(NAME_COL_POSTID, post.postid);
            contentValues.put(NAME_COL_CREATEDTIME, post.createdtime);
            contentValues.put(NAME_COL_LASTTIMECHECKED, post.lastcheckedtime);
            contentValues.put(NAME_COL_LINK, post.link);
            return db.insert(tablename, null, contentValues);
        }
        catch (Exception e)
        {
            return -1;

        }
    }




}
