package com.afonseca.skrik;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class Act_UserCfg extends ActionBarActivity {

    /* Declarations */

    Ctrl_Backend backend = new Ctrl_Backend();
    Funcs_UserCfg funcsUserCfg = new Funcs_UserCfg();

    String serverSide;

    private Toast toast;
    private long lastBackPressTime = 0;

    TextView name;
    TextView email;
    TextView phone;
    TextView uid;
    TextView regid;
    TextView passwd;

    /* General Behaviour Methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    /* Logic:
      - Initialize the textviews,
      - Add the data from Shared preferences
     */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userconfig);

        name = (TextView) findViewById(R.id.name_input);
        email = (TextView) findViewById(R.id.email_input);
        phone = (TextView) findViewById(R.id.phone_input);
        uid = (TextView) findViewById(R.id.uid_input);
        regid = (TextView) findViewById(R.id.regid_input);
        passwd = (TextView) findViewById(R.id.passwd_input);

        Context mContext = getApplicationContext();
        serverSide = serverCheck(mContext);

        name.setText(funcsUserCfg.getUsername(mContext));
        email.setText(funcsUserCfg.getEmail(mContext));
        phone.setText(funcsUserCfg.getPhone(mContext));
        uid.setText(funcsUserCfg.getUid(mContext));
        regid.setText(funcsUserCfg.getRegid(mContext));

        if (email.getText() == ""  && phone.getText() == "" ){
            newUser();
        }
    }


    @Override
    protected void onResume() {
    /* Logic:
      - Add the data from Shared preferences
     */
        super.onResume();

        Context mContext = getApplicationContext();
        serverSide = serverCheck(mContext);

        name.setText(funcsUserCfg.getUsername(mContext));
        email.setText(funcsUserCfg.getEmail(mContext));
        phone.setText(funcsUserCfg.getPhone(mContext));
        uid.setText(funcsUserCfg.getUid(mContext));
        regid.setText(funcsUserCfg.getRegid(mContext));

        if (email.getText() == ""  && phone.getText() == "" ){
            newUser();
        }
    }

    /* General Behaviour Methods */

    @Override
    public void onBackPressed() {
    /* Logic:
      - If back button is pressed
        - Show Toast, ask for a second click to exit
        - If another one happens
          - Send app to background
        - Otherwise, nothing happens
    */
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
    /* Logic:
     - If SAVE button is pressed
       - If data SHOWN (not stored! ) is OK:
         - Send to Control User to Save in Sharedprefs and Backend
         - TODO:Check answer
         - TODO: If user clicks on phone, email is deleted and vice-versa
         - Go back to Main
       - If data SHOWN (not stored! ) is NOT OK:
         - Show what is missing, go back
    */
        String n  = name.getText().toString();
        String em  = email.getText().toString();
        String ph = phone.getText().toString();
        String id  = uid.getText().toString();
        String rid  = regid.getText().toString();
        String pwd  = passwd.getText().toString();

        Context mContext = getApplicationContext();

        //TODO: If there is only one:
        //        - Build list
        //        - ask for confirmation
        //TODO: If there are more:
        //        - check they are unique - DONE
        //        - Ask user for confirmation on which to use (if any)

        String dataCheck = funcsUserCfg.userOK_Input(n, em, ph, id, rid, pwd);
        if (dataCheck == "OK") {
            String saveResult = funcsUserCfg.saveUserConfig(mContext, n, em, ph, id, rid, pwd);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), dataCheck, Toast.LENGTH_LONG).show();
        }
    }

    public void clearUser(View view) {
    /* Logic:
     - If CLEAR button is pressed
       - Ask for confirmation:
         - If confirmed, clear Textviews AND Shared preferences
         - If not confirmed, go back
    */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to DELETE your USER DATA?")
                .setCancelable(false)
                .setPositiveButton("YES, do it!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Context mContext = getApplicationContext();
                        name.setText("");
                        email.setText("");
                        phone.setText("");
                        uid.setText("");
                        regid.setText("");
                        passwd.setText("");
                        funcsUserCfg.clearUser(mContext);
                        newUser();
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

    public void newUser() {

        Context mContext = getApplicationContext();
        List<String> foundAccounts = new ArrayList<>();
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Pattern phonePattern = Patterns.PHONE;
        Account[] accounts = AccountManager.get(mContext).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                if (!foundAccounts.contains( possibleEmail )) {
                    foundAccounts.add(possibleEmail);
                }
            }
            if (phonePattern.matcher(account.name).matches()) {
                String possiblePhone = account.name;
                if (!foundAccounts.contains( possiblePhone )) {
                    foundAccounts.add(possiblePhone);
                }
            }
        }
        if (foundAccounts.size() > 0){
            foundAccounts.add("Enter manually");
            final CharSequence[] allAccounts = foundAccounts.toArray(new
                    CharSequence[foundAccounts.size()]);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("What do you want to link your account to?");
            builder.setItems(allAccounts, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Pattern emailPattern = Patterns.EMAIL_ADDRESS;
                    Pattern phonePattern = Patterns.PHONE;
                    String Chosen = allAccounts[which].toString();
                    if (emailPattern.matcher(Chosen).matches()) {
                        email.setEnabled(true);
                        email.setText(Chosen);
                        phone.setText(" ");
                        phone.setEnabled(false);
                    } else if (phonePattern.matcher(Chosen).matches())
                    {
                        phone.setEnabled(true);
                        phone.setText(Chosen);
                        email.setText(" ");
                        email.setEnabled(false);
                    }
                    else {
                        email.setEnabled(true);
                        phone.setEnabled(true);
                        email.setText("");
                        phone.setText("");
                    }

                }
            });
            builder.show();
        }
    }



    /* Check Functions */

    public String serverCheck(Context mContext) {
    /* Logic:
      - Pick the "Status Point" textview
      - Depending on status, change its color
      - Return status, for other functions to know it too.
     */
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