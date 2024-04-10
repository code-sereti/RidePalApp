package com.example.ridepal.Adapter;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridepal.EventDescription;
import com.example.ridepal.FCM.APIService;
import com.example.ridepal.FCM.Client;
import com.example.ridepal.Models.Event;
import com.example.ridepal.Models.EventPickupDetail;
import com.example.ridepal.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
public class GroupRideAdapter extends RecyclerView.Adapter<GroupRideAdapter.GroupRideViewHolder> {
    private static List<Event> events;
    private List<EventPickupDetail> eventPickupDetails;
    private static FirebaseFirestore db;
    public static String receiverToken;
    public static String userId;
    private APIService apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

    private static Context context; // Add a context variable

    public GroupRideAdapter(Context context, List<Event> groupRides) {
        this.context = context;
        this.events = groupRides;
    }
    @NonNull
    @Override
    public GroupRideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_ride_item, parent, false);
        return new GroupRideViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull GroupRideViewHolder holder, int position) {
        Event event = events.get(position);
        holder.eventNameTextView.setText("" + event.getEventName());
        holder.pickupLocationTextView.setText("Event Location: " + event.getEventLocation());
        holder.eventDateTime.setText(""+event.getEventDateTime());

        holder.seatsTextView.setVisibility(View.INVISIBLE);

        // Fetch pickup details for the current event
        fetchPickupDetails(event, holder);

        userId = event.getUserId();

        // Capture position for later use
        int itemPosition = holder.getAdapterPosition();

        // Set click listener on the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start EventDescription activity when the item is clicked
                Intent intent = new Intent(context, EventDescription.class);
                intent.putExtra("eventId", events.get(itemPosition).getId());
                view.getContext().startActivity(intent);
                Log.d(TAG, "EventId before intent is: " + events.get(itemPosition).getId());
            }
        });
    }



    // Fetch pickup details for the given event
    private void fetchPickupDetails(Event event, GroupRideViewHolder holder) {
        db = FirebaseFirestore.getInstance();
        db.collection("events")
                .document(event.getId())  // Use the event's ID to get the document
                .collection("pickups")   // Navigate to the "pickups" collection
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<EventPickupDetail> pickupDetails = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Convert each document to EventPickupDetail
                            EventPickupDetail pickupDetail = document.toObject(EventPickupDetail.class);
                            pickupDetails.add(pickupDetail);
                        }

                        // Display pickup details if available
                        if (!pickupDetails.isEmpty()) {
                            EventPickupDetail firstPickup = pickupDetails.get(0); // Assuming you're displaying the first pickup for simplicity
//                            holder.pickupLocationTextView.setText("Location: " + firstPickup.getPickupLocation());
                            // Add more details as needed
                        } else {
                            holder.pickupLocationTextView.setText("No Pickup Details Available");
                        }
                    } else {
                        // Handle errors
                        holder.pickupLocationTextView.setText("Error fetching Pickup Details");
                    }
                });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
    static class GroupRideViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;
        TextView pickupLocationTextView;
        TextView seatsTextView;
        TextView eventDateTime;
        Button joinEvent;

        GroupRideViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.eventNameTxt);
            pickupLocationTextView = itemView.findViewById(R.id.eventLocationTxt);
            seatsTextView = itemView.findViewById(R.id.seatsTxt);
            eventDateTime=itemView.findViewById(R.id.timeTxt);
            joinEvent = itemView.findViewById(R.id.joinEventBtn);
        }
    }

}