package com.afonseca.skrik;

import android.content.Context;
import android.util.Log;

import java.sql.Time;
import java.text.SimpleDateFormat;

/**
 * Created by afonseca on 3/13/2015.
 */
public class Tool_Timestamp {
    public static String getBeauty(Context inContext,int timestamp){
        String current_timestamp = String.valueOf(System.currentTimeMillis() / 1000);

        SimpleDateFormat full_date = new SimpleDateFormat(inContext.getResources().getString(R.string.aux_date_format_hour));
        long timestamp_long = timestamp;
        long timestamp_raw = timestamp_long * 1000;
        String result = full_date.format(new Time(timestamp_raw));
        return result;
    }
}
