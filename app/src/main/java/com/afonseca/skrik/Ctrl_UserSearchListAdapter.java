package com.afonseca.skrik;

/**
 * Created by aaf on 1/24/15.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Ctrl_UserSearchListAdapter extends BaseAdapter {

    Context context;
    ArrayList<Data_UserListItems> userList;

    public Ctrl_UserSearchListAdapter(Context context, ArrayList<Data_UserListItems> list) {

        this.context = context;
        userList = list;
    }

    @Override
    public int getCount() {

        return userList.size();
    }

    @Override
    public Object getItem(int position) {

        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        Data_UserListItems channelItems = userList.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.userlist_item, null);

        }

        TextView Username_tv = (TextView) convertView.findViewById(R.id.username_tv);
        Username_tv.setText(channelItems.getUsername());
        TextView Status_tv = (TextView) convertView.findViewById(R.id.status_tv);
        Status_tv.setText(channelItems.getStatus());


        return convertView;
    }
}
