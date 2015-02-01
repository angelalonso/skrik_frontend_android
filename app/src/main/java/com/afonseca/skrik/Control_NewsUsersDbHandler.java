package com.afonseca.skrik;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by afonseca on 2/1/2015.
 */
public class Control_NewsUsersDbHandler {

    public static final String DATABASE_NAME = "NEWSUSERS_DB";
    public static final int DATABASE_VERSION = 4;
    //Context context;
    SQLiteDatabase sqlDatabase;
    Control_NewsUsersDbHelper dbHelper;

    public Control_NewsUsersDbHandler(Context context) {

        dbHelper = new Control_NewsUsersDbHelper(context, DATABASE_NAME, null,
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



