package com.afonseca.skrik;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by afonseca on 1/24/2015.
 */
public class NewsActivity extends ActionBarActivity {

    Control_NewsDbHandler sqlHandler;
    ListView NewsList_lv;
    Button btnsubmit;
    Button btndelete;

    Control_Userconfig controlUserconfig = new Control_Userconfig();
    Control_BackendHandler backend = new Control_BackendHandler();
    TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        checker();
        /* We would need this to add entries to/from us
        *  So far used in SHOWLIST */
        sqlHandler = new Control_NewsDbHandler(this);

        Context context = getApplicationContext();
        String userid = controlUserconfig.getUid(context);

        backend.updateNewslist(sqlHandler,userid);

        NewsList_lv = (ListView) findViewById(R.id.newslist_lv);

        /* SHOWLIST */
        showList(userid);


        /* AUX BUTTONS */
        btnsubmit = (Button) findViewById(R.id.aux_btn);
        btnsubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                String userid = controlUserconfig.getUid(context);

                String message = "good news";
                String status = "sent";
                String timestamp = "1422169444";

                String query = "INSERT INTO NEWS (userid_from,userid_to,message,status,timestamp,backend_id) VALUES ('"
                        + userid + "','" + userid + "','" + message + "','" + status + "','" + timestamp + "','444444444444')";
                sqlHandler.executeQuery(query);

                showList(userid);
            }
        });
        btndelete = (Button) findViewById(R.id.del_btn);
        btndelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String query = "DELETE FROM NEWS WHERE userid_to = userid_to";
                sqlHandler.executeQuery(query);

                Context context = getApplicationContext();
                String userid = controlUserconfig.getUid(context);

                backend.updateNewslist(sqlHandler,userid);
                showList(userid);
            }
        });

        /* END OF AUX BUTTONS */

        username = (TextView) findViewById(R.id.username_tv);

        String output = controlUserconfig.getUsername(context);

        String auxquery = "SELECT count(*) as result FROM NEWS ";
        Cursor c1 = sqlHandler.selectQuery(auxquery);
        String nr_msgs = "";
        if (c1 != null && c1.getCount() > 0) {
            if (c1.moveToFirst()) {
                do {
                    nr_msgs = c1.getString(c1.getColumnIndex("result"));
                } while (c1.moveToNext());
            }
        }
        c1.close();

        username.setText(nr_msgs + " " + output);
    }

    @Override
    protected void onResume() {
        super.onResume();

        username = (TextView) findViewById(R.id.username_tv);

        checker();
        Context context = getApplicationContext();
        String output = controlUserconfig.getUsername(context);

        String auxquery = "SELECT count(*) as result FROM NEWS ";
        Cursor c1 = sqlHandler.selectQuery(auxquery);
        String nr_msgs = "";
        if (c1 != null && c1.getCount() > 0) {
            if (c1.moveToFirst()) {
                do {
                    nr_msgs = c1.getString(c1.getColumnIndex("result"));
                } while (c1.moveToNext());
            }
        }
        c1.close();

        username.setText(nr_msgs + " " + output);

        String userid = controlUserconfig.getUid(context);

        backend.updateNewslist(sqlHandler,userid);
    }

    private void showList(String user_me) {

        ArrayList<Data_NewsListItems> contactList = new ArrayList<Data_NewsListItems>();
        contactList.clear();

        //String query = "SELECT * FROM PHONE_CONTACTS ";
        String query = "SELECT count(*) as msg_nr, userid_from, message, MAX(timestamp) as timestamp_last FROM NEWS GROUP BY userid_from";

        Cursor c1 = sqlHandler.selectQuery(query);
        //if (c1 != null && c1.getCount() != 0) {
        if (c1 != null && c1.getCount() > 0) {
            if (c1.moveToFirst()) {
                do {
                    Data_NewsListItems newsListItems = new Data_NewsListItems();


                    newsListItems.setNrOfMsgs(c1.getString(c1
                            .getColumnIndex("msg_nr")));
                    newsListItems.setUsername(c1.getString(c1
                            .getColumnIndex("userid_from")));
                    newsListItems.setNews(c1.getString(c1
                            .getColumnIndex("message")));
                    newsListItems.setTimestamp(c1.getString(c1
                            .getColumnIndex("timestamp_last")));

                    contactList.add(newsListItems);

                } while (c1.moveToNext());
            }
        }
        c1.close();

        Control_NewsListAdapter contactListAdapter = new Control_NewsListAdapter(
                NewsActivity.this, contactList);
        NewsList_lv.setAdapter(contactListAdapter);

    }

    public void checker() {
        TextView server_tv = (TextView) findViewById(R.id.server_tv);
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            /* TODO: CHECK THAT THE INTERNET CONNECTION TIMES OUT! OTHERWISE IT GETS BLOCKED */
            server_tv.setTextColor(Color.GREEN);
        } else {
            server_tv.setTextColor(Color.RED);
        }
    }

    /** Called when the user clicks the Config button */
    public void gotoUserConfig(View view) {
        Intent intent = new Intent(this, UserConfigActivity.class);
        startActivity(intent);
    }
}
