package com.afonseca.skrik;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by afonseca on 1/24/2015.
 */
public class Control_Userconfig extends Activity {

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";
    public static final String Email = "emailKey";
    public static final String Uid = "uidKey";
    public static final String Regid = "regidKey";
    public static final String Passwd = "PasswdKey";

    public String userOK(Context mContext) {

        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        Boolean allOK = true;
        String result = "";
        ArrayList<String> errors = new ArrayList<>();

        if (!sharedpreferences.contains(Name)
                || sharedpreferences.getString(Name, "") == ""
                || !sharedpreferences.getString(Name, "").matches("[a-zA-Z]+"))
        {
            allOK = false;
            errors.add("username");
        }
        if (!sharedpreferences.contains(Email)
                || sharedpreferences.getString(Email, "") == ""
                || !sharedpreferences.getString(Email, "").matches("[a-zA-Z]+"))
        {
            allOK = false;
            errors.add("Email");
        }


        if (allOK == true){
            result = "OK";
            Log.i(" ---  OK", sharedpreferences.getString(Name, "") + "//" + sharedpreferences.getString(Email, ""));
        } else {
            result = "You need to provide: ";
            for (String iterable_element : errors) {
                result = result + iterable_element + " ";
            }
            Log.i(" ---  ERROR", result + "//" +sharedpreferences.getString(Name, "") + "//" + sharedpreferences.getString(Email, ""));
        }

        Log.i(" ---  ", sharedpreferences.getString(Name, "") + "//" + sharedpreferences.getString(Email, ""));

        return result;
    }

    public String getUsername(Context mContext) {

        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        return sharedpreferences.getString(Name, "");
    }

    public String getEmail(Context mContext) {

        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        return sharedpreferences.getString(Email, "");
    }

    public String getUid(Context mContext) {

        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        return sharedpreferences.getString(Uid, "");
    }

    public String getRegid(Context mContext) {

        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        return sharedpreferences.getString(Regid, "");
    }

    public void saveUser(Context mContext, String username, String email, String uid, String regid, String passwd) {

        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(Name, username);
        editor.putString(Email, email);
        editor.putString(Uid, uid);
        editor.putString(Regid, regid);
        editor.putString(Passwd, passwd);

        editor.commit();

    }


    public void clearUser(Context mContext) {

        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.remove(Name);
        editor.remove(Email);
        editor.remove(Uid);
        editor.remove(Regid);
        editor.remove(Passwd);
        editor.commit();

        editor.commit();
    }
}
