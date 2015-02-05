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

public class Control_ChatListAdapter extends BaseAdapter {

    Context context;
    ArrayList<Data_ChatListItems> chatList;

    public Control_ChatListAdapter(Context context, ArrayList<Data_ChatListItems> list) {

        this.context = context;
        chatList = list;
    }

    @Override
    public int getCount() {

        return chatList.size();
    }

    @Override
    public Object getItem(int position) {

        return chatList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        Data_ChatListItems chatListItems = chatList.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.news_item, null);

        }

        TextView NrMsgs_tv = (TextView) convertView.findViewById(R.id.nr_msgs_tv);
        NrMsgs_tv.setText(chatListItems.getNrOfMsgs());
        TextView Userid_tv = (TextView) convertView.findViewById(R.id.id_tv);
        Userid_tv.setText(chatListItems.getUserid());
        TextView Username_tv = (TextView) convertView.findViewById(R.id.username_tv);
        Username_tv.setText(chatListItems.getUsername());
        TextView News_tv = (TextView) convertView.findViewById(R.id.news_tv);
        News_tv.setText(chatListItems.getNews());
        TextView Timestamp_tv = (TextView) convertView.findViewById(R.id.timestamp_tv);
        Timestamp_tv.setText(chatListItems.getTimestamp());


        return convertView;
    }
}
