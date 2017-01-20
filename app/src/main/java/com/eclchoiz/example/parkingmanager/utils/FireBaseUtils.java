package com.eclchoiz.example.parkingmanager.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.eclchoiz.example.parkingmanager.data.ParkingDataObject;
import com.eclchoiz.example.parkingmanager.data.ParkingMangerContract.ManagerEntry;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by choiz on 2017-01-17.
 */

public class FireBaseUtils {

    public static boolean isNew(String key, Context context) {
        String selectionClause = ManagerEntry.COLUMN_NAME_KEY + " = ?";
        String[] selectionArgs = new String[]{key};
        Uri baseUri = ManagerEntry.CONTENT_URI;
        String queryOrder = null;

        String[] projection = {
                ManagerEntry._ID,
                ManagerEntry.COLUMN_NAME_KEY,
                ManagerEntry.COLUMN_NAME_PART,
                ManagerEntry.COLUMN_NAME_NAME,
                ManagerEntry.COLUMN_NAME_PLATE,
                ManagerEntry.COLUMN_NAME_NUMBER,
                ManagerEntry.COLUMN_NAME_REG_NUMBER,
                ManagerEntry.COLUMN_NAME_PHONE_NUMBER};

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(
                baseUri,
                projection,
                selectionClause,
                selectionArgs,
                queryOrder);

        if (cursor.getCount() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static Uri insertSqlLiteWithObject(ParkingDataObject dataObject, Context context) {
        ContentValues values = objectToValues(dataObject);

        Uri newUri = context.getContentResolver().insert(ManagerEntry.CONTENT_URI, values);

        return newUri;
    }

    public static String insertFireBaseWithValues(ContentValues values, DatabaseReference databaseReference) {
        String key = databaseReference.push().getKey();
        ParkingDataObject dataObject = valueToObject(values);

        Map<String, Object> updateValue = dataObject.toMap();
        Map<String, Object> childUpdate = new HashMap<>();
        childUpdate.put(key, updateValue);
        databaseReference.updateChildren(childUpdate);

        return key;
    }

    private static ContentValues objectToValues(ParkingDataObject dataObject) {
        ContentValues values = new ContentValues();

        values.put(ManagerEntry.COLUMN_NAME_PART, dataObject.getPart());
        values.put(ManagerEntry.COLUMN_NAME_NAME, dataObject.getName());
        values.put(ManagerEntry.COLUMN_NAME_PLATE, dataObject.getPlate());
        values.put(ManagerEntry.COLUMN_NAME_NUMBER, dataObject.getNumber());
        values.put(ManagerEntry.COLUMN_NAME_REG_NUMBER, dataObject.getRegNumber());
        values.put(ManagerEntry.COLUMN_NAME_PHONE_NUMBER, dataObject.getPhoneNumber());
        values.put(ManagerEntry.COLUMN_NAME_KEY, dataObject.getKey());

        return values;
    }

    public static ParkingDataObject valueToObject(ContentValues values) {
        String part = (String) values.get(ManagerEntry.COLUMN_NAME_PART);
        String name = (String) values.get(ManagerEntry.COLUMN_NAME_NAME);
        String plate = (String) values.get(ManagerEntry.COLUMN_NAME_PLATE);
        String number = (String) values.get(ManagerEntry.COLUMN_NAME_NUMBER);
        String regNumber = (String) values.get(ManagerEntry.COLUMN_NAME_REG_NUMBER);
        String phoneNumber = (String) values.get(ManagerEntry.COLUMN_NAME_PHONE_NUMBER);

        return new ParkingDataObject(part, name, plate, number, regNumber, phoneNumber);
    }
}
