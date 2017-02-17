package com.example.cybersafetyapp.HelperClassesPackage;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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

    public static final String NAME_COL_INSTAGRAM_TOKEN = "INSTAGRAM_TOKEN";
    public static final String NAME_COL_TWITTER_TOKEN = "TWITTER_TOKEN";
    public static final String NAME_COL_FACEBOOK_TOKEN = "FACEBOOK_TOKEN";
    public static final String NAME_COL_EMAIL = "EMAIL";

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

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+NAME_TABLE_GUARDIAN_INFORMATION +
                " (EMAIL TEXT PRIMARY KEY NOT NULL,INSTAGRAM_TOKEN TEXT, TWITTER_TOKEN TEXT, FACEBOOK_TOKEN TEXT)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+NAME_TABLE_GUARDIAN_INFORMATION);
        onCreate(db);


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

    public boolean insertGuardianData(String email)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(NAME_COL_EMAIL, email);
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
}
