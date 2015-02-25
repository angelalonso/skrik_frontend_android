package com.afonseca.skrik;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
    Ctrl_Backend backend = new Ctrl_Backend();

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
        Context context = getApplicationContext();
        serverSide = serverCheck(context);

        Bundle b = getIntent().getExtras();
        other_userid = b.getString("userid_other");
        me_userid = b.getString("userid_me");
        username = b.getString("username");
        ChatList_lv = (ListView) findViewById(R.id.chatlist_lv);
        Username_tv = (TextView) findViewById(R.id.username_search_tv);
        Username_tv.setText(username);

        if (serverSide.matches("OK")) { backend.updateNewslist(msgsSQLHandler, me_userid); }
        //if (serverSide.matches("OK")) { Log.i("TESTING", userid); }
        showMessages(other_userid);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Context mContext = getApplicationContext();
        serverSide = serverCheck(mContext);

        if (serverSide.matches("OK")) { Log.i("TESTING", me_userid); }

        showMessages(other_userid);
    }

    /* Additional Actions' Methods */

    private void showMessages(String user_other) {
        //Context mContext = getApplicationContext();


        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss dd/MM/yy");

        ArrayList<Data_ChannelItems> chatList = new ArrayList<>();
        chatList.clear();

        String query = "SELECT CASE WHEN userid_from ='" + user_other + "' THEN 'FROM' ELSE 'TO' END AS to_or_from, id, message, timestamp, status FROM MSGS WHERE userid_from ='" + user_other + "' OR userid_to ='" + user_other + "' ORDER BY timestamp;";

        Cursor c1 = msgsSQLHandler.selectQuery(query);
        Log.i("TESTING - Looking for the other user COUNT->", String.valueOf(c1.getCount()));
        if (c1 != null && c1.getCount() > 0) {
            if (c1.moveToFirst()) {
                do {
                    Data_ChannelItems channelItems = new Data_ChannelItems();

                    channelItems.setMsg(c1.getString(c1.getColumnIndex("message")));
                    channelItems.setToOrFromMe(c1.getString(c1.getColumnIndex("to_or_from")));

                    String timestamp_raw = c1.getString(c1.getColumnIndex("timestamp"));
                    String timestamp = fmt.format(new Time(Long.parseLong(timestamp_raw + "000")));

                    channelItems.setTimestamp(timestamp);

                    chatList.add(channelItems);

                } while (c1.moveToNext());
            }
        }

        Ctrl_ChannelListAdapter channelAdapter = new Ctrl_ChannelListAdapter(
                Act_Channel.this, chatList);
        ChatList_lv.setAdapter(channelAdapter);

    }

    public void sendMessage(View view) {
        Context mContext = getApplicationContext();
        EditText message_et = (EditText) findViewById(R.id.message_et);
        String message = message_et.getText().toString();
        //TODO: We avoid sending millis here, but might be better to do so.
        String timestamp = String.valueOf(System.currentTimeMillis()/1000);

        // DEPRECATED: String Insertquery = "INSERT INTO MSGS (userid_from, userid_to, message, status, timestamp, backend_id) VALUES('" + me_userid + "','" + other_userid + "','" + message + "','created','" + timestamp + "','')";
        //DEPRECATED: long messageID = msgsSQLHandler.executeInsertgetID(me_userid,other_userid,message,"created",timestamp,"none");
        msgsSQLHandler.addNewMessage(me_userid,other_userid,message,timestamp);
        serverSide = serverCheck(mContext);
        if (serverSide.matches("OK")) {
            syncMessages();
        }
        //TODO: Check if there is network first,
        //TODO: If trying to send, then mark as sending locally
        String sendResult = backend.sendMessageToBackend(message,me_userid,other_userid,timestamp);
        message_et.setText("");
        showMessages(other_userid);
    }

    public void clearChannel(View view) {
        String Clearquery = "DELETE FROM MSGS where id = id;";
        msgsSQLHandler.executeQuery(Clearquery);
    }

    public void updateChannel(View view) {

    }

    public void syncMessages(){

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
