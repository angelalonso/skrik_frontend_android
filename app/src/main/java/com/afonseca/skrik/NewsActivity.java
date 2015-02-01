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

/**
 * This Activity should show the News for the configured user
 */
public class NewsActivity extends ActionBarActivity {

    /* Declarations */

    Control_NewsDbHandler newsSQLHandler;
    Control_NewsUsersDbHandler newsUsersSQLHandler;
    Control_Userconfig controlUserconfig = new Control_Userconfig();
    Control_News controlNews = new Control_News();
    Control_BackendHandler backend = new Control_BackendHandler();

    String serverSide;

    ListView NewsList_lv;
    TextView Username_tv;

    /* General Behaviour Methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Context context = getApplicationContext();
        serverSide = serverCheck(context);

        // We would need this to add entries to/from us
        //  So far used in SHOWLIST */
        newsSQLHandler = new Control_NewsDbHandler(this);
        newsUsersSQLHandler = new Control_NewsUsersDbHandler(this);

        String username = controlUserconfig.getUsername(context);
        String userid = controlUserconfig.getUid(context);

        if (serverSide.matches("OK")) { backend.updateNewslist(newsSQLHandler, userid); }
        else { Log.i("TESTING - NETWORK CHECK -- ", "the News List has not been updated, The server is not there"); }

        NewsList_lv = (ListView) findViewById(R.id.newslist_lv);
        Username_tv = (TextView) findViewById(R.id.username_tv);

        Username_tv.setText(username);

        showList(userid);
/*

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

        */
    }

    @Override
    protected void onResume() {
        super.onResume();

        Context context = getApplicationContext();
        serverSide = serverCheck(context);

        // We would need this to add entries to/from us
        //  So far used in SHOWLIST */

        String userid = controlUserconfig.getUid(context);

        if (serverSide.matches("OK")) { backend.updateNewslist(newsSQLHandler, userid); }
        else { Log.i("TESTING - NETWORK CHECK -- ", "the News List has not been updated, because the server is not there"); }

        showList(userid);

    }

    /* Additional Actions' Methods */


    public void updateNews(View view) {
        Context context = getApplicationContext();
        String userid = controlUserconfig.getUid(context);
        showList(userid);
    }

    private void showList(String user_me) {
        Context mContext = getApplicationContext();

        ArrayList<Data_NewsListItems> contactList = new ArrayList<>();
        contactList.clear();

        //String query = "SELECT * FROM PHONE_CONTACTS ";
        String query = "SELECT count(*) as msg_nr, userid_from, message, MAX(timestamp) as timestamp_last FROM NEWS GROUP BY userid_from";

        Cursor c1 = newsSQLHandler.selectQuery(query);
        if (c1 != null && c1.getCount() > 0) {
            if (c1.moveToFirst()) {
                do {
                    Data_NewsListItems newsListItems = new Data_NewsListItems();

                    newsListItems.setNrOfMsgs(c1.getString(c1
                            .getColumnIndex("msg_nr")));
                    newsListItems.setNews(c1.getString(c1
                            .getColumnIndex("message")));
                    newsListItems.setTimestamp(c1.getString(c1
                            .getColumnIndex("timestamp_last")));
                    String userid_from = c1.getString(c1.getColumnIndex("userid_from"));
                    String userid_name = controlNews.getUsername(mContext,userid_from);
                    Log.i("TESTING NEWSUSER - ",userid_from + " is " + userid_name);
                    newsListItems.setUsername(userid_name);
                    //newsListItems.setUsername(c1.getString(c1
                    //        .getColumnIndex("userid_from")));

                    contactList.add(newsListItems);

                } while (c1.moveToNext());
            }
        }

        Control_NewsListAdapter contactListAdapter = new Control_NewsListAdapter(
                NewsActivity.this, contactList);
        NewsList_lv.setAdapter(contactListAdapter);

    }

    /* Check Functions */

    public String serverCheck(Context mContext) {
        TextView server_tv = (TextView) findViewById(R.id.server_tv);
        String status = backend.testNetwork(mContext);
        switch(status) {
            case "OK":
                server_tv.setTextColor(getResources().getColor(R.color.Lime));
                break;
            case "NoServer":
                server_tv.setTextColor(getResources().getColor(R.color.Gold));
                break;
            case "NoNet":
                server_tv.setTextColor(getResources().getColor(R.color.Red));
                break;
            default:
                server_tv.setTextColor(getResources().getColor(R.color.DarkViolet));
                break;
        }
        return status;
    }

    /* "GOTO" Calls */

    public void gotoUserConfig(View view) {
        /** Called when the user clicks the Config button */
        Intent intent = new Intent(this, Act_UserCfg.class);
        startActivity(intent);
    }
}
