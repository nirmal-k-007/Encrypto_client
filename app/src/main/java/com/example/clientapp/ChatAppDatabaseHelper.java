package com.example.clientapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;


public class ChatAppDatabaseHelper extends SQLiteOpenHelper {

    // Database information
    private static final String DATABASE_NAME = "ChatApp.db"; // Database name
    private static final int DATABASE_VERSION = 4;            // Incremented version to 4

    // Table and column information for Credentials
    public static final String TABLE_CREDENTIALS = "Credentials";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PRIVATE_KEY = "private_key";
    public static final String COLUMN_PUBLIC_KEY = "public_key";
    public static final String COLUMN_USER_NAME = "user_name";
    public static final String COLUMN_NAME = "name";

    // Table and column information for Chatlog
    public static final String TABLE_CHATLOG = "Chatlog";
    public static final String COLUMN_CHAT_USERNAME = "username";
    public static final String COLUMN_CHAT_FROM = "message_from";
    public static final String COLUMN_CHAT_CONTENT = "content";

    // SQL to create the Credentials table
    private static final String CREATE_TABLE_CREDENTIALS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_CREDENTIALS + " (" +
                    COLUMN_EMAIL + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL, " +
                    COLUMN_PRIVATE_KEY + " TEXT, " +
                    COLUMN_PUBLIC_KEY + " TEXT, " +
                    COLUMN_USER_NAME + " TEXT, " +
                    COLUMN_NAME + " TEXT);";

    // SQL to create the Chatlog table
    private static final String CREATE_TABLE_CHATLOG =
            "CREATE TABLE IF NOT EXISTS " + TABLE_CHATLOG + " (" +
                    COLUMN_CHAT_USERNAME + " TEXT NOT NULL, " +
                    COLUMN_CHAT_FROM + " TEXT NOT NULL, " +
                    COLUMN_CHAT_CONTENT + " TEXT NOT NULL);";

    public ChatAppDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create both tables
        db.execSQL(CREATE_TABLE_CREDENTIALS);
        db.execSQL(CREATE_TABLE_CHATLOG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add the public_key column if upgrading from version 1
            db.execSQL("ALTER TABLE " + TABLE_CREDENTIALS + " ADD COLUMN " + COLUMN_PUBLIC_KEY + " TEXT;");
        }
        if (oldVersion < 3) {
            // Add the user_name and name columns if upgrading from version 2
            db.execSQL("ALTER TABLE " + TABLE_CREDENTIALS + " ADD COLUMN " + COLUMN_USER_NAME + " TEXT;");
            db.execSQL("ALTER TABLE " + TABLE_CREDENTIALS + " ADD COLUMN " + COLUMN_NAME + " TEXT;");
        }
        if (oldVersion < 4) {
            // Create the Chatlog table if upgrading from version 3
            db.execSQL(CREATE_TABLE_CHATLOG);
        }
    }



    public void insertChatLog(String username, String messageFrom, String content) {
        // Get writable database
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a ContentValues object to store the values
        ContentValues values = new ContentValues();
        values.put(COLUMN_CHAT_USERNAME, username);
        values.put(COLUMN_CHAT_FROM, messageFrom);
        values.put(COLUMN_CHAT_CONTENT, content);

        // Insert the row into the Chatlog table
        db.insert(TABLE_CHATLOG, null, values);

        // Close the database
        db.close();
    }




    public List<MsgFormat> getChatLogsByUsername(String username) {
        // Get readable database
        SQLiteDatabase db = this.getReadableDatabase();

        // Define the query
        String query = "SELECT " + COLUMN_CHAT_FROM + ", " + COLUMN_CHAT_CONTENT +
                " FROM " + TABLE_CHATLOG +
                " WHERE " + COLUMN_CHAT_USERNAME + " = ?";

        // Execute the query
        Cursor cursor = db.rawQuery(query, new String[]{username});

        // Create a list to store ChatLog objects
        List<MsgFormat> chatLogs = new ArrayList<>();

        // Iterate through the results
        if (cursor.moveToFirst()) {
            do {
                String from = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHAT_FROM));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHAT_CONTENT));

                // Add each chat log to the list
                chatLogs.add(new MsgFormat(username, from, content));
            } while (cursor.moveToNext());
        }

        // Close the cursor and database
        cursor.close();
        db.close();

        return chatLogs;
    }


}
