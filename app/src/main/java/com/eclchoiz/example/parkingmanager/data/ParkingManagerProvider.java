package com.eclchoiz.example.parkingmanager.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.eclchoiz.example.parkingmanager.data.ParkingMangerContract.ManagerEntry;

public class ParkingManagerProvider extends ContentProvider {

    public static final String LOG_TAG = ParkingManagerProvider.class.getSimpleName();

    private static final int MANAGER = 100;

    private static final int MANAGER_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ParkingMangerContract.CONTENT_AUTHORITY, ParkingMangerContract.PATH_MANAGER, MANAGER);
        sUriMatcher.addURI(ParkingMangerContract.CONTENT_AUTHORITY, ParkingMangerContract.PATH_MANAGER + "/#", MANAGER_ID);
    }

    private ParkingManagerDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ParkingManagerDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
//        Log.e("uriMatcher", "sUriMatcher : " + match);
        switch (match) {
            case MANAGER:
                cursor = database.query(ManagerEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case MANAGER_ID:
                selection = ManagerEntry._ID + " = ?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(ManagerEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MANAGER:
                return insertCar(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertCar(Uri uri, ContentValues values) {
        String date = values.getAsString(ManagerEntry.COLUMN_NAME_PART);
        if (date == null) {
            throw new IllegalArgumentException("Requires a Part");
        }

        String currentRate = values.getAsString(ManagerEntry.COLUMN_NAME_NUMBER);
        if (currentRate == null) {
            throw new IllegalArgumentException("Requires a Car Number");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(ManagerEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MANAGER:
                return updateManager(uri, contentValues, selection, selectionArgs);
            case MANAGER_ID:
                selection = ManagerEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateManager(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateManager(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(ManagerEntry.COLUMN_NAME_PART)) {
            String part = values.getAsString(ManagerEntry.COLUMN_NAME_PART);
            if (part == null) {
                throw new IllegalArgumentException("Requires a data");
            }
        }

        if (values.containsKey(ManagerEntry.COLUMN_NAME_NUMBER)) {
            String number = values.getAsString(ManagerEntry.COLUMN_NAME_NUMBER);
            if (number == null) {
                throw new IllegalArgumentException("Requires a data");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(ManagerEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MANAGER:
                rowsDeleted = database.delete(ManagerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MANAGER_ID:
                selection = ManagerEntry._ID + " = ?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ManagerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MANAGER:
                return ManagerEntry.CONTENT_LIST_TYPE;
            case MANAGER_ID:
                return ManagerEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }
}