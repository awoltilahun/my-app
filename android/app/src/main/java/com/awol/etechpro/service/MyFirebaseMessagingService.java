package com.awol.etechpro.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.awol.etechpro.R;
import com.awol.etechpro.ui.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    private static final String CHANNEL_ID = "etech_pro_channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Message received from: " + remoteMessage.getFrom());

        String title = "Etech Pro";
        String body  = "You have a new notification";

        // Get notification data
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body  = remoteMessage.getNotification().getBody();
        }

        // Also check data payload
        if (remoteMessage.getData().size() > 0) {
            if (remoteMessage.getData().containsKey("title"))
                title = remoteMessage.getData().get("title");
            if (remoteMessage.getData().containsKey("body"))
                body = remoteMessage.getData().get("body");
        }

        sendNotification(title, body);
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM token: " + token);
        // Subscribe to 'all' topic to receive broadcast notifications
        com.google.firebase.messaging.FirebaseMessaging.getInstance()
            .subscribeToTopic("all")
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) Log.d(TAG, "Subscribed to topic: all");
            });
    }

    private void sendNotification(String title, String messageBody) {
        // Open MainActivity when notification is tapped
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(android.graphics.BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Etech Pro Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Tech tips and app updates");
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}
