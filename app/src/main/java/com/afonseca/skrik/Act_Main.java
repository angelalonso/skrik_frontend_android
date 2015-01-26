package com.afonseca.skrik;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;



public class Act_Main extends ActionBarActivity {

    Control_Userconfig controlUserconfig = new Control_Userconfig();

    /* Logic -
    * - User data local is OK?
    *   - Goto News
    * - No User data local?
    *   - Goto User config
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_skrik);
        Context context = getApplicationContext();

        if (controlUserconfig.userOK(context).equals("OK")){
            Intent intent = new Intent(this, NewsActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, UserConfigActivity.class);
            startActivity(intent);
        }
    }

    /* Same Logic as for onCreate */

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_skrik);

        Context context = getApplicationContext();

        if (controlUserconfig.userOK(context).equals("OK")){
            Intent intent = new Intent(this, NewsActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, UserConfigActivity.class);
            startActivity(intent);
        }
    }

    /** In case the user lands on the main Activity,
     *   despite the workflow on Create and Resume,
     **/
    public void gotoUserConfig(View view) {
        Intent intent = new Intent(this, UserConfigActivity.class);
        startActivity(intent);
    }
}
