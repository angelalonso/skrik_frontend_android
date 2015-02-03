package com.afonseca.skrik;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;

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

    }

    @Override
    protected void onResume() {
        super.onResume();

        Context context = getApplicationContext();
        serverSide = serverCheck(context);

        if (serverSide.matches("OK")) { Log.i("TESTING", userid); }
    }

    /* Additional Actions' Methods */

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
