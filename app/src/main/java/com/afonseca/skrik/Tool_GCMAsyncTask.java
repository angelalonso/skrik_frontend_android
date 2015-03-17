package com.afonseca.skrik;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;


class Tool_GCMAsyncTask extends AsyncTask<String, Void, String> {


    String SENDER_ID = "610647426983";
    GoogleCloudMessaging gcm;
    String regid;

    Toolbox_Sharedprefs toolbox_SP = new Toolbox_Sharedprefs();

    private Context mContext;

    public Tool_GCMAsyncTask(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... urls) {
        Log.i("TESTING WFLOW", "2");
        String msg = "";
        Log.d("Register GCM", "started");
        try {

            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(mContext);
                Log.d("GCM", gcm.toString());
            }

            regid = gcm.register(SENDER_ID); //////NULL POINTER EXCEPTION
            msg = "Device registered, registration ID=" + regid;
            Log.i("TESTING REGID - in",regid);
            toolbox_SP.setRegid(mContext,regid);

        } catch (IOException ex) {
            Log.i("TESTING REGID - ERROR",ex.getMessage());
            return "Error :" + ex.getMessage();

            // If there is an error, don't just keep trying to register.
            // Require the user to click a button again, or perform
            // exponential back-off.
        }
        Log.i("TESTING REGID",regid);

        return regid;
    }

    protected void onPostExecute(Boolean result) {

    }
}