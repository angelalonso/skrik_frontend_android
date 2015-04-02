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
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
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
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                vibe.vibrate(400);
                String userfrom = extras.getString("userfrom");
                String message = extras.getString("message");
                startNotification(userfrom,message);
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
                sendNotification("Received: " + extras.toString());
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        Tool_GCM_BCastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        Log.i("TESTING MSG is",msg);
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, Act_Overview.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                       // .setSmallIcon(R.drawable.ic_stat_gcm)
                        .setContentTitle("GCM Notification")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    protected void startNotification(String userfrom, String message) {
        Context context = Tool_GCM_IntentService.this
                .getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(NOTIFICATION_SERVICE);

        Notification updateComplete = new Notification();
        updateComplete.icon = android.R.drawable.stat_notify_chat;
        // TODO: standard text
        updateComplete.tickerText = "New message";
        updateComplete.when = System.currentTimeMillis();

        Intent notificationIntent = new Intent(context,
                Tool_GCM_IntentService.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);

        // TODO: standard text
        String contentTitle = "you got a new message from user " + userfrom;

        String contentText = message;
        updateComplete.setLatestEventInfo(context, contentTitle,
                contentText, contentIntent);

        notificationManager.notify(0, updateComplete);
    }
}
