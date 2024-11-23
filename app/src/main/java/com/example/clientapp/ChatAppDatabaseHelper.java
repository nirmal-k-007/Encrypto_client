package com.example.clientapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ChatAppDatabaseHelper extends SQLiteOpenHelper {

    // Database information
    private static final String DATABASE_NAME = "ChatApp.db"; // Database name
    private static final int DATABASE_VERSION = 2;            // Incremented version

    // Table and column information
    public static final String TABLE_CREDENTIALS = "Credentials";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PRIVATE_KEY = "private_key";
    public static final String COLUMN_PUBLIC_KEY = "public_key";  // New column

    // SQL to create the Credentials table
    private static final String CREATE_TABLE_CREDENTIALS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_CREDENTIALS + " (" +
                    COLUMN_EMAIL + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL, " +
                    COLUMN_PRIVATE_KEY + " TEXT, " +
                    COLUMN_PUBLIC_KEY + " TEXT);";  // Added new column here

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
        if (oldVersion < 2) {
            // Add the new public_key column if upgrading from version 1
            db.execSQL("ALTER TABLE " + TABLE_CREDENTIALS + " ADD COLUMN " + COLUMN_PUBLIC_KEY + " TEXT;");
        }
    }
}
