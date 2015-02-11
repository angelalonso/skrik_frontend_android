package com.afonseca.skrik;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by afonseca on 2/1/2015.
 */
public class Ctrl_UsersDbHandler {

    public static final String DATABASE_NAME = "USERS_DB";
    public static final int DATABASE_VERSION = 5;
    //Context context;
    SQLiteDatabase sqlDatabase;
    Ctrl_UsersDbHelper dbHelper;

    public Ctrl_UsersDbHandler(Context context) {

        dbHelper = new Ctrl_UsersDbHelper(context, DATABASE_NAME, null,
                DATABASE_VERSION);
        sqlDatabase = dbHelper.getWritableDatabase();
    }

    public void executeQuery(String query) {
        try {

            if (sqlDatabase.isOpen()) {
                sqlDatabase.close();
            }

            sqlDatabase = dbHelper.getWritableDatabase();
            sqlDatabase.execSQL(query);

        } catch (Exception e) {

            System.out.println("DATABASE ERROR " + e);
        }

    }

    public Cursor selectQuery(String query) {
        Cursor c1 = null;
        try {

            if (sqlDatabase.isOpen()) {
                sqlDatabase.close();

            }
            sqlDatabase = dbHelper.getWritableDatabase();
            c1 = sqlDatabase.rawQuery(query, null);

        } catch (Exception e) {

            System.out.println("DATABASE ERROR " + e);

        }
        return c1;

    }

}



