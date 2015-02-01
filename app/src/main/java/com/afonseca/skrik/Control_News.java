package com.afonseca.skrik;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

/*
This one is a more "personalized" controller of what's shown in the News list
  It is useful to control a Whitelist (to join id with username)
    and a blacklist (to avoid seeing messages from someone you don't want)
*/

/*
* TODO: function to get username
*   TODO: If the username is not there, add it, reading from backend
*   TODO: If the username is there, send it back
*
* */
public class Control_News extends Activity {

    public static final String MyPREFERENCES = "MyPrefs" ;
    Control_Userconfig controlUserconfig = new Control_Userconfig();
    Control_NewsDbHandler newsSQLHandler;
    Control_NewsUsersDbHandler newsUsersSQLHandler;

    public String getUsername(String newsUser_id) {
        String query = "SELECT username FROM NEWSUSERS WHERE id = '" + newsUser_id + "'";
        String username = "";
        Cursor c1 = newsSQLHandler.selectQuery(query);
        if (c1 != null && c1.getCount() > 0) {
            if (c1.moveToFirst()) {
                do {
                    username = c1.getString(c1.getColumnIndex("username"));
                } while (c1.moveToNext());
            }
        }
        return username;
    }

    public void addUser(String userid, String username){

        String query = "INSERT INTO NEWSUSER (id, username, blacklisted) VALUES('" + userid + "','" + username + "',False)";
        newsUsersSQLHandler.executeQuery(query);
    }
}
