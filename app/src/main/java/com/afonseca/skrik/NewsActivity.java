package com.afonseca.skrik;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

/**
 * Created by afonseca on 1/24/2015.
 */
public class NewsActivity extends ActionBarActivity {


    Control_Userconfig controlUserconfig = new Control_Userconfig();
    Control_BackendHandler backend = new Control_BackendHandler();
    TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        username = (TextView) findViewById(R.id.username_tv);

        Context context = getApplicationContext();
        String output = controlUserconfig.getUsername(context);

        username.setText(output + " News");
    }

    @Override
    protected void onResume() {
        super.onResume();

        username = (TextView) findViewById(R.id.username_tv);

        Context context = getApplicationContext();
        String output = controlUserconfig.getUsername(context);

        username.setText(output + " news");
    }

    /** Called when the user clicks the Config button */
    public void gotoUserConfig(View view) {
        Intent intent = new Intent(this, UserConfigActivity.class);
        startActivity(intent);
    }
}
