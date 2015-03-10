package com.afonseca.skrik;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;


public class Act_Tables extends ActionBarActivity {

    /* Declarations */

    DB_Msgs_Handler newsMsgsSQLHandler;
    DB_Users_Handler newsUsersSQLHandler;

    String content;
    ListView content_lv;

    /* General Methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tables);

        newsMsgsSQLHandler = new DB_Msgs_Handler(this);
        newsUsersSQLHandler = new DB_Users_Handler(this);

        content_lv = (ListView) findViewById(R.id.table_content_lv);

        msgsLoad();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    public void msgsLoad(){
        content = "";

        List<String> array_list = new ArrayList<String>();
        array_list.add("userid_from | userid_to | message | status | timestamp | backend_id");
        String query = "SELECT userid_from, userid_to, message, status, timestamp, backend_id  FROM MSGS";
        newsMsgsSQLHandler = new DB_Msgs_Handler(this);
        Cursor c1 = newsMsgsSQLHandler.selectQuery(query);
        if (c1 != null && c1.getCount() > 0) {
            if (c1.moveToFirst()) {
                do {
                    content = c1.getString(c1.getColumnIndex("userid_from")) + " | ";
                    content = content + c1.getString(c1.getColumnIndex("userid_to")) + " | ";
                    content = content + c1.getString(c1.getColumnIndex("message")) + " | ";
                    content = content + c1.getString(c1.getColumnIndex("status")) + " | ";
                    content = content + c1.getString(c1.getColumnIndex("timestamp")) + " | ";
                    content = content + c1.getString(c1.getColumnIndex("backend_id"));
                    array_list.add(content);
                } while (c1.moveToNext());
            }
        }
        c1.close();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                array_list );

        content_lv.setAdapter(arrayAdapter);

    }

    public void usersLoad(){
        content = "";

        List<String> array_list = new ArrayList<String>();

        String usr_query = "SELECT id, username, blacklisted FROM USERS where blacklisted=0";

        Cursor c1 = newsUsersSQLHandler.selectQuery(usr_query);
        if (c1 != null && c1.getCount() > 0) {
            if (c1.moveToFirst()) {
                do {
                    String userid = c1.getString(c1.getColumnIndex("id"));
                    String username = c1.getString(c1.getColumnIndex("username"));
                    //TODO: Check when the userid or status is null - MAYBE DELETE THEM?
                    //String msg_query = "SELECT * FROM (SELECT count(message) as nr_msgs, userid_from as user,status FROM MSGS WHERE userid_from='" + userid + "' GROUP BY user UNION SELECT count(message) as nr_msgs, userid_to as user,status FROM MSGS WHERE userid_to='" + userid + "' GROUP BY user)";
                    //String msg_query = "SELECT * FROM (SELECT COUNT(message) as nr_msgs,'' as timestamp, '' as message FROM MSGS WHERE (status='received' and userid_from='" + userid + "') UNION ALL SELECT '' as nr_msgs, MAX(timestamp) as timestamp, '' as message FROM MSGS WHERE (status='received' and userid_from='" + userid + "') UNION ALL SELECT '' as nr_msgs,timestamp, message FROM MSGS WHERE (status='received' and userid_from='" + userid + "')) t GROUP BY timestamp";
                    String msg_query = "SELECT COUNT(message) as nr_msgs FROM MSGS WHERE (status='received' and userid_from='" + userid + "')";
                    Cursor c2 = newsMsgsSQLHandler.selectQuery(msg_query);
                    if (c2 != null && c2.getCount() > 0) {
                        if (c2.moveToFirst()) {
                            do {
                                content = c2.getString(c2.getColumnIndex("nr_msgs")) + " | ";
                                content = content + userid;
                                array_list.add(content);
                            } while (c2.moveToNext());
                        }
                    }
                    c2.close();
                } while (c1.moveToNext());
            }
        }
        c1.close();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                array_list );

        content_lv.setAdapter(arrayAdapter);
    }

    /* Check Functions */

    /* "GOTO" Calls */

    public void gotoMsgsLoad(View view){
        msgsLoad();
    }

    public void gotoUsersLoad(View view){
        usersLoad();
    }

}
