package com.afonseca.skrik;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Patterns;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** TODO:
 * Check the values of userid and regid and handle that offline AND online
 */
public class Funcs_UserCfg extends Activity {

    Ctrl_Backend backend = new Ctrl_Backend();

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";
    public static final String Email = "emailKey";
    public static final String Phone = "phoneKey";
    public static final String Uid = "uidKey";
    public static final String Regid = "regidKey";
    public static final String Status = "statusKey";
    public static final String Passwd = "PasswdKey";

    public String userOK_onSave (String username, String email, String phone, String uid, String regid, String passwd) {

        Boolean allOK = true;
        String result;
        ArrayList<String> errors = new ArrayList<>();

        // Checking there is a USERNAME
        if (username.matches("")){
            allOK = false;
            errors.add("username");
        }
        // Checking there is a valid e-Mail OR phone
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (email.matches("") || !matcher.matches()) {
            Pattern PhonePattern = Patterns.PHONE;
            if (!PhonePattern.matcher(phone).matches()) {
                allOK = false;
                errors.add("'VALID' Email or Phone");
            }
        }

        if (allOK) {
            result = "OK";
        } else {
            result = "You need to provide: ";
            for (String iterable_element : errors) {
                result = result + iterable_element + " ";
            }
        }

        return result;
    }

    public String userOK_SharedPrefs(Context mContext) {

        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        Boolean allOK = true;
        String result;
        ArrayList<String> errors = new ArrayList<>();

        if (!sharedpreferences.contains(Name)
                || sharedpreferences.getString(Name, "").equals(""))
        {
            allOK = false;
            errors.add("username");
        }
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sharedpreferences.getString(Email, ""));
        if (!sharedpreferences.contains(Email)
                || sharedpreferences.getString(Email, "").equals("")
                || !matcher.matches())
        {
            allOK = false;
            errors.add("'VALID' Email");
        }


        if (allOK){
            result = "OK";
        } else {
            result = "You need to provide: ";
            for (String iterable_element : errors) {
                result = result + iterable_element + " ";
            }
        }

        return result;
    }

    public String userHasLinkedAccount(Context mContext) {

        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String Result = "";

        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Pattern phonePattern = Patterns.PHONE;

        String phone_prev = sharedpreferences.getString(Phone, "");
        String email_prev = sharedpreferences.getString(Email, "");
        if (!phone_prev.matches("") && phonePattern.matcher(phone_prev).matches()) {
            Result = Result + "Phone" + phone_prev + " ";
        }
        if (!email_prev.matches("") && emailPattern.matcher(email_prev).matches()) {
            Result = Result + "Email" + email_prev;
        }
        if (Result.matches("")) {
            Result = "None";
        }
        return Result;
    }

    public String getUsername(Context mContext) {

        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        return sharedpreferences.getString(Name, "");
    }

    public String getEmail(Context mContext) {

        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        return sharedpreferences.getString(Email, "");
    }

    public void setEmail(Context mContext, String email) {

        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Email, email);
        //editor.commit();
        editor.apply();
    }

    public String getPhone(Context mContext) {

        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        return sharedpreferences.getString(Phone, "");
    }

    public void setPhone(Context mContext, String phone) {

        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Phone, phone);
        //editor.commit();
        editor.apply();
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
        //editor.commit();
        editor.apply();
    }

    public void clearUser(Context mContext) {

        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.remove(Name);
        editor.remove(Email);
        editor.remove(Uid);
        editor.remove(Regid);
        editor.remove(Passwd);
        //editor.commit();
        editor.apply();
    }

    public String saveUserConfig(Context mContext, String username, String email, String phone, String uid, String regid, String passwd) {

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

        if (serverSide.matches("OK")) {
            // TODO: uid.setText(current_uid);
            if (current_uid.matches("")) {
                current_uid = "99999999999999";
            }
            String saveResult = backend.saveUserToBackend(username, email, phone, current_uid, regid);
            if (saveResult.contains("Email found, NEW ID = ")) {
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
        editor.putString(Phone, phone);
        editor.putString(Uid, current_uid);
        editor.putString(Regid, regid);
        editor.putString(Status, data_status);
        editor.putString(Passwd, passwd);
        //editor.commit();
        editor.apply();

        if (result.contains("ERROR")) {
            return result;
        } else {
            result = result + "OK";
            return result;
        }
    }

    //JUST FOR TESTING WHAT IS ON THE SHAREDPREFS
    public void checkSavedData(Context mContext){
        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String phone = sharedpreferences.getString(Phone, "");
        String email = sharedpreferences.getString(Email, "");
        Log.i("TESTING, DATA-phone:",phone);
        Log.i("TESTING, DATA-email:",email);
    }
}