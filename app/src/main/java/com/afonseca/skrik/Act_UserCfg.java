package com.afonseca.skrik;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/* TODO:
- If user landed here -> Show why (which details are wrong/needed) MESSAGE ÁREA
- If Activity is opened/resumed -> just load the current data
- If user saves data ->...
  - ...if data is correct -> Save to DB, send to server and go to Act_Overview
  - ...if data is not correct -> Correct if possible, if not -> Show why (see above) and let user change it
- If sending data to server -> Encrypt all, then send (decrypt at server)
- If the UID is missing/wrong -> get UID from server
  - If server is unavailable -> use temporary 4444
- If the REGID is missing/wrong/older than 1 day -> get Regid from GCM
  - If GCM returns a new REGID -> update in server
  - If GCM DOES NOT return a REGID -> ...
    - ...if current REGID is correct or temporary, keep it
    - ...if current REGID is empty, get a temporary one
- If the username exists at the server -> show error, get alternatives from server
- TODO: DEFINE WHAT TO DO WITH PASSWORDS (when the user is known, when it's not, when it's new, when it's recovering an existing one...)


 */

/*TODO: Define a better workflow:
  TODO: Show what is needed turning it red (back to grey when writing)
  TODO: Save password in backend (send all secured)
  TODO: Add an Image, send to server
  TODO: Document which Classes are called from here
  TODO: Try to communicate securely
 */

public class Act_UserCfg extends ActionBarActivity {

    /* Declarations */

    Toolbox_Backend toolbox_BE = new Toolbox_Backend();
    Toolbox_Sharedprefs toolbox_SP = new Toolbox_Sharedprefs();

    Context mContext;

    String TAG = "Checkpoint! ->";

    String serverSide;
    private Toast toast;
    private long lastBackPressTime = 0;

    EditText name;
    EditText email;
    EditText phone;
    EditText passwd;
    TextView uid;
    TextView regid;

    /* LOADING Methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userconfig);
        Log.i(TAG,"onCreate");

        name = (EditText) findViewById(R.id.name_input);
        email = (EditText) findViewById(R.id.email_input);
        phone = (EditText) findViewById(R.id.phone_input);
        passwd = (EditText) findViewById(R.id.passwd_input);
        uid = (TextView) findViewById(R.id.uid_input);
        regid = (TextView) findViewById(R.id.regid_input);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mContext = getApplicationContext();
        Log.i(TAG,"onResume");

        serverSide = serverCheck();
        loadUserData(mContext);
    }

    /* onSOMETHINGELSE Methods */

