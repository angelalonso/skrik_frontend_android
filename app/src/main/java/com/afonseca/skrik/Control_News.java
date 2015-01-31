package com.afonseca.skrik;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/*
This one is a more "personalized" controller of what's shown in the News list
  It is useful to control a Whitelist (to join id with username)
    and a blacklist (to avoid seeing messages from someone you don't want)
*/
public class Control_News extends Activity {

    public static final String MyPREFERENCES = "MyPrefs" ;

    public void whitelist_AddUser(Context mContext) {
        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

    }
}
