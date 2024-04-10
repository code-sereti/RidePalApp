package com.example.ridepal;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessageReceiver extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessageReceiver";

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            // Handle data payload if needed
        }

        if (remoteMessage.getNotification() != null) {
            // When a notification payload is received
            sendRideJoinRequestNotification(
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody()
            );
        }
    }

    void sendRideJoinRequestNotification(String title, String message) {
        // Get ride details from the notification message
        // Extract information like ride ID, requester's user ID, owner's user ID, etc.
        // Here, we assume you have this information available

        // Simulated ride details
        String rideId = "tfgh677676";
        String requesterUserId = "hvghytuuy";
        String rideOwnerId = "gvy6687jh"; // This is the ID of the owner of the ride

        // Logic to check if the notification is a ride join request
        // You might check the title or body for specific keywords or identifiers

        // In a real scenario, you would check if the notification is about a ride join request
        // Then, you'd construct the notification message accordingly and send it to the owner

        if (isRideJoinRequestNotification(title, message)) {
            sendNotificationToRideOwner(rideId, requesterUserId, rideOwnerId);
        }
    }

    // Method to check if the notification is a ride join request
    private boolean isRideJoinRequestNotification(String title, String message) {
        // Implement your logic to identify if the notification is about a ride join request
        // Example: Check if the title or message contains specific keywords related to join requests
        return title != null && title.equals("Ride Join Request")
                && message != null && message.startsWith("User");
    }

    // Method to send the actual notification to the ride owner
    private void sendNotificationToRideOwner(String rideId, String requesterUserId, String rideOwnerId) {
        // Create an Intent to open the appropriate activity when the notification is clicked
        // ...


        // Build the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "default")
//                .setSmallIcon(R.drawable.ic_notification_icon)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_large_notification_icon))
                .setContentTitle("Ride Join Request")
                .setContentText("User " + requesterUserId + " wants to join the ride.")
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
//                .setContentIntent(pendingIntent); // Set the pending intent here

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Check if the Android Version is greater than Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "default", "Ride Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Display the notification
        notificationManager.notify(0, notificationBuilder.build());
    }
}
