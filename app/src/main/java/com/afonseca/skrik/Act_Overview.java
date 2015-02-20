package com.afonseca.skrik;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class Act_Overview extends ActionBarActivity {

    /* Declarations */
    //Extender Activity
    Funcs_Overview functionsOverview = new Funcs_Overview();

    Ctrl_NewsDbHandler newsSQLHandler;
    Ctrl_UsersDbHandler newsUsersSQLHandler;
    Funcs_UserCfg funcsUserCfg = new Funcs_UserCfg();
    Ctrl_Backend backend = new Ctrl_Backend();

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
        newsSQLHandler = new Ctrl_NewsDbHandler(this);
        newsUsersSQLHandler = new Ctrl_UsersDbHandler(this);

        String username = funcsUserCfg.getUsername(context);
        String userid = funcsUserCfg.getUid(context);

        if (serverSide.matches("OK")) { backend.updateNewslist(newsSQLHandler, userid); }
        else { Log.i("TESTING - NETWORK CHECK -- ", "the News List has not been updated, The server is not there"); }

        NewsList_lv = (ListView) findViewById(R.id.newslist_lv);
        Username_tv = (TextView) findViewById(R.id.username_tv);


        Username_tv.setText(username);

        showList(userid);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Context context = getApplicationContext();
        serverSide = serverCheck(context);

        // We would need this to add entries to/from us
        //  So far used in SHOWLIST */

        String username = funcsUserCfg.getUsername(context);
        String userid = funcsUserCfg.getUid(context);

        Username_tv.setText(username);

        if (serverSide.matches("OK")) { backend.updateNewslist(newsSQLHandler, userid); }
        else { Log.i("TESTING - NETWORK CHECK -- ", "the News List has not been updated, because the server is not there"); }

        showList(userid);

    }

    /* Additional Actions' Methods */


    public void updateNews(View view) {
        Context context = getApplicationContext();
        String userid = funcsUserCfg.getUid(context);
        showList(userid);
    }

    private void showList(String user_me) {
        Context mContext = getApplicationContext();

        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss dd/MM/yy");

        ArrayList<Data_OverviewItems> contactList = new ArrayList<>();
        contactList.clear();

        String query = "SELECT count(*) AS msg_nr, userid_from, message, MAX(timestamp) AS timestamp_last FROM MSGS GROUP BY userid_from";

        Cursor c1 = newsSQLHandler.selectQuery(query);
        if (c1 != null && c1.getCount() > 0) {
            if (c1.moveToFirst()) {
                do {
                    Data_OverviewItems overviewItems = new Data_OverviewItems();

                    overviewItems.setNrOfMsgs(c1.getString(c1
                            .getColumnIndex("msg_nr")));
                    overviewItems.setNews(c1.getString(c1
                            .getColumnIndex("message")));

                    String timestamp_raw = c1.getString(c1.getColumnIndex("timestamp_last"));
                    String timestamp = fmt.format(new Time(Long.parseLong(timestamp_raw + "000")));

                    overviewItems.setTimestamp(timestamp);
                    String userid_from = c1.getString(c1.getColumnIndex("userid_from"));
                    String userid_name = functionsOverview.getUsername(mContext,userid_from);
                    overviewItems.setUserid(userid_from);
                    overviewItems.setUsername(userid_name);

                    contactList.add(overviewItems);

                } while (c1.moveToNext());
            }
        }

        Ctrl_OverviewListAdapter contactListAdapter = new Ctrl_OverviewListAdapter(
                Act_Overview.this, contactList);
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

    public void listviewClick(View view) {
        Context mContext = getApplicationContext();
        TextView userid_tv = (TextView) view.findViewById(R.id.id_tv);
        String userid_other = userid_tv.getText().toString();
        String userid_me = funcsUserCfg.getUid(mContext);
        TextView username_tv = (TextView) view.findViewById(R.id.username_tv);
        String username = username_tv.getText().toString();
        Intent intent = new Intent(this, Act_Channel.class);
        Bundle b = new Bundle();
        b.putString("userid_other", userid_other);
        b.putString("userid_me", userid_me);
        b.putString("username", username);
        intent.putExtras(b);
        startActivity(intent);
    }
    /* "GOTO" Calls */

    public void gotoUserConfig(View view) {
        /** Called when the user clicks the Config button */
        Intent intent = new Intent(this, Act_UserCfg.class);
        startActivity(intent);
    }

}
