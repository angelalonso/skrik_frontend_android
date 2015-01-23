package com.afonseca.skrik;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * Created by aaf on 1/23/15.
 */
public class UserConfigActivity extends ActionBarActivity {

    TextView name;
    TextView email;
    TextView uid;
    TextView regid;
    TextView passwd;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";
    public static final String Email = "emailKey";
    public static final String Uid = "uidKey";
    public static final String Regid = "regidKey";
    public static final String Passwd = "PasswdKey";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userconfig);

        name = (TextView) findViewById(R.id.name_input);
        email = (TextView) findViewById(R.id.email_input);
        uid = (TextView) findViewById(R.id.uid_input);
        regid = (TextView) findViewById(R.id.regid_input);
        passwd = (TextView) findViewById(R.id.passwd_input);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if (sharedpreferences.contains(Name))
        {
            name.setText(sharedpreferences.getString(Name, ""));
        }
        if (sharedpreferences.contains(Email))
        {
            email.setText(sharedpreferences.getString(Email, ""));
        }
        if (sharedpreferences.contains(Uid))
        {
            uid.setText(sharedpreferences.getString(Uid, ""));
        }
        if (sharedpreferences.contains(Regid))
        {
            regid.setText(sharedpreferences.getString(Regid, ""));
        }
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

        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(Name, n);
        editor.putString(Email, em);
        editor.putString(Uid, id);
        editor.putString(Regid, rid);
        editor.putString(Passwd, pwd);

        editor.commit();

        finish();
    }
    public void clearUser(View view) {

        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.remove(Name);
        editor.remove(Email);
        editor.remove(Uid);
        editor.remove(Regid);
        editor.remove(Passwd);
        editor.commit();

        name.setText("");
        email.setText("");
        uid.setText("");
        regid.setText("");
        passwd.setText("");
    }
}

