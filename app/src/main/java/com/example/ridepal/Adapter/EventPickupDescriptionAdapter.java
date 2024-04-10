package com.example.ridepal.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridepal.Models.EventPickupDetail;
import com.example.ridepal.R;

import java.util.List;

public class EventPickupDescriptionAdapter extends RecyclerView.Adapter<EventPickupDescriptionAdapter.PickupViewHolder> {
    private List<EventPickupDetail> pickupsList;

    public EventPickupDescriptionAdapter(List<EventPickupDetail> pickupsList) {
        this.pickupsList = pickupsList;
    }

    @NonNull
    @Override
    public PickupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_pickup_item, parent, false);
        return new PickupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PickupViewHolder holder, int position) {
        EventPickupDetail pickup = pickupsList.get(position);

        // Set pickup details in the ViewHolder
        holder.locationTextView.setText("Location: " + pickup.getPickupLocation());
        holder.timeTextView.setText("Time: " + pickup.getPickupTime());
        holder.seatsTextView.setText("Available Seats: " + pickup.getAvailableSeats());
    }

    @Override
    public int getItemCount() {
        return pickupsList.size();
    }

    static class PickupViewHolder extends RecyclerView.ViewHolder {
        TextView locationTextView;
        TextView timeTextView;
        TextView seatsTextView;

        PickupViewHolder(@NonNull View itemView) {
            super(itemView);
            locationTextView = itemView.findViewById(R.id.event_pickup_location_txt);
            timeTextView = itemView.findViewById(R.id.event_pickup_time_txt);
            seatsTextView = itemView.findViewById(R.id.event_pickup_available_seats_txt);
//            locationTextView = itemView.findViewById(R.id.locationTextView);
//            timeTextView = itemView.findViewById(R.id.timeTextView);
//            seatsTextView = itemView.findViewById(R.id.seatsTextView);
        }
    }
//    private List<EventPickupDetail> eventPickupDetailList;
//
//    public EventPickupDescriptionAdapter(List<EventPickupDetail> eventPickupDetailList) {
//        this.eventPickupDetailList = eventPickupDetailList;
//    }
//
//    @NonNull
//    @Override
//    public PickupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_event_description, parent, false);
//        return new PickupViewHolder(view);
//    }
//
//
//
//    @Override
//    public void onBindViewHolder(@NonNull PickupViewHolder holder, int position) {
//        EventPickupDetail pickup = eventPickupDetailList.get(position);
//
//        holder.pickupLocationTextView.setText("Location: " + pickup.getPickupLocation());
//        holder.pickupTimeTextView.setText("Time: " + pickup.getPickupTime());
//        holder.availableSeatsTextView.setText("Seats: " + pickup.getAvailableSeats());
//    }
//
////    @NonNull
////    @Override
////    public PickupDetailsAdapter.PickupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
////        return null;
////    }
//
//    @Override
//    public int getItemCount() {
//        return eventPickupDetailList.size();
//    }
//
//    static class PickupViewHolder extends RecyclerView.ViewHolder {
//        TextView pickupLocationTextView;
//        TextView pickupTimeTextView;
//        TextView availableSeatsTextView;
//
//        PickupViewHolder(@NonNull View itemView) {
//            super(itemView);
//            pickupLocationTextView = itemView.findViewById(R.id.event_pickup_location_txt);
//            pickupTimeTextView = itemView.findViewById(R.id.event_pickup_time_txt);
//            availableSeatsTextView = itemView.findViewById(R.id.event_pickup_available_seats_txt);
//        }
//    }
}
