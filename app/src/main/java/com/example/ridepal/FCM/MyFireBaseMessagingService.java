package com.example.ridepal.FCM;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.ridepal.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0) {
            String title = remoteMessage.getData().get("Title");
            String message = remoteMessage.getData().get("Message");

            if (title != null && message != null) {
                // Create notification
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                        .setContentTitle(title)
                        .setContentText(message)
                        .setSmallIcon(R.drawable.logo);

                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(0, builder.build());

                Log.d("Notification", "Received notification: " + title + " - " + message);
            } else {
                Log.e("Notification", "Incomplete notification data");
            }
        } else {
            Log.e("Notification", "No data in the notification");
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        // Handle scenario when messages are deleted from FCM server before being delivered
        Log.e("Notification", "Some messages were deleted from the FCM server");
    }
}