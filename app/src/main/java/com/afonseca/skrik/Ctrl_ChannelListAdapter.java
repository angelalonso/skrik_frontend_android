package com.afonseca.skrik;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Ctrl_ChannelListAdapter extends BaseAdapter {

    Context context;
    ArrayList<Data_ChannelItems> chatList;

    public Ctrl_ChannelListAdapter(Context context, ArrayList<Data_ChannelItems> list) {

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
        Data_ChannelItems channelItems = chatList.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_channel, null);

        }

        TextView Message_tv = (TextView) convertView.findViewById(R.id.message_tv);
        Message_tv.setText(channelItems.getMsg());
        TextView Timestamp_tv = (TextView) convertView.findViewById(R.id.timestamp_tv);
        Timestamp_tv.setText(channelItems.getTimestamp());


        return convertView;
    }
}
