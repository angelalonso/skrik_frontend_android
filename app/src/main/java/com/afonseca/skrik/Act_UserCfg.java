package com.afonseca.skrik;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Act_UserCfg extends ActionBarActivity {

    /* Declarations */

    Toolbox_Backend backend = new Toolbox_Backend();
    Toolbox_Sharedprefs toolbox_SP = new Toolbox_Sharedprefs();

    Context mContext;

    String serverSide;
    private Toast toast;
    private long lastBackPressTime = 0;

    TextView name;
    TextView email;
    TextView phone;
    TextView uid;
    TextView regid;
    TextView passwd;

    /* LOADING Methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userconfig);
        mContext = getApplicationContext();

        name = (TextView) findViewById(R.id.name_input);
        email = (TextView) findViewById(R.id.email_input);
        phone = (TextView) findViewById(R.id.phone_input);
        uid = (TextView) findViewById(R.id.uid_input);
        regid = (TextView) findViewById(R.id.regid_input);
        passwd = (TextView) findViewById(R.id.passwd_input);

        serverSide = serverCheck();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mContext = getApplicationContext();

        serverSide = serverCheck();
        loadUserData(mContext);
    }

    /* onSOMETHING Methods */

    @Override
    public void onBackPressed() {
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
                        regid.setText("");
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
        String n  = name.getText().toString();
        String em  = email.getText().toString();
        String ph = phone.getText().toString();
        String id  = uid.getText().toString();
        String rid  = regid.getText().toString();
        String pwd  = passwd.getText().toString();

        mContext = getApplicationContext();
        String dataCheck = toolbox_SP.userOK_beforeSave(mContext, n, em, ph, id, rid, pwd);
        if (dataCheck.equals("OK")) {
            String saveResult = toolbox_SP.saveUserConfig(mContext, n, em, ph, id, rid, pwd);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), dataCheck, Toast.LENGTH_LONG).show();
        }

    }

    /* CHECK Methods */

    public String serverCheck() {
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


    /* "GOTO" Methods */

}