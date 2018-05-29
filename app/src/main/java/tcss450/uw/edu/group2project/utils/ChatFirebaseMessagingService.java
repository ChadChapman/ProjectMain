package tcss450.uw.edu.group2project.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.chatApp.ChatActivity;

//import group2.tcss450.uw.edu.chatapp.Activities.ChatListActivity;

/**
 @credit: https://developer.android.com/training/notify-user/build-notification#Updating
 */
public class ChatFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "ChatFirebaseMessagingService";
    private static final String CHANNEL_ID = "fb_channel_0";
    private static int mFBNotificationID = 0;


}
