package com.afonseca.skrik;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class Act_Channel extends ActionBarActivity {

    /* Declarations */

    DB_Msgs_Handler msgsSQLHandler;
    DB_Users_Handler usersSQLHandler;
    Toolbox_Backend backend = new Toolbox_Backend();
    Toolbox_LocalSQLite toolbox_SP = new Toolbox_LocalSQLite();

    Context mContext;

    String serverSide;
    String other_userid;
    String me_userid;
    String username;

    TextView Username_tv;
    ListView ChatList_lv;

    /* General Behaviour Methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        msgsSQLHandler = new DB_Msgs_Handler(this);
        usersSQLHandler = new DB_Users_Handler(this);
        mContext = getApplicationContext();
        serverSide = serverCheck(mContext);

        Bundle b = getIntent().getExtras();
        other_userid = b.getString("userid_other");
        me_userid = b.getString("userid_me");
        username = b.getString("username");
        ChatList_lv = (ListView) findViewById(R.id.chatlist_lv);
        Username_tv = (TextView) findViewById(R.id.username_search_tv);
        Username_tv.setText(username);

        if (serverSide.matches("OK")) { backend.updateNewslist(msgsSQLHandler, usersSQLHandler, me_userid); }
        //if (serverSide.matches("OK")) { Log.i("TESTING", userid); }
        showMessages(other_userid);

    }

    @Override
    protected void onResume() {
        super.onResume();

        serverSide = serverCheck(mContext);
        showMessages(other_userid);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_channel, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_userdetails:
                return true;
            case R.id.action_clear_channel:
                clearChannel();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /* Additional Actions' Methods */

    private void showMessages(String user_other) {

        ArrayList<Data_ChannelItems> chatList = new ArrayList<>();
        chatList.clear();

        String query = "SELECT CASE WHEN userid_from ='" + user_other + "' THEN 'FROM' ELSE 'TO' END AS to_or_from, id, message, timestamp, status FROM MSGS WHERE userid_from ='" + user_other + "' OR userid_to ='" + user_other + "' ORDER BY timestamp;";
        msgsSQLHandler = new DB_Msgs_Handler(this);
        Cursor c1 = msgsSQLHandler.selectQuery(query);
        if (c1 != null && c1.getCount() > 0) {
            if (c1.moveToFirst()) {
                do {
                    Data_ChannelItems channelItems = new Data_ChannelItems();

                    channelItems.setMsg(c1.getString(c1.getColumnIndex("message")));
                    channelItems.setToOrFromMe(c1.getString(c1.getColumnIndex("to_or_from")));
                    channelItems.setStatus(c1.getString(c1.getColumnIndex("status")));

                    String timestamp_raw = c1.getString(c1.getColumnIndex("timestamp"));
                    String timestamp = Tool_Timestamp.getBeauty(mContext, Integer.parseInt(timestamp_raw));
                    channelItems.setTimestamp(timestamp);

                    chatList.add(channelItems);

                } while (c1.moveToNext());
            }
        }
        c1.close();
        Ctrl_ChannelListAdapter channelAdapter = new Ctrl_ChannelListAdapter(
                Act_Channel.this, chatList);
        ChatList_lv.setAdapter(channelAdapter);
        ChatList_lv.setSelection(ChatList_lv.getAdapter().getCount()-1);

        String update_query = "UPDATE MSGS SET status='read' WHERE (userid_from ='" + user_other + "' OR userid_to ='" + user_other + "') ";
        msgsSQLHandler.executeQuery(update_query);

    }

    public void sendMessage(View view) {
        Context mContext = getApplicationContext();
        EditText message_et = (EditText) findViewById(R.id.message_et);
        String message = message_et.getText().toString();
        if (!message.matches("") ) {
            //TODO: We avoid sending millis here, but might be better to do so.
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

            //ERROR HERE SQLite
            String newUserName = toolbox_SP.getUsername(mContext,other_userid);

            msgsSQLHandler.addNewMessage(me_userid, other_userid, message, timestamp);
            serverSide = serverCheck(mContext);
            if (serverSide.matches("OK")) {
                syncMessages();
            }
        }
        message_et.setText("");

        showMessages(other_userid);


    }

    public void clearChannel() {
        String clearQuery = "DELETE FROM MSGS where (userid_from='" + other_userid + "' OR userid_to='" + other_userid + "')";
        msgsSQLHandler.executeQuery(clearQuery);
        showMessages(other_userid);
    }

    public void updateChannel(View view) {
        showMessages(other_userid);
    }

    public void syncMessages(){

        Cursor c1 = msgsSQLHandler.getMsgsWStatus("created OR sending");
        if (c1 != null && c1.getCount() > 0) {
            if (c1.moveToFirst()) {
                do {
                    String item_id = c1.getString(c1.getColumnIndex("id"));
                    String itemMessage = c1.getString(c1.getColumnIndex("message"));
                    String item_userid_from = c1.getString(c1.getColumnIndex("userid_from"));
                    String item_userid_to = c1.getString(c1.getColumnIndex("userid_to"));
                    String item_timestamp = c1.getString(c1.getColumnIndex("timestamp"));
                    String sendResult = backend.sendMsgToBackend(itemMessage,item_userid_from,item_userid_to,item_timestamp);
                    String updateQuery = "UPDATE MSGS SET status='sending' WHERE id='" + item_id + "';";
                    msgsSQLHandler.executeQuery(updateQuery);
                    if (sendResult.contains("received")){
                        String remote_id = sendResult.replace("received ","");
                        updateQuery = "UPDATE MSGS SET status='sent' WHERE id='" + item_id + "';";
                        msgsSQLHandler.executeQuery(updateQuery);
                    }
                } while (c1.moveToNext());
            }
        }
        c1.close();
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
}
