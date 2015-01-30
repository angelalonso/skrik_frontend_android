package com.afonseca.skrik;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by afonseca on 1/24/2015.
 *
 * TODO:
 * REALLY, REALLY Check the values of username and email before saving
 * REALLY, REALLY Check the values of userid and regid and handle that offline AND online
 *
 */
public class Control_Userconfig extends Activity {

    Control_BackendHandler backend = new Control_BackendHandler();

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";
    public static final String Email = "emailKey";
    public static final String Uid = "uidKey";
    public static final String Regid = "regidKey";
    public static final String Status = "statusKey";
    public static final String Passwd = "PasswdKey";

    public String userOK_newtext(Context mContext, String username, String email, String uid, String regid, String passwd) {

        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        Boolean allOK = true;
        String result = "";
        ArrayList<String> errors = new ArrayList<>();

        if (username.matches(""))
        {
            allOK = false;
            errors.add("username");
        }
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (email.matches("")
                || !matcher.matches())
        {
            allOK = false;
            errors.add("'VALID' Email");
        }


        if (allOK == true){
            result = "OK";
        } else {
            result = "You need to provide: ";
            for (String iterable_element : errors) {
                result = result + iterable_element + " ";
            }
        }

        return result;
    }


    public String userOK_shared_prefs(Context mContext) {

        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        Log.i("TESTING EMAIL second",sharedpreferences.getString(Email, ""));

        Boolean allOK = true;
        String result = "";
        ArrayList<String> errors = new ArrayList<>();

        if (!sharedpreferences.contains(Name)
                || sharedpreferences.getString(Name, "") == "")
        {
            allOK = false;
            errors.add("username");
        }
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sharedpreferences.getString(Email, ""));
        if (!sharedpreferences.contains(Email)
                || sharedpreferences.getString(Email, "") == ""
                || !matcher.matches())
        {
            allOK = false;
            errors.add("'VALID' Email");
            Log.i("TESTING EMAIL third",sharedpreferences.getString(Email, ""));
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

    public String getStatus(Context mContext) {

        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        return sharedpreferences.getString(Status, "");
    }

    public void setStatus(Context mContext, String data_status) {

        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Status, data_status);

        editor.commit();
    }

    public void setUserConfig(Context mContext, String username, String email, String uid, String regid, String passwd) {
    /* This one is used to update sharedpreferences temporarily */
        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(Name, username);
        editor.putString(Email, email);
        editor.putString(Uid, uid);
        editor.putString(Regid, regid);
        editor.putString(Status, "unsynced");
        editor.putString(Passwd, passwd);

        editor.commit();
    }

        public String saveUserConfig(Context mContext, String username, String email, String uid, String regid, String passwd) {
    /* Logic:
    *   - If Network is OK
    *     - If UID or REGID is empty TODO: REGID?? Also, maybe this control should go into the backend controller
    *       - Give Data to Server, ask for UID and REGID
    *         - (serverside) if the email exists, existing UID is given back. TODO: use a password/email auth/whatever here. *
    *     - If data is OK
    *       - Send
    *       - Save into sharedpreferences
    *       - Go back to main
    *   - If Network is NOT OK
    *     - If UID or REGID is empty TODO: Same as above with REGID
    *       - Build temporary values (leave it empty, maybe?) TODO: Same as above, maybe done in the backend controller
    *     - If data is OK
    *       - Mark data as NOT synchronized
    *       - Save into sharedpreferences
    *       - Go back to main
    *     - Save into sharedpreferences
    *     - Return result
    */
        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        String result = "";
        String serverSide = backend.testNetwork(mContext);
        String current_uid = uid;
        String data_status;

        String sanitized_regid = regid.replaceAll("[^\\d.]", "");
        if (sanitized_regid.matches("")) {
            regid = "4444";
            result = result + "UPDATE regid 4444||";
        }

        if (serverSide.matches("OK")){
            /* I BELIEVE I DONT NEED THIS
            if (current_uid.matches("")) {
                current_uid = backend.getnewID();
                result = result + "UPDATE uid " + current_uid +"||";
                // TODO: uid.setText(current_uid);
            }
            */
            if (current_uid.matches("")) {
                current_uid = "99999999999999";
            }
            String saveResult = backend.saveUserToBackend(username, email, current_uid, regid);
            if (saveResult.contains("Email found, NEW ID = ")){
                current_uid = saveResult.replace("Email found, NEW ID = ","");
            } else if (saveResult.contains("NEW ID = ")){
                current_uid = saveResult.replace("NEW ID = ","");
            }
            data_status = "synced";
        } else {
            if (current_uid.matches("")) {
                current_uid = "99999999999999";
                result = result + "UPDATE uid " + current_uid +"||";
                // TODO: uid.setText(current_uid);
            }
            data_status = "unsynced";
        }

        editor.putString(Name, username);
        editor.putString(Email, email);
        editor.putString(Uid, current_uid);
        editor.putString(Regid, regid);
        editor.putString(Status, data_status);
        editor.putString(Passwd, passwd);

        editor.commit();

        if (result.contains("ERROR")) {
            return result;
        } else {
            result = result + "OK";
            return result;
        }
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

    }
}
