package com.example.cybersafetyapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.Settings;

/**
 * Created by RahatIbnRafiq on 11/14/2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String NAME_DATABASE = "DATABASE_CYBERSAFETYAPP.db";
    public static final String NAME_TABLE_GUARDIAN_INFORMATION = "TABLE_GUARDIAN_INFORMATION";

    public static final String NAME_COL_EMAIL = "EMAIL";
    public static final String NAME_COL_PASSWORD = "PASSWORD";
    public static final String NAME_COL_PHONE = "PHONE";

    public DatabaseHelper(Context context)
    {
        super(context, NAME_DATABASE, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+NAME_TABLE_GUARDIAN_INFORMATION +" (EMAIL TEXT PRIMARY KEY NOT NULL, PASSWORD TEXT NOT NULL, PHONE TEXT NOT NULL )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+NAME_TABLE_GUARDIAN_INFORMATION);
        onCreate(db);

    }

    public Cursor showAllData()
    {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor res = db.rawQuery("select * from "+NAME_TABLE_GUARDIAN_INFORMATION,null);
            return res;
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            System.out.println("exception happened show all data");
            return null;
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

            System.out.println("trying to insert data");
            long result = db.insert(NAME_TABLE_GUARDIAN_INFORMATION, null, contentValues);
            System.out.println("insertion done");

            if (result == -1)
                return false;
            return true;
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            System.out.println("exception happened");

            return false;
        }
    }
}
