package com.example.clientapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

public class ChatAppDatabaseHelper extends SQLiteOpenHelper {

    // Database information
    private static final String DATABASE_NAME = "ChatApp.db"; // Database name
    private static final int DATABASE_VERSION = 1;           // Database version

    // Table and column information
    public static final String TABLE_CREDENTIALS = "Credentials";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PRIVATE_KEY = "private_key";

    // SQL to create the Credentials table
    private static final String CREATE_TABLE_CREDENTIALS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_CREDENTIALS + " (" +
                    COLUMN_EMAIL + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL, " +
                    COLUMN_PRIVATE_KEY + " TEXT);";

    public ChatAppDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the Credentials table
        db.execSQL(CREATE_TABLE_CREDENTIALS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the old table and recreate it if the schema changes
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CREDENTIALS);
        onCreate(db);
    }

}
