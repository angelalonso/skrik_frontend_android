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


//import com.google.android.gcm.GCMRegistrar;

import java.util.ArrayList;

/* TODO:
- If user details are empty or wrong -> go to Act_UserCfg
- If user opens or comes back here -> update data
- If the REGID is missing/wrong/older than 1 day -> get Regid from GCM
  - If GCM returns a new REGID -> update in server
  - If GCM DOES NOT return a REGID -> ...
    - ...if current REGID is correct or temporary -> keep it
    - ...if current REGID is empty -> get a temporary one
- If contact is (short) clicked -> open Channel on contact
- If contact is LONG-clicked -> Show options: blacklist, open Channel // also in  Act_SearchContact //
- If new msg(s) is received -> ...
  - ...if contact exists on list -> Show number of new msgs
  - ...if contact does not exist on list -> if it is not blacklisted -> add it, Show number of new msgs

 */

public class Act_Overview extends ActionBarActivity {
    /*TODO: empty overview has to be just empty
      TODO: if user was registered offline, show a popup to retry and no message

    **/

    /* Declarations */
/*
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    String SENDER_ID = "610647426983";
    //GoogleCloudMessaging gcm;
    String regid;
*/
    //Extender Activity
    Toolbox_Sharedprefs toolbox_SP = new Toolbox_Sharedprefs();
    Toolbox_Backend backend = new Toolbox_Backend();
    Toolbox_LocalSQLite toolbox_localSQL = new Toolbox_LocalSQLite();
    Tool_Debug debugger = new Tool_Debug();



    DB_Msgs_Handler newsMsgsSQLHandler;
    DB_Users_Handler newsUsersSQLHandler;

    Context mContext;

    String username;
    String userid;

    String serverSide;

    ListView NewsList_lv;
    TextView Username_tv;

    /* General Behaviour Methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        mContext = getApplicationContext();

        NewsList_lv = (ListView) findViewById(R.id.newslist_lv);
        Username_tv = (TextView) findViewById(R.id.username_search_tv);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mContext = getApplicationContext();
        serverSide = serverCheck(mContext);

        toolbox_SP.correctRegid(mContext,this);

        if (check_UserData(mContext).matches("OK")) {
            showWhatever();
        } else {
            Intent intent = new Intent(this, Act_UserCfg.class);
            startActivity(intent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_overview, menu);
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
            case R.id.action_clear_alldata:
                clearDB();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /* Additional Actions' Methods */

    public String check_UserData(Context mContext){
        String result = toolbox_SP.userOK_SharedPrefs(mContext);
        return result;
    }

    public void updateOverview(View view) {
        if (check_UserData(mContext).matches("OK")) {
            showWhatever();
        } else {
            Intent intent = new Intent(this, Act_UserCfg.class);
            startActivity(intent);
        }
    }

    private void showWhatever(){
        userid = toolbox_SP.getUid(mContext);
        username = toolbox_SP.getUsername(mContext);
        Username_tv.setText(username);
        if (serverSide.matches("OK")) {
            // We get the list of new messages and if any is from an unknown user, we save its details locally(getUsername does it for us)
            String update = backend.updateNewslist(newsMsgsSQLHandler, newsUsersSQLHandler, userid);

            if (update.contains("Add ")) {
                String[] NewUsers = update.replace("Add ", "").split(",");
                for (int c = 0; c < NewUsers.length; c++) {
                    toolbox_localSQL.getUsername(mContext,NewUsers[c]);
                }
            }
        }
        if (userid.matches(mContext.getResources().getString(R.string.aux_dummy_uid))) {
            showEmpty();
        } else {
            showList(userid);
        }
    }

    private void showEmpty() {


        NewsList_lv.setEmptyView( findViewById( R.id.empty_list_view ) );
        NewsList_lv.setAdapter( new ArrayAdapter( this, R.layout.activity_overview, new ArrayList() ) );

    }


