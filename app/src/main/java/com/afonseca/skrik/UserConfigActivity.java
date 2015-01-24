package com.afonseca.skrik;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by aaf on 1/23/15.
 */
public class UserConfigActivity extends ActionBarActivity {

    TextView name;
    TextView email;
    TextView uid;
    TextView regid;
    TextView passwd;

    Control_BackendHandler backend = new Control_BackendHandler();
    Control_Userconfig controlUserconfig = new Control_Userconfig();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userconfig);

        name = (TextView) findViewById(R.id.name_input);
        email = (TextView) findViewById(R.id.email_input);
        uid = (TextView) findViewById(R.id.uid_input);
        regid = (TextView) findViewById(R.id.regid_input);
        passwd = (TextView) findViewById(R.id.passwd_input);


        Context context = getApplicationContext();

        String current_uid = controlUserconfig.getUid(context);
        if (current_uid == ""){
            current_uid = backend.getnewID();
        }

        name.setText(controlUserconfig.getUsername(context));
        email.setText(controlUserconfig.getEmail(context));
        uid.setText(current_uid);
        regid.setText(controlUserconfig.getRegid(context));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);
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

    /** Called when the user clicks the Save user button */
    public void saveUser(View view) {
        String n  = name.getText().toString();
        String em  = email.getText().toString();
        String id  = uid.getText().toString();
        String rid  = regid.getText().toString();
        String pwd  = passwd.getText().toString();

        Context context = getApplicationContext();
        Log.i(" TESTING REGID 1", rid);
        controlUserconfig.saveUser(context, n, em, id, rid, pwd);
        String saveResult = controlUserconfig.userOK(context);
        if (saveResult == "OK") {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), saveResult, Toast.LENGTH_LONG).show();
        }
    }
    public void clearUser(View view) {

        Context context = getApplicationContext();
        controlUserconfig.clearUser(context);

        name.setText("");
        email.setText("");
        uid.setText(backend.getnewID());
        regid.setText("");
        passwd.setText("");


    }
}

