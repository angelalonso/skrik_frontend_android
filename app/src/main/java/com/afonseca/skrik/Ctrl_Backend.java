package com.afonseca.skrik;



import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Patterns;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;


public class Ctrl_Backend {

    String IP = "192.168.10.229";
    String PORT = "8000";
    String URL = "http://" + IP + ":" + PORT;


/* SERVER CHECK */

    public String testNetwork(Context mContext) {
        /* Logic:
         * - If the mobile phone has no network, or deactivated, we get a "NoNet"
         * - If it is active:
         *   - If we get a timeout within a second, we get a "NoServer"
         *   - Otherwise, if the answer:
         *     - is a Site, even an error one ("<!DOCTYPE") we get an "OK"
         *     - is not a Site, we get a "NoServer"
         */
        String output;

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null) {
            String url_checkserver = URL;

            try {
                /* TODO: TEST that the Timeout is right */
                /* TODO: Configure the server to avoid DDoS */
                String knock = new Tool_AsyncTask().execute(url_checkserver).get(1000, TimeUnit.MILLISECONDS);
                if (knock.startsWith("<!DOCTYPE html>")) {
                    output = "OK";
                } else {
                    output = "NoServer";
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                output = "NoServer";
            } catch (ExecutionException e) {
                e.printStackTrace();
                output = "NoServer";
            } catch (TimeoutException e) {
                e.printStackTrace();
                output = "NoServer";
            }

        } else {
            output = "NoNet";
        }

        return output;
    }

/* USER DATA */

