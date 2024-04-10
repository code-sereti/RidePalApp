package com.example.ridepal;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridepal.Adapter.NotificationAdapter;
import com.example.ridepal.Models.MyData;
import com.example.ridepal.Models.Notification;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Notifications extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notifications;
    private TextView back;

    private FirebaseFirestore db;
    private CollectionReference notificationsCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        notificationsCollection = db.collection("notifications").document(MyData.userId).collection("sentNotifications");

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerView);
        back = findViewById(R.id.notificationsBack);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(Notifications.this,notifications);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Load notifications from Firestore
        loadNotifications();

        // Handle item click listener
        adapter.setOnItemClickListener(new NotificationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Handle notification clicked
                Notification clickedNotification = notifications.get(position);

                View itemView = recyclerView.getChildAt(position);
                if (itemView != null) {
                    itemView.setBackgroundColor(ContextCompat.getColor(Notifications.this, R.color.whiteCardColor));
                }
                // Update the notification's read state in the list and in Firestore if needed
                clickedNotification.setRead(true);
                adapter.notifyItemChanged(position);
                if (!clickedNotification.isRead()) {
                    // Update the notification's read state in Firestore
                    markNotificationAsRead(position);
                }

                // Here you can handle further actions when a notification is clicked
                // For example, open a detailed view of the notification
            }
        });
    }
    private void loadNotifications() {
        notificationsCollection.orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Notification notification = documentSnapshot.toObject(Notification.class);
                            notifications.add(notification);
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure to load notifications
                        Log.e("Notifications", "Error fetching notifications: " + e.getMessage());
                        Toast.makeText(Notifications.this, "You don't have notifications.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void markNotificationAsRead(int position) {
        Notification clickedNotification = notifications.get(position);
        clickedNotification.setRead(true);
        adapter.notifyItemChanged(position);

        // Update the notification's read state in Firestore
        notificationsCollection.document(clickedNotification.getId())
                .update("read", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Notification marked as read in Firestore
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure to mark notification as read in Firestore
                        Log.e("Notifications", "Error updating notification status: " + e.getMessage());
                        Toast.makeText(Notifications.this, "Failed to mark notification as read. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // Method to get the current user's ID (you need to implement this based on your authentication logic)
    private String getCurrentUserId() {
        // Replace this with your code to retrieve the current user's ID
        return "user_id"; // Dummy user ID, replace this with actual logic
    }
}