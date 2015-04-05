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

public class Act_UserCfg extends ActionBarActivity {

    /* Declarations */

    Toolbox_Backend toolbox_BE = new Toolbox_Backend();
    Toolbox_Sharedprefs toolbox_SP = new Toolbox_Sharedprefs();
    Tool_Timestamp my_Timestamp = new Tool_Timestamp();

    Context mContext;

    String serverSide;
    private Toast toast;
    private long lastBackPressTime = 0;

    EditText name;
    EditText email;
    EditText phone;
    EditText passwd;
    TextView uid;
    TextView regid;
    TextView regid_ts;

    String TAG = "Checkpoint! ->";

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
        regid_ts = (TextView) findViewById(R.id.regid_timestamp);
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
        regid_ts.setText(my_Timestamp.getBeauty(mContext,(int)(toolbox_SP.getRegidTimestamp(mContext)/1000)));
        String regid_new = toolbox_SP.correctRegid(mContext,this);
        if (!regid_new .matches("")) {
            regid.setText(regid_new);
        }
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
                        passwd.setText("");
                        uid.setText("");
                        regid.setText("");
                        regid_ts.setText("");
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
        String pwd  = passwd.getText().toString();

        mContext = getApplicationContext();
        toolbox_SP.correctRegid(mContext,this);
        // OTHERWISE we might get a new regid but keep on passing the old one
        String rid = toolbox_SP.getRegid(mContext);
        String dataCheck = toolbox_SP.userOK_beforeSave(mContext, n, em, ph, pwd);
        if (dataCheck.equals("OK")) {
            String saveResult = toolbox_SP.saveUserConfig(mContext, n, em, ph, id, rid, pwd);
            finish();
        } else {
            //We use this as an update
            loadUserData(mContext);
            Toast.makeText(getApplicationContext(), dataCheck, Toast.LENGTH_LONG).show();
        }

    }

    /* CHECK Methods */
/*
    // TODO: MOVE THIS TO toolbox_SP
    public String regidddCheck() {
        // TODO: Send and check result of AsyncTask
        // TODO: Update regid directly at the Asynctask?
        //
        //Check if regid is empty or invalid or older than one day.(check if there is a previous timestamp)
        //If so, ask for a new one
        //If a result comes AND is a valid one, change it and update timestamp
        //If it's not valid AND current one is empty, give a temporary one and

        String regid_old = toolbox_SP.getRegid(mContext);
        long regidts_old = toolbox_SP.getRegidTimestamp(mContext);
        long ts_new = System.currentTimeMillis();
        // IF regid is empty or temporary
        if (!toolbox_SP.acceptableRegid(mContext)) {
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
*/
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