package com.afonseca.skrik;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by afonseca on 2/3/2015.
 */
public class ChatActivity extends ActionBarActivity {

    /* Declarations */

    Control_NewsDbHandler newsSQLHandler;
    Control_BackendHandler backend = new Control_BackendHandler();

    String serverSide;
    String userid;
    String other_userid;
    String username;

    TextView Username_tv;

    /* General Behaviour Methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        newsSQLHandler = new Control_NewsDbHandler(this);
        Context context = getApplicationContext();
        serverSide = serverCheck(context);

        Bundle b = getIntent().getExtras();
        other_userid = b.getString("userid");
        userid = "";
        username = b.getString("username");
        Username_tv = (TextView) findViewById(R.id.username_tv);
        Username_tv.setText(username);

        if (serverSide.matches("OK")) { backend.updateNewslist(newsSQLHandler, userid); }
        //if (serverSide.matches("OK")) { Log.i("TESTING", userid); }
        showMessages(other_userid);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Context context = getApplicationContext();
        serverSide = serverCheck(context);

        if (serverSide.matches("OK")) { Log.i("TESTING", userid); }

        showMessages(other_userid);
    }

    /* Additional Actions' Methods */

    private void showMessages(String user_other) {
        Context mContext = getApplicationContext();

        ArrayList<Data_ChatListItems> chatList = new ArrayList<>();
        chatList.clear();

        //String query = "SELECT * FROM PHONE_CONTACTS ";
        String query = "SELECT CASE WHEN userid_from =30660416779715 THEN 'FROM' ELSE 'TO' END as to_or_from, id, message, timestamp, status from msging where userid_from ='" + user_other + "' or userid_to ='" + user_other + "' order by timestamp;";

        Cursor c1 = newsSQLHandler.selectQuery(query);
        if (c1 != null && c1.getCount() > 0) {
            if (c1.moveToFirst()) {
                do {
                    Data_ChatListItems chatListItems = new Data_ChatListItems();

                    chatListItems.setMsg(c1.getString(c1
                            .getColumnIndex("message")));
                    chatListItems.setToOrFromMe(c1.getString(c1
                            .getColumnIndex("to_or_from")));
                    chatListItems.setTimestamp(c1.getString(c1
                            .getColumnIndex("timestamp")));

                    chatList.add(chatListItems);

                } while (c1.moveToNext());
            }
        }
//TODO: Continue here
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
}
