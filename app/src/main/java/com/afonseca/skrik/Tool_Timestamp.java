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
        /*
        TODO:
        - if timestamp is yesterday -> "YESTERDAY, HH:MM"
        - if ts is older -> "DD-MM HH:MM"
        - if ts is from othe year -> "DD-MM-YY HH:MM"
        - otherwise "HH:MM"
         */
        long cur_timestamp_raw = System.currentTimeMillis();

        SimpleDateFormat format_full = new SimpleDateFormat(inContext.getResources().getString(R.string.aux_date_format_full));


        long timestamp_long = timestamp;
        long timestamp_raw = timestamp_long * 1000;

        String timestamp_format = format_full.format(new Time(timestamp_raw));
        String cur_timestamp_format = format_full.format(new Time(cur_timestamp_raw));

        String year = timestamp_format.split(" ")[0].split("/")[2];
        String cur_year = cur_timestamp_format.split(" ")[0].split("/")[2];

        if (year.matches(cur_year)) {
            String day = timestamp_format.split(" ")[0].split("/")[0];
            String cur_day = cur_timestamp_format.split(" ")[0].split("/")[0];
            if (day.matches(cur_day)) {
                SimpleDateFormat format_today = new SimpleDateFormat(inContext.getResources().getString(R.string.aux_date_format_hour));
                timestamp_format = format_today.format(new Time(timestamp_raw));
            } else if (Integer.parseInt(cur_day) == (Integer.parseInt(day) + 1)) {
                SimpleDateFormat format_today = new SimpleDateFormat(inContext.getResources().getString(R.string.aux_date_format_hour));
                timestamp_format = inContext.getResources().getString(R.string.aux_date_yesterday) + ", " + format_today.format(new Time(timestamp_raw));

            } else {
                SimpleDateFormat format_month = new SimpleDateFormat(inContext.getResources().getString(R.string.aux_date_format_day));
                timestamp_format = format_month.format(new Time(timestamp_raw));
            }

        }


        String result = timestamp_format;
        return result;
    }
}
