package com.afonseca.skrik;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class Tool_GCM_IntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    NotificationManager mNotificationManager;
    /* TODO: NOT NEEDED?
    NotificationCompat.Builder notification;
    TaskStackBuilder stackBuilder;
    Intent resultIntent;
    PendingIntent pIntent;
    NotificationManager manager;
*/

    String TAG = "GCM";

    public Tool_GCM_IntentService() {
        super("Tool_GCM_IntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context mContext = getApplicationContext();
        Vibrator vibe = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE) ;
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(mContext);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        Log.i("TESTING GCM Message",messageType);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification(mContext,"Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification(mContext,"Deleted messages on server: " +
                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                vibe.vibrate(400);
                String userfrom = extras.getString("userfrom");
                String message = extras.getString("message");
                startNotification(mContext,userfrom,message);
                /* TODO: NOT NEEDED?
                for (int i=0; i<5; i++) {
                    Log.i(TAG, "Working... " + (i + 1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());

                */
                // Post notification of received message.
                sendNotification(mContext,"Received: " + extras.toString());
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        Tool_GCM_BCastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(Context mContext,String msg) {
        Log.i("TESTING MSG is",msg);
        mNotificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        //Intent notificationIntent = new Intent(mContext, Act_Overview.class);

        Intent showIntent = new Intent(mContext, Act_Overview.class);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, showIntent, 0);

        //PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, Act_Overview.class), PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
/*
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("MyApp")
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setSmallIcon(icon);
  */
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                       // .setSmallIcon(R.drawable.ic_stat_gcm)
                        .setContentTitle("GCM Notification")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setDefaults(Notification.DEFAULT_ALL |
                                Notification.DEFAULT_SOUND |
                                Notification.DEFAULT_VIBRATE |
                                Notification.DEFAULT_LIGHTS |
                                Notification.FLAG_AUTO_CANCEL)
                        .setContentText(msg)
                        //.setContentIntent(contentIntent.getActivity(mContext, 0, showIntent, PendingIntent.FLAG_CANCEL_CURRENT))
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    protected void startNotification(Context mContext, String userfrom, String message) {
        //Context contextie = Tool_GCM_IntentService.this.getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) mContext
                .getSystemService(NOTIFICATION_SERVICE);

        Notification updateComplete = new Notification();

        updateComplete.icon = android.R.drawable.stat_notify_chat;
        // TODO: standard text
        updateComplete.tickerText = "New message";
        updateComplete.when = System.currentTimeMillis();

        Intent notificationIntent = new Intent(mContext, Act_Overview.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);


        //TODO: DELETE IF NOT NEEDED (OLD)
        /*Intent notificationIntent = new Intent(context,
                Tool_GCM_IntentService.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);
*/
        // TODO: standard text
        String contentTitle = "User " + userfrom + " says:";

        String contentText = message;
        updateComplete.setLatestEventInfo(mContext, contentTitle,
                contentText, contentIntent);

        updateComplete.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, updateComplete);
    }
}
