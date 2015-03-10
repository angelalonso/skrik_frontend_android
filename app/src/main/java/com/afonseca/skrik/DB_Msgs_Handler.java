package com.afonseca.skrik;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DB_Msgs_Handler {

    SQLiteDatabase sqlDatabase;
    DB_Msgs_Helper dbHelper;

    public DB_Msgs_Handler(Context context) {

        dbHelper = DB_Msgs_Helper.getInstance(context);
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
        String Insertquery = "INSERT INTO MSGS (userid_from, userid_to, message, status, timestamp, backend_id) VALUES('" + me_userid + "','" + other_userid + "','" + message + "','created','" + timestamp + "','none');";
        executeQuery(Insertquery);
    }

    public Cursor getMsgsWStatus(String status) {
        Cursor c1 = null;
        String query;
        if (status.contains(" OR ") || status.contains(" or ")){
            String status_1 = status.replace(" OR ","' OR status='");
            Log.i("TESTING QUERY   ", status_1);
            query = "SELECT id,message,userid_from, userid_to,timestamp FROM MSGS WHERE status ='" + status_1 + "';";

        } else {
            query = "SELECT id,message,userid_from, userid_to,timestamp FROM MSGS WHERE status ='" + status + "';";
        }
        c1 = selectQuery(query);

        return c1;
    }



}


