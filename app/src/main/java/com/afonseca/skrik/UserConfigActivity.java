package com.afonseca.skrik;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/** LOGIC
 * - If back button is pressed
 *   - Show Toast, ask for a second click to exit
 *   - If another one happens
 *     - Send app to background
 *   - Otherwise, nothing happens
 * - :If CLEAR button is pressed
 *   - :Ask for confirmation:
 *     - :If confirmed, clear Textviews AND Shared preferences
 *     - :If not confirmed, go back
 * - TODO:If SAVE button is pressed
 *   - TODO:If Network is OK
 *     - TODO:If UID or REGID is empty
 *       - TODO:Give Data to Server, ask for UID and REGID
 *         - TODO:(serverside) if the email exists, existing UID is given back. TODO: use a password/email auth/whatever here. *
 *     - TODO:If data was marked as NOT synchronized
 *       Send
 *
 *     - TODO:If data was marked as synchronized
 *   - TODO:If Network is NOT OK
 *     - TODO:If UID or REGID is empty
 *       - TODO:Build temporary values (leave it empty, maybe?)
 *     - TODO:Mark data as NOT synchronized
 *     - TODO:Save into sharedpreferences
 *
 */
public class UserConfigActivity extends ActionBarActivity {

    /* Declarations */

    Control_BackendHandler backend = new Control_BackendHandler();
    Control_Userconfig controlUserconfig = new Control_Userconfig();

    String serverSide;

    private Toast toast;
    private long lastBackPressTime = 0;

    TextView name;
    TextView email;
    TextView uid;
    TextView regid;
    TextView passwd;

    /* General Behaviour Methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userconfig);

        name = (TextView) findViewById(R.id.name_input);
        email = (TextView) findViewById(R.id.email_input);
        uid = (TextView) findViewById(R.id.uid_input);
        regid = (TextView) findViewById(R.id.regid_input);
        passwd = (TextView) findViewById(R.id.passwd_input);

        Context mContext = getApplicationContext();
        serverSide = serverCheck(mContext);

        //Starting the update process...TODO: PASS serverSide to A FUNCTION AND DO EVERYTHING THERE WHEN POSSIBLE!! (Also good for onResume)
        // TODO: BETTER YET: FOLLOW THE LOGIC ABOVE!!

        String current_uid = controlUserconfig.getUid(mContext);
        if (serverSide.matches("OK")) {
            if (current_uid == ""){
                current_uid = backend.getnewID();
                uid.setText(current_uid);
            }
        } else {
            Log.i("TESTING - NETWORK CHECK -- ", "the News List has not been updated, The server is not there");
        }
/*

*/
        name.setText(controlUserconfig.getUsername(mContext));
        email.setText(controlUserconfig.getEmail(mContext));
        //
        regid.setText(controlUserconfig.getRegid(mContext));

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

    /* General Behaviour Methods */

    @Override
    public void onBackPressed() {
        if (this.lastBackPressTime < System.currentTimeMillis() - 3000) {
            toast = Toast.makeText(this, "Press back again to close this app", Toast.LENGTH_SHORT);
            toast.show();
            this.lastBackPressTime = System.currentTimeMillis();
        } else {
            if (toast != null) {
                toast.cancel();
            }
            super.onBackPressed();
            moveTaskToBack(true);
        }
    }

    /* Additional Actions' Methods */

    public void saveUser(View view) {
        /** Called when the user clicks the Save user button */
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to DELETE your USER DATA?")
                .setCancelable(false)
                .setPositiveButton("YES, do it!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Context mContext = getApplicationContext();
                        name.setText("");
                        email.setText("");
                        uid.setText("");
                        regid.setText("");
                        passwd.setText("");
                        controlUserconfig.clearUser(mContext);
                    }
                })
                .setNegativeButton("Ups, NO!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

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

