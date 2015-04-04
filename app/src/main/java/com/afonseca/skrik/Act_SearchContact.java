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

/* TODO:
- If contact is (short) clicked ->...
  - ...if contact is NOT blacklisted -> add contact to list for overview, open Channel
  - ...if contact is blacklisted -> ask for confirmation before opening Channel
- If contact is LONG-clicked -> Show options: blacklist, open Channel  // also in  Act_Overview //
 */
public class Act_SearchContact extends ActionBarActivity {

    EditText name2Search;
    ListView contactList_lv;
    Toolbox_Sharedprefs toolbox_SP = new Toolbox_Sharedprefs();
    Toolbox_Backend backend = new Toolbox_Backend();
    Toolbox_LocalSQLite toolbox_SQL = new Toolbox_LocalSQLite();

    /* General Behaviour Methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchcontact);

        name2Search = (EditText) findViewById(R.id.search_et);
        contactList_lv = (ListView) findViewById(R.id.contactlist_lv);
    }

    public void updateContactList(View view) {
        String contact2search = name2Search.getText().toString();
        showContactList(contact2search);
    }

    private void showContactList(String contact2search) {
        ArrayList<Data_SearchContact_ListItems> contactList = new ArrayList<>();
        contactList.clear();

        contactList = backend.searchUser(contact2search);

        ListAdapter_SearchContact contactlistAdapter = new ListAdapter_SearchContact(
                Act_SearchContact.this, contactList);
        contactList_lv.setAdapter(contactlistAdapter);

    }

    public void gotoChannel(View view) {
        Context mContext = getApplicationContext();
        TextView userid_tv = (TextView) view.findViewById(R.id.userid_search_tv);
        String userid_other = userid_tv.getText().toString();
        String userid_me = toolbox_SP.getUid(mContext);
        if (userid_me.matches(getString(R.string.aux_dummy_uid))){
            Toast.makeText(getApplicationContext(), R.string.msg_user_not_registered, Toast.LENGTH_LONG).show();
            gotoUserConfig();
        } else {
            TextView username_tv = (TextView) view.findViewById(R.id.username_search_tv);
            String username = username_tv.getText().toString();
            toolbox_SQL.addNewUser(mContext,userid_other,username);
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
