package com.afonseca.skrik;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    TextView tv_name;
    TextView tv_phone;
    TextView tv_email;


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
        tv_name = (TextView) findViewById(R.id.tv_name_preview);
        tv_phone = (TextView) findViewById(R.id.tv_phone_preview);
        tv_email = (TextView) findViewById(R.id.tv_email_preview);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mContext = getApplicationContext();
        Log.i(TAG,"onResume");

        //serverSide = serverCheck();
        dataAddProcess();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_overview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_user_settings:
                gotoUserConfig();
                return true;
            case R.id.action_check_tables:
                gotoShowDB();
                return true;
            case R.id.action_clear_alldata:
                clearDB();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* ACTION Methods */

    // Method to change appearance, depending on what data we are getting
    public void useDataMode(String mode){
        datamode = mode;
        tv_name.setText(getText(R.string.all_username) + " " + toolbox_SP.getUsername(mContext));
        tv_phone.setText(getText(R.string.all_phone) + " " + toolbox_SP.getPhone(mContext));
        tv_email.setText(getText(R.string.all_email) + " " + toolbox_SP.getEmail(mContext));
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
            case "selectEmail":
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
                tv_info.setText(R.string.msg_userreg_writecode);
                et_phone.setVisibility(View.INVISIBLE);
                sp_phone.setVisibility(View.INVISIBLE);
                et_email.setVisibility(View.INVISIBLE);
                et_code.setVisibility(View.VISIBLE);
                et_name.setVisibility(View.INVISIBLE);
                btn_ok.setVisibility(View.VISIBLE);
                btn_ok.setEnabled(true);
                break;
            case "enterName":
                tv_info.setText(R.string.msg_userreg_username);
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
                btn_ok.setEnabled(true);
                break;
            default:
                et_phone.setVisibility(View.INVISIBLE);
                sp_phone.setVisibility(View.INVISIBLE);
                et_email.setVisibility(View.INVISIBLE);
                et_code.setVisibility(View.INVISIBLE);
                et_name.setVisibility(View.INVISIBLE);
                btn_ok.setVisibility(View.INVISIBLE);
                btn_ok.setEnabled(true);
        }
    }

    // Method that captures the click on "ADD"
    // It checks what is being received from the EditTexts
    public void dataAdd(View view) {
        String phone;
        String email;
        String name;
        Intent intent;
        switch(datamode) {
            case "enterPhone":
                phone = et_phone.getText().toString();
                if ((phone != null) && (!phone.matches("more"))) {
                    toolbox_SP.setPhone(mContext, phone);
                    useDataMode("enterEmail");
                    getEmails();
                }
                break;
            case "selectPhone":
                //TODO: Which one to get here?
                phone = et_phone.getText().toString();
                if ((phone != null) && (!phone.matches("more"))) {
                    toolbox_SP.setPhone(mContext, phone);
                    useDataMode("enterEmail");
                    getEmails();
                }
                break;
            case "enterEmail":
                email = et_email.getText().toString();
                toolbox_SP.setEmail(mContext, email);
                useDataMode("enterCode");
                break;
            case "selectEmail":
                //TODO: Which one to get here?
                email = et_email.getText().toString();
                toolbox_SP.setEmail(mContext, email);
                useDataMode("enterCode");
                break;
            case "enterCode":
                useDataMode("enterName");
                break;
            case "enterName":
                name = et_name.getText().toString();
                toolbox_SP.setUsername(mContext, name);
                useDataMode("None");
                //TODO: Register user in Backend
                intent = new Intent(this, Act_Overview.class);
                startActivity(intent);
                break;
            default:
                useDataMode("None");
                intent = new Intent(this, Act_Overview.class);
                startActivity(intent);
                break;
        }
    }

    public void dataReset(View view) {
        //TODO: Maybe clean up here?
        dataAddProcess();
    }

    // This function is the main trigger of the whole process
    // It should leave us either adding a new phone or adding a new e-Mail or editing the whole apckage (where one can delete all)
    public void dataAddProcess() {

        getPhoneNr();
        //getPhones takes care of any other result that might be in "phone"
        //if more phone numbers or none of them are found, dataAdd will take care of this later.
        useDataMode("enterPhone");
        String phone = toolbox_SP.getPhone(mContext);
        Log.i(TAG,"chosen: " + phone);
        if ((phone != null) && (phone != ""))  {
            //This we do already in getPhoneNr
            //toolbox_SP.setPhone(mContext, phone);

            // THIS LOCKED THE APP!
            //checkDataFromPhone();
            useDataMode("enterEmail");
            getEmails();
        }

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

    public void getPhoneNr() {
        //PART 1 Find Phone numbers
        // - Find phone accounts in the phone
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
        // - What was already saved in this app? usually nothing, but...
        String prev_phone = toolbox_SP.getPhone(mContext);
        if (!foundPhoneAccounts.contains( prev_phone )) {
            foundPhoneAccounts.add(prev_phone);
        }
        // Manually adding for test purposes
        foundPhoneAccounts.add("00000000");
        foundPhoneAccounts.add("00000012");
        foundPhoneAccounts.add("00000342");

        //PART 2 Choose

        final Context inContext = mContext;
        Log.i(TAG,"found " + foundPhoneAccounts.size() + "phones");

        // If there is just one, save it to
        if (foundPhoneAccounts.size() == 1) {
            toolbox_SP.setPhone(mContext, foundPhoneAccounts.get(0));
            et_phone.setText(foundPhoneAccounts.get(0));
            Log.i(TAG, "ONE FOUDN");
        }

        //TODO:WE MIGHT NEED A TIMEOUT
        // If there are more than one, let the user choose one
        //   If there are several, show popup to choose one, and make phone="more"
        else if (foundPhoneAccounts.size() > 1) {

            foundPhoneAccounts.add(mContext.getResources().getString(R.string.btn_usercfg_entermanual));
            final CharSequence[] allAccounts = foundPhoneAccounts.toArray(new
                    CharSequence[foundPhoneAccounts.size()]);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(mContext.getResources().getString(R.string.msg_link_account));
            builder = builder.setItems(allAccounts, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String Chosen = allAccounts[which].toString();
                    // TODO: TEST THAT IT IS NOT mContext.getResources().getString(R.string.btn_usercfg_entermanual)
                    if (!Chosen.matches(mContext.getResources().getString(R.string.btn_usercfg_entermanual))) {
                        toolbox_SP.setPhone(inContext, Chosen);
                        et_phone.setText(Chosen);
                    } else {
                        //TODO: We are asked twice here
                        toolbox_SP.setPhone(inContext, null);
                        et_phone.setText(null);
                    }
                }
            });
            builder.show();
        }
    }

    public String getEmails() {
        String result;
        String email = toolbox_SP.getEmail(mContext);
        //TODO:
        //  - Get email accounts configured
        //  - If there are several, show popup to choose one, and make email="more"
        if ((email != null) && (!email.matches(""))){
            result = email;
        }
        result = "tralariquetevi@gmail.com";
        return result;
    }

    public String getCode() {
        String result;
        String email = toolbox_SP.getEmail(mContext);
        //TODO:
        //  - Get email accounts configured
        //  - If there are several, show popup to choose one, and make email="more"
        if ((email != null) && (!email.matches(""))){
            result = email;
        }
        result = "tralariquetevi@gmail.com";
        return result;
    }

    public String getName() {
        String result;
        String email = toolbox_SP.getEmail(mContext);
        //TODO:
        //  - Get email accounts configured
        //  - If there are several, show popup to choose one, and make email="more"
        if ((email != null) && (!email.matches(""))){
            result = email;
        }
        result = "tralariquetevi@gmail.com";
        return result;
    }

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
        foundPhoneAccounts.add("00000012");

        // If there is no phone, let user add it. (Nothing to do, then)
        // If there is just one, use it
        final Context inContext = mContext;
        Log.i(TAG,"found " + foundPhoneAccounts.size());
        if (foundPhoneAccounts.size() == 1) {
            toolbox_SP.setPhone(mContext,foundPhoneAccounts.get(0));
            checkDataFromPhone();
        }
        // If there are more than one, let the user choose one
        else if (foundPhoneAccounts.size() > 1) {
            foundPhoneAccounts.add(mContext.getResources().getString(R.string.btn_usercfg_entermanual));
            final CharSequence[] allAccounts = foundPhoneAccounts.toArray(new
                    CharSequence[foundPhoneAccounts.size()]);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(mContext.getResources().getString(R.string.msg_link_account));
            builder.setItems(allAccounts, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Pattern phonePattern = Patterns.PHONE;
                    String Chosen = allAccounts[which].toString();
                    if (phonePattern.matcher(Chosen).matches())
                    {
                        toolbox_SP.setPhone(inContext,Chosen);
                        toolbox_SP.setEmail(inContext,"");
                    }
                    else {
                        toolbox_SP.setEmail(inContext,"");
                        toolbox_SP.setPhone(inContext,"");
                    }
                }
            });
            builder.show();
        }
    }


    // This just checks what we have in the Backend for this Phone number, then
    public void checkDataFromPhone() {
        String phone = toolbox_SP.getPhone(mContext);
        String dataSaved = toolbox_BE.getDataFromPhoneNr(phone);
        toast = Toast.makeText(this, dataSaved, Toast.LENGTH_SHORT);
        toast.show();

        //toolbox_SP.setPhone(mContext,et_phone.getText().toString());
        //et_phone.setText("");
        //useDataMode("enterEmail");
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

    public void gotoUserConfig() {
        Intent intent = new Intent(this, Act_UserCfg.class);
        startActivity(intent);
    }

    public void gotoShowDB() {
        /** Called when the user clicks the Config button */
        Intent intent = new Intent(this, Act_Tables.class);
        startActivity(intent);
    }

    public void clearDB() {
        Context mContext = getApplicationContext();
        DB_Msgs_Handler msgsSQLHandler = new DB_Msgs_Handler(mContext);
        String Clearquery = "DELETE FROM MSGS WHERE id=id;";
        msgsSQLHandler.executeQuery(Clearquery);
    }
}