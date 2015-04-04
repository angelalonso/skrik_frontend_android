package com.afonseca.skrik;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/* TODO:
- If a notification is clicked -> GOTO Act_Overview AND update data
*/

/**
 * Created by afonseca on 3/16/2015.
 */
public class Tool_GCM_BCastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that Tool_GCM_IntentService will handle the intent.
        Log.i("TESTING GCM", "Message received!");
        ComponentName comp = new ComponentName(context.getPackageName(),
                Tool_GCM_IntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