    public String getUsername(String userid) {
        String output = null;
        String url_getusername = URL + "/getusername/" + userid + "/";
        try {
            output = new Tool_AsyncTask().execute(url_getusername).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return output;

    }

    public String saveUserToBackend(String username, String email, String phone, String userid, String regid) {

        /*
        * Throw a message if the email is already in use (maybe get back the data? WELL...
        * */
        String output = null;
        String url_saveuser = "";
        //TODO: Find a way to make clear that it's either email or phone
        Pattern phonePattern = Patterns.PHONE;
        if (!phone.matches("") && phonePattern.matcher(phone).matches()){
            url_saveuser = URL + "/saveid/" + userid + "/name/" + username + "/acc/p" + phone + "/regid/" + regid + "/";
            Log.i("TESTING: ","new phone");
        } else {
            url_saveuser = URL + "/saveid/" + userid + "/name/" + username + "/acc/e" + email + "/regid/" + regid + "/";
            Log.i("TESTING: ","new email");
        }
        try {
            output = new Tool_AsyncTask().execute(url_saveuser).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return output;
    }


    public ArrayList<Data_SearchUser_ListItems> searchUser(String word2Search){
        ArrayList<Data_SearchUser_ListItems> result = new ArrayList<Data_SearchUser_ListItems>();
        String output = null;
        if (!word2Search.matches("")) {
            String url_searchuser = URL + "/searchusers/" + word2Search + "/";
            try {
                output = new Tool_AsyncTask().execute(url_searchuser).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        /*  Work on the result */
            ArrayList<String> stringArray = new ArrayList<String>();
            if (output != null) {
                try {
                    JSONArray jsonArray = new JSONArray(output);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        stringArray.add(jsonArray.getString(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            for (String s : stringArray) {
                try {
                    JSONArray jsonLine = new JSONArray(s);
                    Data_SearchUser_ListItems item = new Data_SearchUser_ListItems();
                    item.setUsername(jsonLine.getString(0));
                    item.setUserID(jsonLine.getString(1));
                    item.setStatus(jsonLine.getString(2));
                    item.setOrder(Integer.parseInt(jsonLine.getString(3)));
                    result.add(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        for (int i=0; i < result.size(); i++) {
            Log.i("TESTING - user found: ", result.get(i).toString());
        }
        return result;
    }

/* MSGS DATA */

    public String updateNewslist(DB_Msgs_Handler sqlMsgsHandler,DB_Users_Handler sqlUsersHandler,String userid) {

        String output = null;
        String callback = "Add ";

        /* First of all, we make sure we get something from the Backend */
        String url_getnewid = URL + "/getnews/" + userid + "/";
        try {
            output = new Tool_AsyncTask().execute(url_getnewid).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        /*  Work on the result */
        ArrayList<String> stringArray = new ArrayList<String>();
        if (output != null){
            try {
                JSONArray jsonArray = new JSONArray(output);
                for (int i = 0; i < jsonArray.length(); i++) {
                    stringArray.add(jsonArray.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        String received_key = "";
        for (String s : stringArray) {
            try {
                JSONArray jsonLine = new JSONArray(s);
                String userfrom = jsonLine.getString(0);
                String message = jsonLine.getString(1);
                String status = jsonLine.getString(2);
                if (status.contains("fwding-")){
                    received_key = status.replace("fwding-","");
                    status = "received";
                }
                String timestamp = jsonLine.getString(3);
                String backend_id = jsonLine.getString(4);
                String query = "SELECT count(*) AS result FROM MSGS WHERE backend_id = '" + backend_id + "' ";
                Cursor c1 = sqlMsgsHandler.selectQuery(query);

                if (c1 == null){
                    String insert_query = "INSERT INTO MSGS (userid_from,userid_to,message,status,timestamp,backend_id) VALUES ('" + userfrom + "','" + userid + "','" + message + "','" + status + "','" + timestamp + "','" + backend_id + "')";
                    sqlMsgsHandler.executeQuery(insert_query);
                } else {
                    String test = Integer.toString(c1.getCount());
                    if (c1.moveToFirst()) {
                        Integer nr_msgs = Integer.parseInt(c1.getString(c1.getColumnIndex("result")));
                        if (nr_msgs == 0){
                            String insert_query = "INSERT INTO MSGS (userid_from,userid_to,message,status,timestamp,backend_id) VALUES ('" + userfrom + "','" + userid + "','" + message + "','" + status + "','" + timestamp + "','" + backend_id + "')";
                            sqlMsgsHandler.executeQuery(insert_query);
                        }
                    }
                }

                c1.close();
                String auxquery = "SELECT count(*) as result FROM MSGS ";
                Cursor c2 = sqlMsgsHandler.selectQuery(auxquery);
                String nr_msgs = "";
                if (c2 != null && c2.getCount() > 0) {
                    if (c2.moveToFirst()) {
                        do {
                            nr_msgs = c2.getString(c2.getColumnIndex("result"));
                        } while (c2.moveToNext());
                    }
                }

                c2.close();

                String query_userexists = "SELECT count(*) AS result FROM USERS WHERE id = '" + userfrom + "' ";
                Cursor c3 = sqlUsersHandler.selectQuery(query_userexists);
                String nr_users = "";
                if (c3 != null && c3.getCount() > 0) {
                    if (c3.moveToFirst()) {
                        do {
                            nr_users = c3.getString(c3.getColumnIndex("result"));
                        } while (c3.moveToNext());
                    }
                }
                if (nr_users.matches("0")) {
                    //TODO: INSERT A NEW USER HERE
                    callback = callback + userfrom + ",";
                }
                c3.close();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (!received_key.matches("")) {
            confirmReceivedBackend(received_key);
        }
        if (callback.matches("Add ")) {
            callback = "";
        }
        return callback;
    }

    public String confirmReceivedBackend(String key) {
        String output = null;
        String url_gotmsg = URL + "/gotmsg/" + key + "/";
        try {
            output = new Tool_AsyncTask().execute(url_gotmsg).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return output;
    }

    public String sendMsgToBackend(String message, String userid_from, String userid_to, String timestamp) {

        /*
        * Throw a message if the email is already in use (maybe get back the data?
        * */
        String output = null;
        String url_saveuser = URL + "/newmessage/" + message + "/userfrom/" + userid_from + "/userto/" + userid_to + "/timestamp/" + timestamp + "/";
        try {
            output = new Tool_AsyncTask().execute(url_saveuser).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return output;
    }

}