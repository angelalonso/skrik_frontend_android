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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

    TextView tv_info;
    EditText et_phone;
    Spinner sp_phone;
    EditText et_email;
    EditText et_code;
    EditText et_name;
    Button btn_ok;

    String serverSide;
    private Toast toast;
    private long lastBackPressTime = 0;

    String datamode = "None";
    String TAG = "Checkpoint! ->";

    /* LOADING Methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userregister);
        Log.i(TAG, "onCreate");

        tv_info = (TextView) findViewById(R.id.tv_usrreg_info);
        et_phone = (EditText) findViewById(R.id.et_usrreg_phone);
        sp_phone = (Spinner) findViewById(R.id.sp_usrreg_phone);
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
        dataCheckProcess(mContext);
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

    public void dataCheckProcess(Context mContext) {
        // STEP 1: Check that a phone can be found
        //  If nothing found or several found -> GET ONE
        //  When you have one, DO Something on Backend
        //  If there is no known NAME -> GET ONE

        // Check that a phone can be found
        useDataMode("enterPhone");
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

        // TESTING
        foundPhoneAccounts.add("00000000");

        // If there is no phone, let user add it. (Nothing to do, then)
        // If there is just one, use it
        Log.i(TAG,"found " + foundPhoneAccounts.size());
        if (foundPhoneAccounts.size() == 1) {
            toolbox_SP.setPhone(mContext,foundPhoneAccounts.get(0));
            checkDataFromPhone();
        }
        // If there are more than one, let the user choose one
        else if (foundPhoneAccounts.size() > 1) {
            foundPhoneAccounts.add(mContext.getResources().getString(R.string.btn_usercfg_entermanual));
            //final CharSequence[] phoneAccounts = foundPhoneAccounts.toArray(new CharSequence[foundPhoneAccounts.size()]);
            useDataMode("selectPhone");
            ArrayAdapter<String> adp1 = new ArrayAdapter<>(mContext,android.R.layout.simple_list_item_1,foundPhoneAccounts);
            sp_phone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
                    String selected = parent.getItemAtPosition(pos).toString();
                    if (selected.matches(getResources().getString(R.string.btn_usercfg_entermanual))) {
                        useDataMode("enterPhone");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }

            });
            sp_phone.setAdapter(adp1);
        }
        // No checkdatafromphone here, since it will be triggered with the "OK" click
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

    public void checkDataFromPhone() {
        String phone = toolbox_SP.getPhone(mContext);
        String dataSaved = toolbox_BE.getDataFromPhoneNr(phone);
        toast = Toast.makeText(this, dataSaved, Toast.LENGTH_SHORT);
        toast.show();

        //toolbox_SP.setPhone(mContext,et_phone.getText().toString());
        et_phone.setText("");
        useDataMode("enterEmail");
    }

    public void useDataMode(String mode){
        datamode = mode;
        switch(mode) {
            case "enterPhone":
                tv_info.setText(R.string.msg_userreg_writephone);
                et_phone.setVisibility(View.VISIBLE);
                sp_phone.setVisibility(View.INVISIBLE);
                et_email.setVisibility(View.INVISIBLE);
                et_code.setVisibility(View.INVISIBLE);
                et_name.setVisibility(View.INVISIBLE);
                btn_ok.setVisibility(View.VISIBLE);
                btn_ok.setEnabled(true);
                break;
            case "selectPhone":
                tv_info.setText(R.string.msg_userreg_writephone);
                et_phone.setVisibility(View.INVISIBLE);
                sp_phone.setVisibility(View.VISIBLE);
                et_email.setVisibility(View.INVISIBLE);
                et_code.setVisibility(View.INVISIBLE);
                et_name.setVisibility(View.INVISIBLE);
                btn_ok.setVisibility(View.VISIBLE);
                btn_ok.setEnabled(true);
                break;
            case "enterEmail":
                tv_info.setText(R.string.msg_userreg_writeemail);
                et_phone.setVisibility(View.INVISIBLE);
                sp_phone.setVisibility(View.INVISIBLE);
                et_email.setVisibility(View.VISIBLE);
                et_code.setVisibility(View.INVISIBLE);
                et_name.setVisibility(View.INVISIBLE);
                btn_ok.setVisibility(View.VISIBLE);
                btn_ok.setEnabled(true);
                break;
            case "enterCode":
                et_phone.setVisibility(View.INVISIBLE);
                sp_phone.setVisibility(View.INVISIBLE);
                et_email.setVisibility(View.INVISIBLE);
                et_code.setVisibility(View.VISIBLE);
                et_name.setVisibility(View.INVISIBLE);
                btn_ok.setVisibility(View.VISIBLE);
                btn_ok.setEnabled(true);
                break;
            case "enterName":
                et_phone.setVisibility(View.INVISIBLE);
                sp_phone.setVisibility(View.INVISIBLE);
                et_email.setVisibility(View.INVISIBLE);
                et_code.setVisibility(View.INVISIBLE);
                et_name.setVisibility(View.VISIBLE);
                btn_ok.setVisibility(View.VISIBLE);
                btn_ok.setEnabled(true);
                break;
            case "None":
                et_phone.setVisibility(View.INVISIBLE);
                sp_phone.setVisibility(View.INVISIBLE);
                et_email.setVisibility(View.INVISIBLE);
                et_code.setVisibility(View.INVISIBLE);
                et_name.setVisibility(View.INVISIBLE);
                btn_ok.setVisibility(View.INVISIBLE);
                btn_ok.setEnabled(false);
                break;
            default:
                et_phone.setVisibility(View.INVISIBLE);
                sp_phone.setVisibility(View.INVISIBLE);
                et_email.setVisibility(View.INVISIBLE);
                et_code.setVisibility(View.INVISIBLE);
                et_name.setVisibility(View.INVISIBLE);
                btn_ok.setVisibility(View.INVISIBLE);
                btn_ok.setEnabled(false);
        }

    }

    public void dataAdd(View view) {

        switch(datamode) {
            case "enterPhone":
                checkDataFromPhone();
                break;
            case "selectPhone":
                Toast.makeText(mContext, "OnItemSelectedListener : " + sp_phone.getSelectedItem(), Toast.LENGTH_SHORT).show();
                checkDataFromPhone();
                break;
            case "enterEmail":
                useDataMode("enterCode");
                break;
            case "enterCode":
                useDataMode("enterName");
                break;
            case "enterName":
                useDataMode("None");
                break;
            case "None":
                useDataMode("None");
                break;
            default:
                useDataMode("None");
        }

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