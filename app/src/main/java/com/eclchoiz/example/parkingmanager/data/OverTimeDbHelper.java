package com.eclchoiz.example.parkingmanager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.eclchoiz.example.parkingmanager.data.OverTimeContract.OverTimeEntry;

public class OverTimeDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "manager.db";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + OverTimeEntry.TABLE_NAME;


    public OverTimeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + OverTimeEntry.TABLE_NAME + " (" +
                        OverTimeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        OverTimeEntry.COLUMN_NAME_PLATE + " TEXT, " +
                        OverTimeEntry.COLUMN_NAME_NUMBER + " TEXT NOT NULL, " +
                        OverTimeEntry.COLUMN_NAME_INFO + " NUMBER NOT NULL, " +
                        OverTimeEntry.COLUMN_NAME_WARNING+ " NUMBER, " +
                        OverTimeEntry.COLUMN_NAME_PHONE_NUMBER + " TEXT  " +
                        " )";
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}