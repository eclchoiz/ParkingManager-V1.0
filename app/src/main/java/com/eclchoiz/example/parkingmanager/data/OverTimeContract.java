package com.eclchoiz.example.parkingmanager.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class OverTimeContract {

    public OverTimeContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.eclchoiz.example.parkingmanager";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MANAGER = "parkingmanager";

    public static final class OverTimeEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MANAGER);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MANAGER;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MANAGER;

        public static final String TABLE_NAME = "overtime";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME_PLATE = "plate";
        public static final String COLUMN_NAME_NUMBER = "number";
        public static final String COLUMN_NAME_INFO = "info";
        public static final String COLUMN_NAME_WARNING = "warning";
        public static final String COLUMN_NAME_PHONE_NUMBER = "phone_number";
    }
}
