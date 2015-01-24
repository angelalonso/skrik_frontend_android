package com.afonseca.skrik;

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

    Control_BackendHandler backend = new Control_BackendHandler();
    TextView test_output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        String url = "http://192.168.10.229:8000/getnewid/";
        String result = "nothing";

        test_output = (TextView) findViewById(R.id.test_tv);
        //Control_StringAsyncTask task = new Control_StringAsyncTask();
        //result = task.execute(new String[] { url }).get();
        String output = null;
        output = backend.getnewID();
        //String new_output = "Gili";
        test_output.setText(output);
    }

    /** Called when the user clicks the Config button */
    public void gotoUserConfig(View view) {
        Intent intent = new Intent(this, UserConfigActivity.class);
        startActivity(intent);
    }
}
