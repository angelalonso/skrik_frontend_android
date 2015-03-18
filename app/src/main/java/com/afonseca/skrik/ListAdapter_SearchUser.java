package com.afonseca.skrik;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter_SearchUser extends BaseAdapter {

    Context context;
    ArrayList<Data_SearchUser_ListItems> userList;

    public ListAdapter_SearchUser(Context context, ArrayList<Data_SearchUser_ListItems> list) {

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
        Data_SearchUser_ListItems channelItems = userList.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_usersearch, null);

        }

        TextView Username_tv = (TextView) convertView.findViewById(R.id.username_search_tv);
        Username_tv.setText(channelItems.getUsername());
        TextView UserID_tv = (TextView) convertView.findViewById(R.id.userid_search_tv);
        UserID_tv.setText(channelItems.getUserID());
        TextView Status_tv = (TextView) convertView.findViewById(R.id.status_search_tv);
        Status_tv.setText(channelItems.getStatus());


        return convertView;
    }

}
