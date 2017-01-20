package com.eclchoiz.example.parkingmanager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.eclchoiz.example.parkingmanager.data.ParkingMangerContract.ManagerEntry;

public class ParkingManagerDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "manager.db";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ManagerEntry.TABLE_NAME;


    public ParkingManagerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + ManagerEntry.TABLE_NAME + " (" +
                        ManagerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        ManagerEntry.COLUMN_NAME_PART + " TEXT NOT NULL, " +
                        ManagerEntry.COLUMN_NAME_NAME+ " TEXT, " +
                        ManagerEntry.COLUMN_NAME_PLATE + " TEXT, " +
                        ManagerEntry.COLUMN_NAME_NUMBER + " TEXT NOT NULL, " +
                        ManagerEntry.COLUMN_NAME_REG_NUMBER + " TEXT, " +
                        ManagerEntry.COLUMN_NAME_PHONE_NUMBER + " TEXT  " +
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