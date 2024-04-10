package com.example.ridepal.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridepal.Models.Ride;
import com.example.ridepal.R;
import com.example.ridepal.SingleRidesDescription;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {
    private List<Ride> rides;
    private Context context;

    public RideAdapter(Context context, List<Ride> rides) {
        this.context = context;
        this.rides = rides;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ride_item, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rides.get(position);

        holder.textViewDriverName.setText(ride.getName());
        holder.textViewPickup.setText(ride.getSource() + " To " + ride.getDestination());
        holder.textSeats.setText(ride.getSeats()+" Seats");

        SimpleDateFormat firestoreDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.US);
        try {
            Date date = firestoreDateFormat.parse(ride.getDate_and_time());
            SimpleDateFormat desiredDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
            String formattedTime = desiredDateFormat.format(date);
            holder.textViewDateTime.setText(formattedTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle item click
                Intent intent = new Intent(context, SingleRidesDescription.class);
                intent.putExtra("rideId", ride.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    public static class RideViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewDriverName;
        public TextView textViewPickup;
        public TextView textViewDateTime;
        public TextView textSeats;

        public RideViewHolder(View itemView) {
            super(itemView);
            textViewDriverName = itemView.findViewById(R.id.driverNameTxt);
            textViewPickup = itemView.findViewById(R.id.sourceTxt);
            textViewDateTime = itemView.findViewById(R.id.timeTxt);
            textSeats = itemView.findViewById(R.id.seatsTxt);
        }
    }
}