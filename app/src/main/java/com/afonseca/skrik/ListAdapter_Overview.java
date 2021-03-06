package com.afonseca.skrik;


import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListAdapter_Overview extends BaseAdapter {

    Context context;
    ArrayList<Data_OverviewItems> contactList;

    public ListAdapter_Overview(Context context, ArrayList<Data_OverviewItems> list) {

        this.context = context;
        contactList = list;
    }

    @Override
    public int getCount() {

        return contactList.size();
    }

    @Override
    public Object getItem(int position) {

        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        Data_OverviewItems contactListItems = contactList.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_overview, null);

        }

        TextView NrMsgs_tv = (TextView) convertView.findViewById(R.id.nr_msgs_tv);
        NrMsgs_tv.setText(contactListItems.getNrOfMsgs());
        if (contactListItems.getNrOfMsgs() != null && Integer.parseInt(contactListItems.getNrOfMsgs()) > 0) {
            NrMsgs_tv.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            NrMsgs_tv.setTextColor(context.getResources().getColor(R.color.gray));
        }
        TextView Userid_tv = (TextView) convertView.findViewById(R.id.id_tv);
        Userid_tv.setText(contactListItems.getUserid());
        TextView Username_tv = (TextView) convertView.findViewById(R.id.username_search_tv);
        Username_tv.setText(contactListItems.getUsername());
        TextView News_tv = (TextView) convertView.findViewById(R.id.news_tv);
        News_tv.setText(contactListItems.getNews());
        TextView Timestamp_tv = (TextView) convertView.findViewById(R.id.timestamp_tv);
        Timestamp_tv.setText(contactListItems.getTimestamp());


        return convertView;
    }
}
