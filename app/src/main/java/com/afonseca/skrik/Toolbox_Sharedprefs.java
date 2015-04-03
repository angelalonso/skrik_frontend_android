package com.afonseca.skrik;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Patterns;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

   // TODO: Check the values of userid and regid and handle that offline AND online

  /* Used by:
  Act_UserCfg
  */

public class Toolbox_Sharedprefs extends Activity {

    Toolbox_Backend backend = new Toolbox_Backend();

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";
    public static final String Email = "emailKey";
    public static final String Phone = "phoneKey";
    public static final String Uid = "uidKey";
    public static final String Regid = "regidKey";
    public static final String Status = "statusKey";
    public static final String Passwd = "PasswdKey";

    public String getUsername(Context inContext) {

        SharedPreferences sharedpreferences = inContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        return sharedpreferences.getString(Name, "");
    }

    
    public String getEmail(Context inContext) {

        SharedPreferences sharedpreferences = inContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        return sharedpreferences.getString(Email, "");
    }

    
    public void setEmail(Context inContext, String email) {

        SharedPreferences sharedpreferences = inContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Email, email);
        editor.apply();
    }

    
    public String getPhone(Context inContext) {

        SharedPreferences sharedpreferences = inContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        return sharedpreferences.getString(Phone, "");
    }

    
    public void setPhone(Context inContext, String phone) {

        SharedPreferences sharedpreferences = inContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Phone, phone);
        editor.apply();
    }

    public String getUid(Context inContext) {

        SharedPreferences sharedpreferences = inContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        return sharedpreferences.getString(Uid, "");
    }

    public String getRegid(Context inContext) {

        SharedPreferences sharedpreferences = inContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        return sharedpreferences.getString(Regid, "");
    }

    public void setRegid(Context inContext, String regid) {

        SharedPreferences sharedpreferences = inContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Regid, regid);
        editor.apply();
    }

    public String getPasswd(Context inContext) {

        SharedPreferences sharedpreferences = inContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        return sharedpreferences.getString(Passwd, "");
    }

    // THIS IS NOT YET USED - is OK
    public String getStatus(Context inContext) {

        SharedPreferences sharedpreferences = inContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        return sharedpreferences.getString(Status, "");
    }

    // THIS IS NOT YET USED - is OK
    public void setStatus(Context inContext, String data_status) {

        SharedPreferences sharedpreferences = inContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Status, data_status);
        editor.apply();
    }

    public boolean hasPasswd(Context inContext) {

        SharedPreferences sharedpreferences = inContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        boolean result = false;
        if (!sharedpreferences.getString(Passwd, "").matches("")){
            result = true;
        }
        return result;
    }

    public boolean phoneHasRegid(Context inContext) {
        SharedPreferences sharedpreferences = inContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        boolean result = false;

        String regid = sharedpreferences.getString(Regid, "");
        String sanitized_regid = regid.replaceAll("[^\\d.]", "");
        if (!sanitized_regid.matches("") && !sanitized_regid.matches("4444")) {
            result = true;
        }

        return result;
    }
    
    public void clearUser(Context inContext) {

        SharedPreferences sharedpreferences = inContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.remove(Name);
        editor.remove(Email);
        editor.remove(Uid);
        editor.remove(Regid);
        editor.remove(Passwd);
        editor.apply();
    }

    public String userOK_beforeSave (Context inContext, String username, String email, String phone, String passwd) {

        Boolean allOK = true;
        String result;
        ArrayList<String> errors = new ArrayList<>();

        // Checking there is a USERNAME
        if (username.matches("")){
            allOK = false;
            errors.add(inContext.getResources().getString(R.string.aux_result_username));
        }
        // Checking there is a valid e-Mail OR phone
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (email.matches("") || !matcher.matches()) {
            Pattern PhonePattern = Patterns.PHONE;
            if (!PhonePattern.matcher(phone).matches()) {
                allOK = false;
                errors.add(inContext.getResources().getString(R.string.aux_result_valid_email_phone));
            }
        }
        Log.i("TESTING PASSWORD ", getPasswd(inContext));
        // Checking there is a PASSWORD
        if (passwd.matches("") && !hasPasswd(inContext)){
            allOK = false;
            errors.add(inContext.getResources().getString(R.string.aux_result_passwd));
        }
        if (allOK) {
            result = inContext.getResources().getString(R.string.aux_result_ok);
        } else {
            result = inContext.getResources().getString(R.string.aux_result_provide);
            for (String iterable_element : errors) {
                result = result + iterable_element + " ";
            }
        }
        return result;
    }


    public String userOK_SharedPrefs(Context inContext) {

        SharedPreferences sharedpreferences = inContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        Boolean allOK = true;
        String result;
        ArrayList<String> errors = new ArrayList<>();

        if (!sharedpreferences.contains(Name)
                || sharedpreferences.getString(Name, "").equals(""))
        {
            allOK = false;
            errors.add(inContext.getResources().getString(R.string.aux_result_username));
        }
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sharedpreferences.getString(Email, ""));
        if (!sharedpreferences.contains(Email)
                || sharedpreferences.getString(Email, "").equals("")
                || !matcher.matches())
        {
            Pattern phonepattern = Patterns.PHONE;
            Matcher phonematcher = phonepattern.matcher(sharedpreferences.getString(Phone, ""));
            if (!sharedpreferences.contains(Phone)
                    || sharedpreferences.getString(Phone, "").equals("")
                    || !phonematcher.matches()) {
                allOK = false;
                errors.add(inContext.getResources().getString(R.string.aux_result_valid_email_phone));
            }
        }


        if (allOK){
            result = inContext.getResources().getString(R.string.aux_result_ok);
        } else {
            result = inContext.getResources().getString(R.string.aux_result_provide);
            for (String iterable_element : errors) {
                result = result + iterable_element + " ";
            }
        }
        return result;
    }

    public String userHasLinkedAccount(Context inContext) {

        SharedPreferences sharedpreferences = inContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
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



    public String saveUserConfig(Context inContext, String username, String email, String phone, String uid, String regid, String passwd) {
        SharedPreferences sharedpreferences = inContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        String result = "";
        String serverSide = backend.testNetwork(inContext);
        String current_uid = uid;
        String data_status;
        //TODO: Maybe try one last time to get a regid here?
        String sanitized_regid = regid.replaceAll("[^\\d.]", "");
        if (sanitized_regid.matches("")) {
            regid = "4444";
            result = result + "UPDATE regid 4444||";
        }

        if (serverSide.matches("OK")) {
            if (current_uid.matches("")) {
                current_uid = inContext.getResources().getString(R.string.aux_dummy_uid);
            }
            String saveResult = backend.saveUserToBackend(username, email, phone, current_uid, regid);
            if (saveResult.contains("Email found, NEW ID = ")) {
                current_uid = saveResult.replace("Email found, NEW ID = ","");
            } else if (saveResult.contains("Phone found, NEW ID = ")){
                current_uid = saveResult.replace("Phone found, NEW ID = ","");
            } else if (saveResult.contains("NEW ID = ")){
                current_uid = saveResult.replace("NEW ID = ","");
            }
            data_status = "synced";
        } else {
            if (current_uid.matches("")) {
                current_uid = inContext.getResources().getString(R.string.aux_dummy_uid);
                result = result + "UPDATE uid " + current_uid +"||";
            }
            data_status = "unsynced";
        }



        editor.putString(Name, username);
        editor.putString(Email, email);
        editor.putString(Phone, phone);
        editor.putString(Uid, current_uid);
        editor.putString(Regid, regid);
        editor.putString(Status, data_status);
        // TODO: Give the chance to change a password AND CONTROL IT!
        if (!passwd.matches("")) {
            editor.putString(Passwd, passwd);
        }
        editor.apply();

        if (result.contains("ERROR")) {
            return result;
        } else {
            result = result + "OK";
            return result;
        }
    }

}