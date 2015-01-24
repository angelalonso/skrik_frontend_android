package com.afonseca.skrik;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by afonseca on 1/24/2015.
 */
public class Control_Userconfig extends Activity {

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    public static final String Name = "nameKey";
    public static final String Email = "emailKey";

    /**  */
    public boolean userOK() {

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Boolean result = false;
        if (sharedpreferences.contains(Name) && sharedpreferences.contains(Email))
        {
            result = true;
        }

        return result;
    }

}
