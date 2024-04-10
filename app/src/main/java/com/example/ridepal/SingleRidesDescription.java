package com.example.ridepal;


import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ridepal.FCM.FCMSend;
import com.example.ridepal.Models.MyData;
import com.example.ridepal.Models.Ride;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SingleRidesDescription extends AppCompatActivity {
    public TextView driverName;
    public TextView availableSeats;
    public TextView pickupLocation;
    public TextView dropoffLocation;
    public TextView driverPhoneNumber;
    public TextView pickupTime;
    public ImageView back_rides;
    public Button joinRide;
    public static String userId,source,destination,formattedTime,receiverToken;
    public String rideId;
    public static int selectedSeats,seats;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_rides_description);

//        driverName = findViewById(R.id.driverNameDetail);
//        pickupLocation = findViewById(R.id.pickupLocationDetail);
//        dropoffLocation = findViewById(R.id.dropoffLocationDetail);
//        driverPhoneNumber = findViewById(R.id.driverPhoneDetail);
//        availableSeats = findViewById(R.id.availableSeatsDetail);
//        pickupTIme = findViewById(R.id.pickupTimeDetail);
//        joinRide = findViewById(R.id.joinRideBtn);
        joinRide = findViewById(R.id.joinRideBtn);
        back_rides=findViewById(R.id.rides);

        back_rides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        rideId = getIntent().getStringExtra("rideId");

        // Fetch event details from Firestore based on eventId

        FirebaseFirestore.getInstance().collection("rides")
                .document(rideId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Map Firestore data to your model class or use directly
                        Ride ride = documentSnapshot.toObject(Ride.class);
                        updateUI(ride,rideId);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
        joinRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(SingleRidesDescription.this,rideId);
            }
        });
    }

    private static void showPopup(Context context, String rideId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select number of seats to book");

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_seats_selected, null);
        builder.setView(view);

        NumberPicker numberPicker = view.findViewById(R.id.seatsNumberPicker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(seats); // Set the maximum value to the available seats
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedSeats = numberPicker.getValue();
                // Handle confirm button click
                if (selectedSeats > seats) {
                    // case where the user selects more seats than available
                    Toast.makeText(context, "Selected seats exceed available seats", Toast.LENGTH_SHORT).show();
                } else {

                    // Call a method to handle further logic with the selected number of seats
                    handleSelectedSeats(context, selectedSeats,rideId);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle cancel button click
            }
        });

        builder.show();
    }

    // Add this method to handle further logic with the selected pickup location
    private static void handleSelectedSeats(Context context, int selectedSeats, String rideId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Object receiverUserToken = documentSnapshot.get("fcmToken");
                            if (receiverUserToken != null) {
                                receiverToken = receiverUserToken.toString();
                                FCMSend.pushNotification(
                                        context,
                                        userId,
                                        receiverToken,
                                        "Request to join ride",
                                        MyData.name + " has requested to join your ride of "+ source + " To " + destination + " Seats: "+ selectedSeats
                                );
                                Log.e("TEST",MyData.name + " has requested to join your ride of "+ source + " To " + destination + " Seats: "+ selectedSeats);
                                Log.e("TEST", "SEATS " + seats);
                                Log.e("TEST", "RIDE ID " + rideId);
                                int seatsRemaining = seats - selectedSeats;
                                updateSeats(seatsRemaining,rideId);
                                saveBookingData(rideId,MyData.userId,selectedSeats);
                            } else {
                                Log.e("FIRESTORE_VALUE", "Field 'fieldName' does not exist or is null");
                            }
                        } else {
                            Log.e("FIRESTORE_VALUE", "Document does not exist");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failures
                        Log.e("FIRESTORE_VALUE", "Error getting value from Firestore", e);
                    }
                });

    }


    // Delete user from database


    public static void updateSeats(int seatsRemaining, String rideId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("rides")
                .document(rideId) // Replace with the actual document ID
                .update("seats",seatsRemaining )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Handle success
                        Log.d("FIRESTORE_VALUE", "Seats values Updated successfully ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                        Log.e("FIRESTORE_VALUE", "Field to update seats \n " + e);
                    }
                });
    }


    public static void saveBookingData(String rideId, String userId, int bookedSeats) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a reference to the joinedUsers sub-collection
        CollectionReference joinedUsersRef = db.collection("rides")
                .document(rideId)
                .collection("joinedUsers");

        // Create a document for the user in the joinedUsers sub-collection
        DocumentReference userDocRef = joinedUsersRef.document(userId);

        // Create a data map to be saved in the document
        Map<String, Object> userData = new HashMap<>();
        // UserId here
        userData.put("joined_user",MyData.userId);
        userData.put("name", MyData.name);
        userData.put("bookedSeats", bookedSeats);
        userData.put("confirmed", false); // Initial confirmation status

        // Add the data to the document
        userDocRef.set(userData)
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                    Log.d(TAG, "Booking data saved successfully for user: " + userId);

                    // If needed, you can update the available seats in the pickup location
                    // Example: updateAvailableSeats(eventId, pickupId, bookedSeats);
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e(TAG, "Error saving booking data for user: " + userId, e);
                });
    }

    @SuppressLint("SetTextI18n")
    private void updateUI(Ride ride, String rideId) {
        driverName = findViewById(R.id.driverNameDetail);
        pickupLocation = findViewById(R.id.pickupLocationDetail);
        dropoffLocation = findViewById(R.id.dropoffLocationDetail);
        driverPhoneNumber = findViewById(R.id.driverPhoneDetail);
        availableSeats = findViewById(R.id.availableSeatsDetail);
        pickupTime = findViewById(R.id.pickupTimeDetail);

        String pickupTimeString = ride.getDate_and_time();

        SimpleDateFormat firestoreDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.US);

        try {
            // Parse the Firestore date string into a Date object
            Date date = firestoreDateFormat.parse(pickupTimeString);

            // Define the desired format for the output
            SimpleDateFormat desiredDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

            // Format the Date object into the desired format
            formattedTime = desiredDateFormat.format(date);

            // Now 'formattedTime' contains the time in the desired format

        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the parsing exception
        }

        // Set text in TextViews
        driverName.setText("Driver Name: " + ride.getName());
        pickupLocation.setText("pickup Location: " + ride.getSource());
        dropoffLocation.setText("DropOff Location: " + ride.getDestination());
        driverPhoneNumber.setText("Driver Phone Number: " + ride.getPhone_number());
        availableSeats.setText("Available Seats: " + ride.getSeats());
        pickupTime.setText("Pickup Time: " + formattedTime);
        userId = ride.getUserId();
        source = ride.getSource();
        destination = ride.getDestination();
        seats= ride.getSeats();

    }
}