    @Override
    public void onBackPressed() {
        Log.i(TAG,"onBackPressed");

        mContext = getApplicationContext();
        if (this.lastBackPressTime < System.currentTimeMillis() - 3000) {
            toast = Toast.makeText(this, mContext.getResources().getString(R.string.msg_press_exit), Toast.LENGTH_SHORT);
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

    /* ACTION Methods */


    public void loadUserData(Context mContext){
        Log.i(TAG,"loadUserData");

        String LinkedAccount = toolbox_SP.userHasLinkedAccount(mContext);

        if (LinkedAccount.equals("None")){
            offerAccount(mContext);
        }

        name.setText(toolbox_SP.getUsername(mContext));
        email.setText(toolbox_SP.getEmail(mContext));
        phone.setText(toolbox_SP.getPhone(mContext));
        uid.setText(toolbox_SP.getUid(mContext));
        regid.setText(toolbox_SP.getRegid(mContext));
    }

    public void offerAccount(Context mContext) {
        Log.i(TAG,"offerAccount");

        final Context inContext = mContext;
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
            foundAccounts.add(mContext.getResources().getString(R.string.btn_usercfg_entermanual));
            final CharSequence[] allAccounts = foundAccounts.toArray(new
                    CharSequence[foundAccounts.size()]);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(mContext.getResources().getString(R.string.msg_link_account));
            builder.setItems(allAccounts, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Pattern emailPattern = Patterns.EMAIL_ADDRESS;
                    Pattern phonePattern = Patterns.PHONE;
                    String Chosen = allAccounts[which].toString();
                    if (emailPattern.matcher(Chosen).matches()) {
                        email.setEnabled(true);
                        email.setText(Chosen);
                        toolbox_SP.setEmail(inContext,Chosen);
                        toolbox_SP.setPhone(inContext,"");
                        phone.setText(" ");
                        phone.setEnabled(false);
                    } else if (phonePattern.matcher(Chosen).matches())
                    {
                        phone.setEnabled(true);
                        phone.setText(Chosen);
                        toolbox_SP.setPhone(inContext,Chosen);
                        toolbox_SP.setEmail(inContext,"");
                        email.setText(" ");
                        email.setEnabled(false);
                    }
                    else {
                        email.setEnabled(true);
                        phone.setEnabled(true);
                        email.setText("");
                        phone.setText("");
                        toolbox_SP.setEmail(inContext,"");
                        toolbox_SP.setPhone(inContext,"");
                    }
                }
            });
            builder.show();
            toolbox_SP.setPhone(mContext, phone.getText().toString());
            toolbox_SP.setEmail(mContext, email.getText().toString());
        }
    }

    public void clearUser(View view) {
        Log.i(TAG,"clearUser");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(mContext.getResources().getString(R.string.msg_del_confirm))
                .setCancelable(false)
                .setPositiveButton(mContext.getResources().getString(R.string.msg_del_conf_y), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mContext = getApplicationContext();
                        DB_Msgs_Handler msgsSQLHandler = new DB_Msgs_Handler(mContext);
                        String ClearMsgsquery = "DELETE FROM MSGS WHERE id=id;";
                        msgsSQLHandler.executeQuery(ClearMsgsquery);
                        DB_Users_Handler usersSQLHandler = new DB_Users_Handler(mContext);
                        String Clearusersquery = "DELETE FROM USERS WHERE id=id;";
                        usersSQLHandler.executeQuery(Clearusersquery);
                        name.setText("");
                        email.setText("");
                        phone.setText("");
                        uid.setText("");
                        passwd.setText("");
                        toolbox_SP.clearUser(mContext);
                        String saveResult = toolbox_SP.saveUserConfig(mContext, "", "", "", "", "", "");
                        loadUserData(mContext);
                    }
                })
                .setNegativeButton(mContext.getResources().getString(R.string.msg_del_conf_n), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void saveUser(View view) {
        Log.i(TAG,"saveUser");

        String n  = name.getText().toString();
        String em  = email.getText().toString();
        String ph = phone.getText().toString();
        String id  = uid.getText().toString();
        String rid  = regid.getText().toString();
        String pwd  = passwd.getText().toString();

        mContext = getApplicationContext();
        //TODO: Shall we trigger this already while the user is entering data?
        //    TODO: Maybe we can create a new function thtat does that, then triggers an update when it's done.
        // UNDER CONSTRUCTION BELOW (regidCheck)
        regidCheck();
        String dataCheck = toolbox_SP.userOK_beforeSave(mContext, n, em, ph, pwd);
        if (dataCheck.equals("OK")) {
            String saveResult = toolbox_SP.saveUserConfig(mContext, n, em, ph, id, rid, pwd);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), dataCheck, Toast.LENGTH_LONG).show();
        }

    }

    /* CHECK Methods */

    // TODO: MOVE THIS TO toolbox_SP
    public String regidCheck() {
        // TODO: Send and check result of AsyncTask
        // TODO: Update regid directly at the Asynctask?
        /*
        Check if regid is empty or invalid or older than one day.(check if there is a previous timestamp)
        If so, ask for a new one
        If a result comes AND is a valid one, change it and update timestamp
        If it's not valid AND current one is empty, give a temporary one and
         */
        String regid_old = toolbox_SP.getRegid(mContext);
        long regidts_old = toolbox_SP.getRegidTimestamp(mContext);
        long ts_new = System.currentTimeMillis();
        // IF regid is empty or temporary
        if (!toolbox_SP.phoneHasRegid(mContext)) {
            //TODO: get result, check it only update if its correct
            Tool_GCM_AsyncTask task = new Tool_GCM_AsyncTask(this);
            task.execute();
            toolbox_SP.setRegidTimestamp(mContext,ts_new);
        // IF Timestamp of regid is older than a day
        } else if (ts_new - regidts_old > 3600000) {
            //TODO: get result, check it only update if its correct
            Tool_GCM_AsyncTask task = new Tool_GCM_AsyncTask(this);
            task.execute();
            toolbox_SP.setRegidTimestamp(mContext,ts_new);
        }
        String regid_new = toolbox_SP.getRegid(mContext);

        return regid_new;
    }

    public String serverCheck() {
        Log.i(TAG,"serverCheck");

        TextView server_tv = (TextView) findViewById(R.id.server_tv);
        String status = toolbox_BE.testNetwork(mContext);
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



    /* "GOTO" Methods */

}