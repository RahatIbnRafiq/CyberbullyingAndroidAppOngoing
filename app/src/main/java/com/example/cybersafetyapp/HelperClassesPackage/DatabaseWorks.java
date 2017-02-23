package com.example.cybersafetyapp.HelperClassesPackage;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.cybersafetyapp.UtilityPackage.UtilityVariables;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;


public class DatabaseWorks extends SQLiteOpenHelper{
    private static DatabaseWorks mInstance = null;
    public static final String NAME_DATABASE = "DATABASE_CYBERSAFETYAPP.db";
    public static final String NAME_TABLE_GUARDIAN_INFORMATION = "TABLE_GUARDIAN_INFORMATION";
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

    private Context mCxt;

    private Hashtable<String, ArrayList<String>> hasTable;


    public static DatabaseWorks getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DatabaseWorks(ctx.getApplicationContext());
        }
        return mInstance;
    }

    DatabaseWorks(Context ctx) {
        super(ctx, NAME_DATABASE, null, 1);
        this.hasTable  = new Hashtable<>();
        ArrayList<String> colList = new ArrayList<String>();
        colList.add(NAME_COL_EMAIL);
        colList.add(NAME_COL_TWITTER_TOKEN);
        colList.add(NAME_COL_INSTAGRAM_TOKEN);
        colList.add(NAME_COL_FACEBOOK_TOKEN);
        this.hasTable.put(NAME_TABLE_GUARDIAN_INFORMATION,colList);

        colList = new ArrayList<>();
        colList.add(NAME_COL_EMAIL);
        colList.add(NAME_COL_POSTID);
        colList.add(NAME_COL_LINK);
        colList.add(NAME_COL_CREATEDTIME);
        colList.add(NAME_COL_LASTTIMECHECKED);
        this.hasTable.put(NAME_TABLE_INSTAGRAM_MONITORING_POST_TABLE,colList);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+NAME_TABLE_GUARDIAN_INFORMATION +
                " (EMAIL TEXT PRIMARY KEY NOT NULL,INSTAGRAM_TOKEN TEXT, TWITTER_TOKEN TEXT, FACEBOOK_TOKEN TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS "+NAME_TABLE_INSTAGRAM_MONITORING_POST_TABLE +" (EMAIL TEXT NOT NULL, POSTID TEXT NOT NULL , LINK TEXT NOT NULL, " +
                "CREATEDTIME TEXT NOT NULL, LASTTIMECHECKED TEXT NOT NULL, PRIMARY KEY(EMAIL,POSTID))");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+NAME_TABLE_GUARDIAN_INFORMATION);
        onCreate(db);


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

    public void updateLastTimeCheckedForPost(String tableName,String postid, String lastTimeChecked)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //Log.i(UtilityVariables.tag,"inside updateLastTimeCheckedForPost function of database helper");
        String sql = "UPDATE "+tableName+" SET "+ NAME_COL_LASTTIMECHECKED+"='"+lastTimeChecked+"' WHERE POSTID ='"+postid+"'" ;
        db.execSQL(sql);
        db.close();
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

    public void insertAccessTokenValue(String email, String token, String columnName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE "+NAME_TABLE_GUARDIAN_INFORMATION+" SET "+ columnName+"='"+token+"' WHERE EMAIL='"+email+"'" ;
        db.execSQL(sql);
        db.close();
    }

    public void printAllDataFromTable(String tableName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+tableName,null);
        ArrayList<String> cols = this.hasTable.get(tableName);
        if(res != null)
        {
            if(res.moveToFirst())
            {
                do {
                    StringBuilder sb = new StringBuilder();
                    for(String key: cols) {
                        sb.append(res.getString(res.getColumnIndex(key))+",") ;
                    }
                    Log.i(UtilityVariables.tag,"DatabaseWorks: printing table row: "+sb.toString());

                }while(res.moveToNext());
            }
        }
    }

    public ArrayList<String> getMonitoringPostIDsByEmail(String email,String tablename)
    {
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

    public boolean insertGuardianData(String email)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(NAME_COL_EMAIL, email);
            try{
                db.insert(NAME_TABLE_GUARDIAN_INFORMATION, null, contentValues);
                return true;
            }catch (SQLiteConstraintException ex)
            {
                return false;
            }
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
