package com.afonseca.skrik;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class Act_SearchUser extends ActionBarActivity {

    EditText name2Search;
    ListView userList_lv;
    Toolbox_Sharedprefs funcsUserCfg = new Toolbox_Sharedprefs();
    Toolbox_Backend backend = new Toolbox_Backend();

    /* General Behaviour Methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchuser);

        name2Search = (EditText) findViewById(R.id.search_et);
        userList_lv = (ListView) findViewById(R.id.userlist_lv);
    }

    public void updateUserList(View view) {
        String user2search = name2Search.getText().toString();
        showUserList(user2search);
    }

    private void showUserList(String user2search) {
        ArrayList<Data_SearchUser_ListItems> userList = new ArrayList<>();
        userList.clear();

        userList = backend.searchUser(user2search);

        Ctrl_SearchUser_ListAdapter userlistAdapter = new Ctrl_SearchUser_ListAdapter(
                Act_SearchUser.this, userList);
        userList_lv.setAdapter(userlistAdapter);

    }

    public void gotoChannel(View view) {
        Context mContext = getApplicationContext();
        TextView userid_tv = (TextView) view.findViewById(R.id.userid_search_tv);
        String userid_other = userid_tv.getText().toString();
        String userid_me = funcsUserCfg.getUid(mContext);
        if (userid_me.matches(getString(R.string.aux_dummy_uid))){
            Toast.makeText(getApplicationContext(), R.string.msg_user_not_registered, Toast.LENGTH_LONG).show();
            gotoUserConfig();
        } else {
            TextView username_tv = (TextView) view.findViewById(R.id.username_search_tv);
            String username = username_tv.getText().toString();
            Intent intent = new Intent(this, Act_Channel.class);
            Bundle b = new Bundle();
            b.putString("userid_other", userid_other);
            b.putString("userid_me", userid_me);
            b.putString("username", username);
            intent.putExtras(b);
            startActivity(intent);
        }
    }

    /* "GOTO" Calls */

    public void gotoUserConfig() {
        Intent intent = new Intent(this, Act_UserCfg.class);
        startActivity(intent);
    }
}
