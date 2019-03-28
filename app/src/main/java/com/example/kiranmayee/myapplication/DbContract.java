package com.example.kiranmayee.myapplication;

import android.provider.BaseColumns;

public class DbContract {
    public static final class User implements BaseColumns {
        public static final String TABLE_NAME = "User";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_USER = "user";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
    }
}