    private void showList(String user_me) {

        newsUsersSQLHandler = new DB_Users_Handler(this);
        newsMsgsSQLHandler = new DB_Msgs_Handler(this);

        ArrayList<Data_OverviewItems> contactList = new ArrayList<>();
        contactList.clear();
        ArrayList<String> usernames = new ArrayList<String>();
        ArrayList<String> userids = new ArrayList<String>();
        String query = "SELECT id, username FROM USERS where blacklisted=0 and id<>'" + user_me + "'";
        Cursor c_1 = newsUsersSQLHandler.selectQuery(query);
        if (c_1 != null && c_1.getCount() > 0) {
            if (c_1.moveToFirst()) {
                do {
                    userids.add(c_1.getString(c_1.getColumnIndex("id")));
                    usernames.add(c_1.getString(c_1.getColumnIndex("username")));
                } while (c_1.moveToNext());
            }
        }
        c_1.close();
        for (int c = 0; c < userids.size(); c++) {
            Data_OverviewItems overviewItems = new Data_OverviewItems();
            overviewItems.setUsername(usernames.get(c));

            String nr_ofmsgs = "";
            String newmsg_query = "SELECT count(message) as nr_msgs FROM MSGS WHERE (userid_from='" + userids.get(c) + "' AND status='received') ";
            Cursor c_2 = newsMsgsSQLHandler.selectQuery(newmsg_query);
            if (c_2 != null && c_2.getCount() > 0) {
                if (c_2.moveToFirst()) {
                    do {
                        nr_ofmsgs = c_2.getString(c_2.getColumnIndex("nr_msgs"));
                        if (Integer.parseInt(nr_ofmsgs) > 0) {
                            overviewItems.setNrOfMsgs(nr_ofmsgs);
                        }
                    } while (c_2.moveToNext());
                }
            }
            c_2.close();

            String latest_ts;
            String latest_ts_raw = "";
            Log.i("TESTING - looking for", userids.get(c));
            String latestts_query = "SELECT MAX(timestamp) as timestamp FROM MSGS WHERE (userid_from='" + userids.get(c) + "' OR userid_to='" + userids.get(c) + "')";

            if (nr_ofmsgs.matches("0")) {
                String allmsg_query = "SELECT count(message) as nr_msgs FROM MSGS WHERE (userid_from='" + userids.get(c) + "' OR userid_to='" + userids.get(c) + "')";
                Cursor c_aux = newsMsgsSQLHandler.selectQuery(allmsg_query);
                if (c_aux != null && c_aux.getCount() > 0) {
                    if (c_aux.moveToFirst()) {
                        do {
                            nr_ofmsgs = c_aux.getString(c_aux.getColumnIndex("nr_msgs"));
                        } while (c_aux.moveToNext());
                    }
                }
                c_aux.close();
                if (!nr_ofmsgs.matches("0")) {
                    Cursor c_3 = newsMsgsSQLHandler.selectQuery(latestts_query);
                    if (c_3 != null && c_3.getCount() > 0) {
                        if (c_3.moveToFirst()) {
                            do {
                                latest_ts_raw = c_3.getString(c_3.getColumnIndex("timestamp"));
                                latest_ts = Tool_Timestamp.getBeauty(mContext, Integer.parseInt(latest_ts_raw));
                                overviewItems.setTimestamp(latest_ts);
                            } while (c_3.moveToNext());
                        }
                    }
                    c_3.close();
                    String latestmsg_query = "SELECT message FROM MSGS WHERE (userid_from='" + userids.get(c) + "'  OR userid_to='" + userids.get(c) + "' AND timestamp='" + latest_ts_raw + "')";
                    Cursor c_4 = newsMsgsSQLHandler.selectQuery(latestmsg_query);
                    if (c_4 != null && c_4.getCount() > 0) {
                        if (c_4.moveToFirst()) {
                            do {
                                overviewItems.setNews(c_4.getString(c_4.getColumnIndex("message")));
                            } while (c_4.moveToNext());
                        }
                    }
                    c_4.close();
                }

            } else {
                Cursor c_3 = newsMsgsSQLHandler.selectQuery(latestts_query);
                if (c_3 != null && c_3.getCount() > 0) {
                    if (c_3.moveToFirst()) {
                        do {
                            latest_ts_raw = c_3.getString(c_3.getColumnIndex("timestamp"));
                            latest_ts = Tool_Timestamp.getBeauty(mContext, Integer.parseInt(latest_ts_raw));
                            overviewItems.setTimestamp(latest_ts);
                        } while (c_3.moveToNext());
                    }
                }
                c_3.close();
                String latestmsg_query = "SELECT message FROM MSGS WHERE (userid_from='" + userids.get(c) + "'  OR userid_to='" + userids.get(c) + "' AND timestamp='" + latest_ts_raw + "')";
                Cursor c_4 = newsMsgsSQLHandler.selectQuery(latestmsg_query);
                if (c_4 != null && c_4.getCount() > 0) {
                    if (c_4.moveToFirst()) {
                        do {
                            overviewItems.setNews(c_4.getString(c_4.getColumnIndex("message")));
                        } while (c_4.moveToNext());
                    }
                }
                c_4.close();

            }

            contactList.add(overviewItems);
        }
        ListAdapter_Overview contactListAdapter = new ListAdapter_Overview(
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
    //TODO: TUNE THIS
    public void listviewClick(View view) {
        TextView userid_tv = (TextView) view.findViewById(R.id.id_tv);
        String userid_other = userid_tv.getText().toString();
        if (userid_other.matches("")){
            TextView username_tv = (TextView) view.findViewById(R.id.username_search_tv);
            String username_other = username_tv.getText().toString();
            userid_other = toolbox_localSQL.getUserid(mContext, username_other);
        }
        String userid_me = toolbox_SP.getUid(mContext);
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
        Intent intent = new Intent(this, Act_SearchContact.class);
        startActivity(intent);
    }


    public void clearDB() {
        Context mContext = getApplicationContext();
        DB_Msgs_Handler msgsSQLHandler = new DB_Msgs_Handler(mContext);
        String Clearquery = "DELETE FROM MSGS WHERE id=id;";
        msgsSQLHandler.executeQuery(Clearquery);
        showWhatever();
    }

}
