package com.afonseca.skrik;


import android.app.Activity;
import android.content.Context;
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
public class Funcs_Overview extends Activity {

    Ctrl_Backend backend = new Ctrl_Backend();

    public String getUsername(Context mContext,String newsUser_id) {
        Control_NewsUsersDbHandler newsUsersSQLHandler = new Control_NewsUsersDbHandler(mContext);
        String query = "SELECT username FROM NEWSUSERS WHERE id = '" + newsUser_id + "'";
        String username = "";
        Cursor c1 = newsUsersSQLHandler.selectQuery(query);
        if (c1 != null && c1.getCount() > 0) {
            if (c1.moveToFirst()) {
                do {
                    username = c1.getString(c1.getColumnIndex("username"));
                } while (c1.moveToNext());
            }
        }
        if (username.matches("")) {
            username = backend.getUsername(newsUser_id);
            addUser(mContext,newsUser_id,username);
        }
        return username;
    }

    public void addUser(Context mContext,String userid, String username){
        Control_NewsUsersDbHandler newsUsersSQLHandler = new Control_NewsUsersDbHandler(mContext);

        String query = "INSERT INTO NEWSUSERS (id, username, blacklisted) VALUES('" + userid + "','" + username + "',0)";
        newsUsersSQLHandler.executeQuery(query);
    }
}
