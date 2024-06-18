package com.dama.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseManager {
    private static DatabaseHelper databaseHelper;
    private static SQLiteDatabase database;

    public static void initializeDatabase(Context context) {
        if (databaseHelper == null) {
            Log.d("Database", "stworzono");
            databaseHelper = new DatabaseHelper(context);
        }
        else
            Log.d("Database", "istnieje");
        database = databaseHelper.getWritableDatabase();
    }

    public static SQLiteDatabase getDatabase() {
        return database;
    }
}
