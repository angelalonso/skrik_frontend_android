package com.afonseca.skrik;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;


public class Toolbox_LocalSQLite {

    DB_Users_Handler usersSQLHandler;
    Toolbox_Backend backend;


    public String getUserid(Context inContext,String newsUser_name) {
        usersSQLHandler = new DB_Users_Handler(inContext);
        String getidforname_query = "SELECT id FROM USERS WHERE username = '" + newsUser_name + "'";
        String userid = "";
        Cursor c1 = usersSQLHandler.selectQuery(getidforname_query);
        if (c1 != null && c1.getCount() > 0) {
            if (c1.moveToFirst()) {
                do {
                    userid = c1.getString(c1.getColumnIndex("id"));
                } while (c1.moveToNext());
            }
        }
        try {
            c1.close();
        } catch(NullPointerException e) {
                Log.i("ERROR HANDLING : ", "Catched NullPointerException at closing" + getidforname_query);
                e.printStackTrace();
        }
        if (userid.matches("")) {
            userid = "";
        }

        return userid;
    }


    public String getUsername(Context inContext,String newsUser_id) {
        backend = new Toolbox_Backend();
        usersSQLHandler = new DB_Users_Handler(inContext);
        String query = "SELECT username FROM USERS WHERE id = '" + newsUser_id + "'";
        String username = "";
        Cursor c1 = usersSQLHandler.selectQuery(query);
        if (c1 != null && c1.getCount() > 0) {
            if (c1.moveToFirst()) {
                do {
                    username = c1.getString(c1.getColumnIndex("username"));
                } while (c1.moveToNext());
            }
        }
        c1.close();
        if (username.matches("")) {
            username = backend.getUsername(newsUser_id);
            addNewUser(inContext,newsUser_id,username);
        }

        return username;
    }



    // TODO: It is only used from here (up there), should be called when a new message arrives/is sent
    public void addNewUser(Context inContext,String userid, String username){
        usersSQLHandler = new DB_Users_Handler(inContext);

        String query = "INSERT INTO USERS (id, username, blacklisted) VALUES('" + userid + "','" + username + "',0)";
        usersSQLHandler.executeQuery(query);
    }
}
