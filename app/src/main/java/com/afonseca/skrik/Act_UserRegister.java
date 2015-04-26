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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


    /* TODO: STEPS
      - Check that a phone can be found
        - YES, continue
        - NO
          - Ask for it
      - Is the phone in our DB (Backend) ?
        - YES
          - Ask to use it
            - NO ---------> Go to create a new user
            - YES
              - Send e-Mail with Password
              - Inform user
              - Wait for user to enter password
                - OK -------> ...DONE
                - NOT OK
                  - Try again
                  - OR Show button to re-send email
                  - OR Show button to ---------> Go to create a new user
        - NO (phone in our DB)
          - Does user have a username already? (changing device)
            - YES
              - Ask for e-mail address
              - Send e-Mail with Password
              - Inform user
              - Wait for user to enter password
                - OK -------> Update davice in BAckend and...DONE
                - NOT OK
                  - Try again
                  - OR Show button to re-send email
                  - OR Show button to ---------> Go to create a new user
            - NO ---------> Go to create a new user
     */

public class Act_UserRegister extends ActionBarActivity {

    /* Declarations */

    Toolbox_Backend toolbox_BE = new Toolbox_Backend();
    Toolbox_Sharedprefs toolbox_SP = new Toolbox_Sharedprefs();
    Tool_Timestamp my_Timestamp = new Tool_Timestamp();

    Context mContext;

    EditText et_phone;
    EditText et_email;
    EditText et_code;
    EditText et_name;
    Button btn_ok;

    String serverSide;
    private Toast toast;
    private long lastBackPressTime = 0;

    String TAG = "Checkpoint! ->";

    /* LOADING Methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userregister);
        Log.i(TAG, "onCreate");

        et_phone = (EditText) findViewById(R.id.et_usrreg_phone);
        et_email = (EditText) findViewById(R.id.et_usrreg_email);
        et_code = (EditText) findViewById(R.id.et_usrreg_code);
        et_name = (EditText) findViewById(R.id.et_usrreg_name);
        btn_ok = (Button) findViewById(R.id.btn_userreg_ok);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mContext = getApplicationContext();
        Log.i(TAG,"onResume");

        //serverSide = serverCheck();
        userCheckProcess(mContext);
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

    public void userCheckProcess(Context mContext) {
        final Context inContext = mContext;
        //TODO: Change mode according to results instead of this
        //TODO: Hint text must be shorter
        et_phone.setVisibility(View.VISIBLE);
        btn_ok.setVisibility(View.VISIBLE);
        // STEP 1: Check that a phone can be found
        List<String> foundPhoneAccounts = new ArrayList<>();
        Pattern phonePattern = Patterns.PHONE;
        Account[] accounts = AccountManager.get(mContext).getAccounts();
        for (Account account : accounts) {
            if (phonePattern.matcher(account.name).matches()) {
                String possiblePhone = account.name;
                if (!foundPhoneAccounts.contains( possiblePhone )) {
                    foundPhoneAccounts.add(possiblePhone);
                }
            }
        }
        // If there is no phone, let user add it. If there are several phones, let user choose
        if (foundPhoneAccounts.size() == 0) {

        } else if (foundPhoneAccounts.size() == 1) {
            toolbox_SP.setPhone(mContext,foundPhoneAccounts.get(0));
        } else if (foundPhoneAccounts.size() > 1) {
            foundPhoneAccounts.add(mContext.getResources().getString(R.string.btn_usercfg_entermanual));
            final CharSequence[] allAccounts = foundPhoneAccounts.toArray(new
                    CharSequence[foundPhoneAccounts.size()]);
            AlertDialog.Builder builderPh = new AlertDialog.Builder(this);
            builderPh.setTitle(mContext.getResources().getString(R.string.msg_link_account));
            builderPh.setItems(allAccounts, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Pattern phonePattern = Patterns.PHONE;
                    String Chosen = allAccounts[which].toString();
                    if (phonePattern.matcher(Chosen).matches())
                    {
                        toolbox_SP.setPhone(inContext,Chosen);
                    }
                }
            });
            builderPh.show();
        }

        //TODO: From here on,

    /* TODO: STEPS
      - Check that a phone can be found
        - YES, continue
        - NO
          - Ask for it
      - Is the phone in our DB (Backend) ?
        - YES
          - Ask to use it
            - NO ---------> Go to create a new user
            - YES
              - Send e-Mail with Password
              - Inform user
              - Wait for user to enter password
                - OK -------> ...DONE
                - NOT OK
                  - Try again
                  - OR Show button to re-send email
                  - OR Show button to ---------> Go to create a new user
        - NO (phone in our DB)
          - Does user have a username already? (changing device)
            - YES
              - Ask for e-mail address
              - Send e-Mail with Password
              - Inform user
              - Wait for user to enter password
                - OK -------> Update davice in BAckend and...DONE
                - NOT OK
                  - Try again
                  - OR Show button to re-send email
                  - OR Show button to ---------> Go to create a new user
            - NO ---------> Go to create a new user
     */

    }
    public void dataMode(String mode){

    }
/*
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

*/

    /* "GOTO" Methods */

}