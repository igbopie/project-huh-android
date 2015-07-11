package com.huhapp.android;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.huhapp.android.api.model.Notification;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private  static int UNIQUE_INT_PER_CALL = 1;
    public static final String TAG = "GcmIntentService";
    public static String NOTIFICATION_RECEIVED = "com.unitedcoders.android.broadcasttest.SHOWTOAST";


    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("GcmIntentService", "onHandleIntent");
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

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
                Notification notification = new Notification();
                notification.setCommentId(extras.getString("commentId"));
                notification.setQuestionId(extras.getString("questionId"));
                notification.setMessage(extras.getString("collapse_key"));

                sendNotification(notification);
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(Notification msg) {
        UNIQUE_INT_PER_CALL++;
        Log.i(TAG, "SendNotification: "+msg);
        Log.i(TAG, "SendNotification: "+msg.getQuestionId());

        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, QuestionDetailActivity.class);
        intent.putExtra(QuestionDetailActivity.EXTRA_QUESTION_ID, msg.getQuestionId());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(this, UNIQUE_INT_PER_CALL, intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.huh)
                        .setContentTitle("Huh?")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg.getMessage()))
                        .setContentText(msg.getMessage());

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(UNIQUE_INT_PER_CALL, mBuilder.build());

        Intent broadcast = new Intent();
        broadcast.setAction(NOTIFICATION_RECEIVED);
        sendBroadcast(broadcast);

    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.huh)
                        .setContentTitle("Huh?")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mNotificationManager.notify(UNIQUE_INT_PER_CALL, mBuilder.build());

        Intent broadcast = new Intent();
        broadcast.setAction(NOTIFICATION_RECEIVED);
        sendBroadcast(broadcast);
    }

    //TODO GROUP
}