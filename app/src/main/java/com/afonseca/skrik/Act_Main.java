package com.afonseca.skrik;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;



public class Act_Main extends ActionBarActivity {

    /* Declarations */

    Control_Userconfig controlUserconfig = new Control_Userconfig();

    /* General Methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    /* Logic:
     - User data local is OK?
       - Goto News
     - No User data local?
       - Goto User config

    */
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_skrik);
        Context context = getApplicationContext();

        if (controlUserconfig.userOK_SharedPrefs(context).equals("OK")){
            Intent intent = new Intent(this, NewsActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, Act_UserCfg.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
    /* Same Logic as for onCreate */
        super.onResume();
        setContentView(R.layout.activity_skrik);

        Context context = getApplicationContext();

        if (controlUserconfig.userOK_SharedPrefs(context).equals("OK")){
            Intent intent = new Intent(this, NewsActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, Act_UserCfg.class);
            startActivity(intent);
        }
    }

    /* Check Functions */

    /* "GOTO" Calls */

    public void gotoUserConfig(View view) {
        /** In case the user lands on the main Activity,
         *   despite the workflow on Create and Resume,
         **/
        Intent intent = new Intent(this, Act_UserCfg.class);
        startActivity(intent);
    }
}
