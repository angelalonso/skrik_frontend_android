package com.afonseca.skrik;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DB_Msgs_Handler {

    public static final String DATABASE_NAME = "MSGS_DB";
    public static final int DATABASE_VERSION = 3;
    SQLiteDatabase sqlDatabase;
    DB_Msgs_Helper dbHelper;

    public DB_Msgs_Handler(Context context) {

        dbHelper = new DB_Msgs_Helper(context, DATABASE_NAME, null,
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

    //MAYBE UNNEEDED?
    public long executeInsertgetID(String me_userid, String other_userid, String message, String status, String timestamp, String backend_id) {
        ContentValues newValues = new ContentValues();
        newValues.put("userid_from", me_userid);
        newValues.put("userid_to", other_userid);
        newValues.put("message", message);
        newValues.put("status", status);
        newValues.put("timestamp", timestamp);
        newValues.put("backend_id", backend_id);
        long newID = sqlDatabase.insert("MSGS",null,newValues);
        return newID;
    }

    public void addNewMessage(String me_userid, String other_userid, String message,String timestamp) {
        String Insertquery = "INSERT INTO MSGS (userid_from, userid_to, message, status, timestamp, backend_id) VALUES('" + me_userid + "','" + other_userid + "','" + message + "','created','" + timestamp + "','none')";
        executeQuery(Insertquery);
    }

}


