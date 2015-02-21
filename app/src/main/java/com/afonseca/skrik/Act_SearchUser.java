package com.afonseca.skrik;


import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import java.util.ArrayList;


public class Act_SearchUser extends ActionBarActivity {

    EditText name2Search;
    Ctrl_Backend backend = new Ctrl_Backend();

    /* General Behaviour Methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchuser);

        name2Search = (EditText) findViewById(R.id.search_et);
    }

    public void updateUserList(View view) {
        String user2search = name2Search.getText().toString();
        showUserList(user2search);
    }

    private void showUserList(String user2search) {
        ArrayList<Data_OverviewItems> contactList = new ArrayList<>();
        contactList.clear();

        String userlist = backend.searchUser(user2search);

    }
}
