package com.afonseca.skrik;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

/**
 * Created by afonseca on 2/3/2015.
 */
public class ChatActivity extends ActionBarActivity {

    TextView Username_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bundle b = getIntent().getExtras();
        String userid = b.getString("userid");
        Username_tv = (TextView) findViewById(R.id.username_tv);
        Username_tv.setText(userid);
    }
}
