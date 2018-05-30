package tcss450.uw.edu.group2project.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.chatApp.ChatActivity;

/**
 @credit: https://developer.android.com/training/notify-user/build-notification#Updating
 */
public class ChatFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "ChatFirebaseMessagingService";
    private static final String CHANNEL_ID = "fb_channel_0";
    private static int mFBNotificationIDInt = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                //scheduleJob();
//            } else {
//                // Handle message within 10 seconds
//                //handleNow();
//            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    /*
        This is where the magic happens.  Send a notification then open whatever
        activity from the notification.
     */
    private void sendNotification(RemoteMessage.Notification notification) {
        Intent intent = new Intent(this, ChatActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this
                , 0
                , intent
                , PendingIntent.FLAG_ONE_SHOT);
        //should probably clear out old notifications? is this stacking?, spamming them?
        createNotificationChannel();
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_chat_notif)
                .setContentTitle(notification.getTitle())
                .setAutoCancel(true)
                .setContentText(notification.getBody())
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(mFBNotificationIDInt, notificationBuilder.build());
    }

    /*
        Basically straight from the FireBase / Android docs.
        Because why wouldn't you make a separate thing for some APIs but support a ton of them?
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //may want it in prefs also?
            CharSequence name = getString(R.string.keys_prefs_notification_channel_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
