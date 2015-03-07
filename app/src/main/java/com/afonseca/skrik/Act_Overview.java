package com.afonseca.skrik;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class Act_Overview extends ActionBarActivity {

    /* Declarations */
    //Extender Activity
    Funcs_Overview functionsOverview = new Funcs_Overview();

    DB_Msgs_Handler newsMsgsSQLHandler;
    DB_Users_Handler newsUsersSQLHandler;

    Funcs_UserCfg funcsUserCfg = new Funcs_UserCfg();
    Ctrl_Backend backend = new Ctrl_Backend();

    String serverSide;

    ListView NewsList_lv;
    TextView Username_tv;

    /* General Behaviour Methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        Context mContext = getApplicationContext();
        serverSide = serverCheck(mContext);

        // We would need this to add entries to/from us
        //  So far used in SHOWLIST */
        newsMsgsSQLHandler = new DB_Msgs_Handler(this);
        newsUsersSQLHandler = new DB_Users_Handler(this);

        String username = funcsUserCfg.getUsername(mContext);
        String userid = funcsUserCfg.getUid(mContext);

        NewsList_lv = (ListView) findViewById(R.id.newslist_lv);
        Username_tv = (TextView) findViewById(R.id.username_search_tv);

        Username_tv.setText(username);

        if (serverSide.matches("OK")) {
            // We get the list of new messages and if any is from an unknown user, we save its details locally(getUsername does it for us)
            String update = backend.updateNewslist(newsMsgsSQLHandler, newsUsersSQLHandler, userid);
            if (update.contains("Add ")) {
                String[] NewUsers = update.replace("Add ", "").split(",");
                for (int c = 0; c < NewUsers.length; c++) {
                    String newUserName = functionsOverview.getUsername(mContext,NewUsers[c]);
                }
            }
        }
        // TODO: create a showempty for other "emptinesses (maybe just a "look for users" message?)
        if (userid.matches(mContext.getResources().getString(R.string.aux_dummy_uid))) {
            showEmpty();
        } else {
            showList(userid);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Context mContext = getApplicationContext();
        serverSide = serverCheck(mContext);

        // We would need this to add entries to/from us
        //  So far used in SHOWLIST */

        String username = funcsUserCfg.getUsername(mContext);
        String userid = funcsUserCfg.getUid(mContext);

        Username_tv.setText(username);

        if (serverSide.matches("OK")) {
            // We get the list of new messages and if any is from an unknown user, we save its details locally(getUsername does it for us)
            String update = backend.updateNewslist(newsMsgsSQLHandler, newsUsersSQLHandler, userid);
            if (update.contains("Add ")) {
                String[] NewUsers = update.replace("Add ", "").split(",");
                for (int c = 0; c < NewUsers.length; c++) {
                    String newUserName = functionsOverview.getUsername(mContext,NewUsers[c]);
                }
            }
        }

        if (userid.matches(mContext.getResources().getString(R.string.aux_dummy_uid))) {
            showEmpty();
        } else {
            showList(userid);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_user_settings:
                gotoUserConfig();
                return true;
            case R.id.action_check_tables:
                gotoShowDB();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /* Additional Actions' Methods */


    public void updateOverview(View view) {
        Context context = getApplicationContext();
        String userid = funcsUserCfg.getUid(context);
        showList(userid);
    }

    private void showEmpty() {


        NewsList_lv.setEmptyView( findViewById( R.id.empty_list_view ) );
        NewsList_lv.setAdapter( new ArrayAdapter( this, R.layout.activity_overview, new ArrayList() ) );

    }


    private void showList(String user_me) {
        Context mContext = getApplicationContext();
        //NewsList_lv.setEmptyView(findViewById(android.R.id.empty));

        SimpleDateFormat fmt = new SimpleDateFormat(getString(R.string.aux_date_format));

        ArrayList<Data_OverviewItems> contactList = new ArrayList<>();
        contactList.clear();
        //TODO:
        //    TODO: For each new message received OR sent, add the user (if it was not there yet.
        //    TODO: Get the list of users (not me, and not blacklisted)
        //    TODO: for each, get the latest message and timestamp, plus the amount of messages not read
        //String query = "SELECT count(*) AS msg_nr, userid_from, message, MAX(timestamp) AS timestamp_last FROM MSGS GROUP BY userid_from";
        String query = "SELECT id, username, blacklisted FROM USERS where blacklisted=0 and id<>'" + user_me + "'";
        Cursor c_1 = newsUsersSQLHandler.selectQuery(query);
        if (c_1 != null && c_1.getCount() > 0) {
            if (c_1.moveToFirst()) {
                do {
                    Data_OverviewItems overviewItems = new Data_OverviewItems();
                    String userid = c_1.getString(c_1.getColumnIndex("id"));
                    String username = c_1.getString(c_1.getColumnIndex("username"));
                    overviewItems.setUsername(username);
                    String msg_query = "SELECT count(message) as nr_msgs, message, timestamp FROM MSGS WHERE userid_from='" + userid + "'";
                    Cursor c_2 = newsMsgsSQLHandler.selectQuery(msg_query);
                    if (c_2 != null && c_2.getCount() > 0) {
                        if (c_2.moveToFirst()) {
                            do {
                                String nr_msgs = c_2.getString(c_2.getColumnIndex("nr_msgs"));
                                String msg = c_2.getString(c_2.getColumnIndex("message"));
                                String timestamp_raw = c_2.getString(c_2.getColumnIndex("timestamp"));
                                if (msg.matches("null")) {
                                    Log.i("TESTING   ->", nr_msgs + msg + timestamp_raw);
                                }
                                /*if (!nr_msgs.matches(null)) {overviewItems.setNrOfMsgs(nr_msgs);}
                                if (!msg.matches(null)) {overviewItems.setNews(msg);}
                                if (!timestamp_raw.matches(null)) {
                                    String timestamp = fmt.format(new Time(Long.parseLong(timestamp_raw + "000")));
                                    overviewItems.setTimestamp(timestamp);
                                }
                                */
                            } while (c_2.moveToNext());
                        }
                    }
                    contactList.add(overviewItems);
                    c_2.close();

                } while (c_1.moveToNext());
            }
        }
        c_1.close();
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
        if (userid_me.matches("99999999999999")){
            Toast.makeText(getApplicationContext(), R.string.msg_user_not_registered, Toast.LENGTH_LONG).show();
            gotoUserConfig();
        } else {
            TextView username_tv = (TextView) view.findViewById(R.id.username_search_tv);
            String username = username_tv.getText().toString();
            Intent intent = new Intent(this, Act_Channel.class);
            Bundle b = new Bundle();
            b.putString("userid_other", userid_other);
            b.putString("userid_me", userid_me);
            b.putString("username", username);
            intent.putExtras(b);
            startActivity(intent);
        }
    }


    /* "GOTO" Calls */

    public void gotoUserConfig() {
        Intent intent = new Intent(this, Act_UserCfg.class);
        startActivity(intent);
    }
    // Version for xml elements
    public void gotoUserConfig(View view) {
        Intent intent = new Intent(this, Act_UserCfg.class);
        startActivity(intent);
    }

    public void gotoShowDB() {
        /** Called when the user clicks the Config button */
        Intent intent = new Intent(this, Act_Tables.class);
        startActivity(intent);
    }

    public void gotoSearchUser(View view) {
        /** Called when the user clicks the Config button */
        Intent intent = new Intent(this, Act_SearchUser.class);
        startActivity(intent);
    }


    //TODO: BE DELETED

    public void clearDB(View view) {

        Context mContext = getApplicationContext();
        DB_Msgs_Handler msgsSQLHandler = new DB_Msgs_Handler(mContext);
        String Clearquery = "DELETE FROM MSGS WHERE id=id;";
        msgsSQLHandler.executeQuery(Clearquery);
    }

    //http://developer.android.com/guide/topics/ui/actionbar.html


}
