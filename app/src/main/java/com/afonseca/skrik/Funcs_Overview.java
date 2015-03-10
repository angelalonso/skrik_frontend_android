package com.afonseca.skrik;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;


public class Funcs_Overview extends Activity {

    Ctrl_Backend backend = new Ctrl_Backend();
    //TODO: Where is this called from? Act_overview>Showlist, Act_Channel>sendMessage

    //TODO: This should not go in functions overview
    public String getUsername(Context mContext,String newsUser_id) {
        DB_Users_Handler newsUsersSQLHandler = new DB_Users_Handler(mContext);
        String query = "SELECT username FROM USERS WHERE id = '" + newsUser_id + "'";
        String username = "";
        Cursor c1 = newsUsersSQLHandler.selectQuery(query);
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
            addNewUser(mContext,newsUser_id,username);
        }

        return username;
    }
    //TODO: This should not go in functions overview
    public String getUserid(Context mContext,String newsUser_name) {
        DB_Users_Handler newsUsersSQLHandler = new DB_Users_Handler(mContext);
        String query = "SELECT id FROM USERS WHERE username = '" + newsUser_name + "'";
        String userid = "";
        Cursor c1 = newsUsersSQLHandler.selectQuery(query);
        if (c1 != null && c1.getCount() > 0) {
            if (c1.moveToFirst()) {
                do {
                    userid = c1.getString(c1.getColumnIndex("id"));
                } while (c1.moveToNext());
            }
        }
        c1.close();
        if (userid.matches("")) {
            userid = "";
        }

        return userid;
    }

    // TODO: It is only used from here (up there), should be called when a new message arrives/is sent
    public void addNewUser(Context mContext,String userid, String username){
        DB_Users_Handler newsUsersSQLHandler = new DB_Users_Handler(mContext);

        String query = "INSERT INTO USERS (id, username, blacklisted) VALUES('" + userid + "','" + username + "',0)";
        newsUsersSQLHandler.executeQuery(query);
    }
}
