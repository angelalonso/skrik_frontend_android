package com.afonseca.skrik;



import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Patterns;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class Toolbox_Backend {

    String IP = "fonseca.de.com";
    String PORT = "8044";
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


    public String getDataFromPhoneNr(String phone) {
        String output = null;
        String url_getusername = URL + "/getdataforphone/" + phone + "/";
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
        } else {
            url_saveuser = URL + "/saveid/" + userid + "/name/" + username + "/acc/e" + email + "/regid/" + regid + "/";
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




    public ArrayList<Data_SearchContact_ListItems> searchUser(String word2Search){
        ArrayList<Data_SearchContact_ListItems> result = new ArrayList<Data_SearchContact_ListItems>();
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
                    Data_SearchContact_ListItems item = new Data_SearchContact_ListItems();
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
        /*for (int i=0; i < result.size(); i++) {
            Log.i("TESTING - user found: ", result.get(i).toString());
        }*/
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
        if (output != null && !output.contains("<!DOCTYPE")){
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
                try{
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
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                String auxquery = "SELECT count(*) as result FROM MSGS ";
                try {
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

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                if (!callback.contains(userfrom)) {
                    String query_userexists = "SELECT count(*) AS result FROM USERS WHERE id = '" + userfrom + "' ";
                    try {
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

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
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

        String output = null;
        String url_newmessage = URL + "/newmessage/" + message + "/userfrom/" + userid_from + "/userto/" + userid_to + "/timestamp/" + timestamp + "/";
        try {
            output = new Tool_AsyncTask().execute(url_newmessage).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return output;
    }

    public String sendCryptMsgToBackend(String message, String userid_from, String userid_to, String timestamp) {

        String publicKey = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDEFCCvmp2QWWoeYa2W4HOnV70e9qLsGnDWP2R4qhiZVjj8Igdqe4GbqmFPYZEqQgGl3XieFGjGBQT9IYPOLHLs7j85ZW7qjJ9rOkNvZ66Rm6HottH3ULPCWvgah0SOo87ny2hi+yhnNoDt35e5FEX0w2RMaiAHQTefepnXjKDfEJm/MLEOSVCVEkFBByl2Nv+/RmPXpiOZd87LBQFkLv70AUs1yeA7jGfEkes6LA/WdXtaAXgqqvoR87wXF3oiFrRrJCxd4WUNqjc7xivpxKXn3NiCXpfDzOxigc3ITpP26+6Ngqlk3gFBE3r0LMKkfzDQ+7qGb4NB2W5p6DSvp9Yr pi@raspberrypi";

        String output = null;
        String url_rawmessage = "/newmessage/" + message + "/userfrom/" + userid_from + "/userto/" + userid_to + "/timestamp/" + timestamp + "/";
        String url_encrypted = "";

        try {
            url_encrypted =  URL + "/skrik/" + encryptRSA(url_rawmessage, publicKey) + "/";
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            output = new Tool_AsyncTask().execute(url_encrypted).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return output;
    }

    public String encryptRSA(String message,String keyString) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

// http://stackoverflow.com/questions/25211749/rsa-encryption-using-public-key-in-android-for-a-php-server
        //String keyString = getPublicKeyStringFromPemFormat(publicKeyString, false);

        // converts the String to a PublicKey instance
        byte[] keyBytes = Base64.decode(keyString.getBytes("utf-8"), Base64.NO_WRAP);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey key = keyFactory.generatePublic(spec);

        // decrypts the message
        byte[] dectyptedText = null;
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        dectyptedText = cipher.doFinal(Base64.decode(message.getBytes("utf-8"), Base64.NO_WRAP));
        return Base64.encodeToString(dectyptedText, Base64.NO_WRAP);
    }
}