package com.afonseca.skrik;



import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by afonseca on 1/24/2015.
 */
public class Control_BackendHandler {

    /* I keep these separated, just in case */
    String IP = "192.168.10.229";
    String PORT = "8000";
    String URL = "http://" + IP + ":" + PORT;

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
                String knock = new Control_StringAsyncTask().execute(url_checkserver).get(1000, TimeUnit.MILLISECONDS);
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

    public String getnewID() {
        String output = null;

        String url_getnewid = URL + "/getnewid/";
        try {
            output = new Control_StringAsyncTask().execute(url_getnewid).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return output;
    }

    public String saveUserToBackend(String username, String email, String userid, String regid) {
        String output = null;
        String url_saveuser = URL + "/saveid/" + userid + "/name/" + username + "/email/" + email + "/regid/" + regid + "/";
        try {
            output = new Control_StringAsyncTask().execute(url_saveuser).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return output;
    }

    public void updateNewslist(Control_NewsDbHandler sqlHandler,String userid) {
        String output = null;

        /* First of all, we make sure we get something from the Backend */
        String url_getnewid = URL + "/getnews/" + userid + "/";
        try {
            output = new Control_StringAsyncTask().execute(url_getnewid).get();
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

        for (String s : stringArray) {
            try {
                JSONArray jsonLine = new JSONArray(s);
                String userfrom = jsonLine.getString(0);
                String message = jsonLine.getString(1);
                String status = jsonLine.getString(2);
                String timestamp = jsonLine.getString(3);
                String backend_id = jsonLine.getString(4);
                String query = "SELECT count(*) as result FROM NEWS WHERE backend_id = '" + backend_id + "' ";
                Cursor c1 = sqlHandler.selectQuery(query);
                if (c1 == null){
                    String insert_query = "INSERT INTO NEWS (userid_from,userid_to,message,status,timestamp,backend_id) VALUES ('" + userfrom + "','" + userid + "','" + message + "','" + status + "','" + timestamp + "','" + backend_id + "')";
                    sqlHandler.executeQuery(insert_query);
                } else {
                    String test = Integer.toString(c1.getCount());
                    if (c1.moveToFirst()) {
                        Integer nr_msgs = Integer.parseInt(c1.getString(c1.getColumnIndex("result")));
                        if (nr_msgs == 0){
                            String insert_query = "INSERT INTO NEWS (userid_from,userid_to,message,status,timestamp,backend_id) VALUES ('" + userfrom + "','" + userid + "','" + message + "','" + status + "','" + timestamp + "','" + backend_id + "')";
                            sqlHandler.executeQuery(insert_query);
                        }
                    }
                }
                c1.close();
                String auxquery = "SELECT count(*) as result FROM NEWS ";
                Cursor c2 = sqlHandler.selectQuery(auxquery);
                String nr_msgs = "";
                if (c2 != null && c2.getCount() > 0) {
                    if (c2.moveToFirst()) {
                        do {
                            nr_msgs = c2.getString(c2.getColumnIndex("result"));
                        } while (c2.moveToNext());
                    }
                }

                c2.close();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
