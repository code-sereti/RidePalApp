package com.example.ridepal.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridepal.Models.Notification;
import com.example.ridepal.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notifications;
    private OnItemClickListener listener;
    private static Context context;


    public NotificationAdapter(Context context, List<Notification> notifications) {
        NotificationAdapter.context = context;
        this.notifications = notifications;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView messageTextView;
        TextView timestampTextView;
        LinearLayout notificationLayout;

        public NotificationViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            notificationLayout = itemView.findViewById(R.id.notificationLayout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification currentNotification = notifications.get(position);

        holder.titleTextView.setText(currentNotification.getTitle());
        holder.messageTextView.setText(currentNotification.getMessage());

        Date timestampDate = currentNotification.getTimestamp().toDate();

        // Format the Date to String
        String formattedTimestamp = formatDate(timestampDate);

        // Set the formatted timestamp in the TextView
        holder.timestampTextView.setText(formattedTimestamp);

        // Change background color based on read state
        if (currentNotification.isRead()) {
            holder.notificationLayout.setBackgroundColor(context.getColor(R.color.whiteCardColor));
        } else {
            holder.notificationLayout.setBackgroundColor(context.getColor(R.color.light_grey));
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy - HH:mm");
        return dateFormat.format(date);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }
}
