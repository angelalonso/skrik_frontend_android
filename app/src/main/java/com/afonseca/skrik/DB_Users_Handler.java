package com.afonseca.skrik;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by afonseca on 2/1/2015.
 */
public class DB_Users_Handler {

    SQLiteDatabase sqlDatabase;
    DB_Users_Helper dbHelper;

    public DB_Users_Handler(Context context) {

        dbHelper = DB_Users_Helper.getInstance(context);
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



