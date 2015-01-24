package com.afonseca.skrik;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by afonseca on 1/24/2015.
 */
public class NewsActivity extends ActionBarActivity {

    Control_SqlHandler sqlHandler;
    ListView NewsList_lv;
    Button btnsubmit;

    Control_Userconfig controlUserconfig = new Control_Userconfig();
    Control_BackendHandler backend = new Control_BackendHandler();
    TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        NewsList_lv = (ListView) findViewById(R.id.newslist_lv);
        sqlHandler = new Control_SqlHandler(this);
        showList();

        btnsubmit = (Button) findViewById(R.id.aux_btn);
        btnsubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String name = "Hey you!";
                String phoneNo = "Im Testing!";

                String query = "INSERT INTO PHONE_CONTACTS(name,phone) values ('"
                        + name + "','" + phoneNo + "')";
                sqlHandler.executeQuery(query);
                showList();
            }
        });

        username = (TextView) findViewById(R.id.username_tv);

        Context context = getApplicationContext();
        String output = controlUserconfig.getUsername(context);

        username.setText(output + " News");
    }

    @Override
    protected void onResume() {
        super.onResume();

        username = (TextView) findViewById(R.id.username_tv);

        Context context = getApplicationContext();
        String output = controlUserconfig.getUsername(context);

        username.setText(output + " news");
    }

    private void showList() {

        ArrayList<Data_NewsListItems> contactList = new ArrayList<Data_NewsListItems>();
        contactList.clear();
        String query = "SELECT * FROM PHONE_CONTACTS ";
        Cursor c1 = sqlHandler.selectQuery(query);
        if (c1 != null && c1.getCount() != 0) {
            if (c1.moveToFirst()) {
                do {
                    Data_NewsListItems newsListItems = new Data_NewsListItems();

                    newsListItems.setSlno(c1.getString(c1
                            .getColumnIndex("slno")));
                    newsListItems.setName(c1.getString(c1
                            .getColumnIndex("name")));
                    newsListItems.setPhone(c1.getString(c1
                            .getColumnIndex("phone")));
                    contactList.add(newsListItems);

                } while (c1.moveToNext());
            }
        }
        c1.close();

        Control_NewsListAdapter contactListAdapter = new Control_NewsListAdapter(
                NewsActivity.this, contactList);
        NewsList_lv.setAdapter(contactListAdapter);

    }


    /** Called when the user clicks the Config button */
    public void gotoUserConfig(View view) {
        Intent intent = new Intent(this, UserConfigActivity.class);
        startActivity(intent);
    }
}
