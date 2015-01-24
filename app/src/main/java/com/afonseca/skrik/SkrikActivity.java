package com.afonseca.skrik;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/*

This checks if we already have a user configured.
YES -> it passes the ball to "NEWS"
NO  -> it passes the ball to "USER CONFIG"

 */

public class SkrikActivity extends ActionBarActivity {
/*
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    public static final String Name = "nameKey";
    public static final String Email = "emailKey";
*/
    Control_Userconfig controlUserconfig = new Control_Userconfig();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skrik);
        Context context = getApplicationContext();
        if (controlUserconfig.userOK(context) == "OK"){
            Intent intent = new Intent(this, NewsActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, UserConfigActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_skrik);
        Context context = getApplicationContext();

        if (controlUserconfig.userOK(context) == "OK"){
            Intent intent = new Intent(this, NewsActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, UserConfigActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_skrik, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /** Called when the user clicks the Config button */
    public void gotoUserConfig(View view) {
        Intent intent = new Intent(this, UserConfigActivity.class);
        startActivity(intent);
    }
}
