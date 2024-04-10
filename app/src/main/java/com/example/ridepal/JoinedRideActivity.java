package com.example.ridepal;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridepal.FCM.FCMSend;
import com.example.ridepal.Helper.Whatsapp;
import com.example.ridepal.Models.MyData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JoinedRideActivity extends AppCompatActivity {
    public TextView driverName;
    public TextView pickupLocation;
    public TextView dropoffLocation;
    public TextView driverPhoneNumber;
    public TextView bookedSeats;
    public TextView pickupTime;
    public ImageView back,whatsapp;
    public Button cancel;
    static RecyclerView recyclerViewPassengers;
    String formattedTime;
    String userId;
    String selectedPickupLocation,rideId;
    long bookedSeatsString,availableSeatsString;
    public  String pickupLocationString,dropoffLocationString,driverPhoneNumberString,receiverToken;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    CreatedFragment createdFragment = new CreatedFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined_ride);

        back = findViewById(R.id.joinedRidesBack);
        driverName = findViewById(R.id.joinedRideDriverNameDetail);
        pickupLocation = findViewById(R.id.joinedRidePickupLocationDetail);
        dropoffLocation = findViewById(R.id.joinedRideDropoffLocationDetail);
        driverPhoneNumber = findViewById(R.id.joinedRideDriverPhoneDetail);
        bookedSeats = findViewById(R.id.joinedRideBookedSeats);
        pickupTime = findViewById(R.id.joinedRidePickupTimeDetail);
        whatsapp = findViewById(R.id.whatsappicon);
        cancel = findViewById(R.id.cancel);

        rideId = getIntent().getStringExtra("documentId");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Whatsapp.sendMessageToWhatsApp(driverPhoneNumberString,"",JoinedRideActivity.this);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                                JoinedRideActivity.this,
                                                userId,
                                                receiverToken,
                                                "Cancelled Ride",
                                                MyData.name + " has cancelled your event from "+ pickupLocationString+" to " +dropoffLocationString
                                        );
                                        long seatsRemaining = availableSeatsString + bookedSeatsString;
                                        updateSeats(seatsRemaining);
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
        });

        db.collection("rides")
                .document(rideId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Document exists, you can retrieve data
                        String driverNameString = documentSnapshot.getString("name");
                        pickupLocationString = documentSnapshot.getString("source");
                        dropoffLocationString = documentSnapshot.getString("destination");
                        driverPhoneNumberString = documentSnapshot.getString("phone_number");
                        int availableSeatsInt = Math.toIntExact(documentSnapshot.getLong("seats"));
                        String pickupTimeString = documentSnapshot.getString("date_and_time");
                        userId = documentSnapshot.getString("userId");
                        availableSeatsString =  documentSnapshot.getLong("seats");

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

//                        updateEventDetails();
                        driverName.setText("Driver Name: " + driverNameString);
                        pickupLocation.setText("Pickup Location: "+pickupLocationString);
                        dropoffLocation.setText("Dropoff Location: "+dropoffLocationString);
                        driverPhoneNumber.setText("Driver PhoneNumber: "+driverPhoneNumberString);
                        pickupTime.setText("Pickup Time: "+formattedTime);

                        // Update your UI or perform other actions with the data
                    } else {
                        // Document does not exist
                        Log.d(TAG, "No such document");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e(TAG, "Error fetching event details ", e);
                });
        db.collection("rides")
                .document(rideId)
                .collection("joinedUsers")
                .document(MyData.userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Document exists, you can retrieve data
                        bookedSeatsString = documentSnapshot.getLong("bookedSeats");
                        bookedSeats.setText("Booked seats: "+bookedSeatsString);
                        // Update your UI or perform other actions with the data
                    } else {
                        Log.d(TAG, "No such document");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e(TAG, "Error fetching event details ", e);
                });
    }

    public void updateSeats(long seatsRemaining){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("rides")
                .document(rideId)
                .update("seats",seatsRemaining )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Handle success
                        Log.d("FIRESTORE_VALUE", "Seats values Updated successfully ");
                        deleteUser(JoinedRideActivity.this);
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

    private void deleteUser(Context context) {
        db.collection("rides")
                .document(rideId)
                .collection("joinedUsers")
                .document(MyData.userId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("FIRESTORE_VALUE", "Ride canceled Successfully");
                        Toast.makeText(context, "Ride canceled Successfully", Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(JoinedRideActivity.this, JoinedRideActivity.class);
//                        startActivityForResult(intent, REQUEST_CODE);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e(TAG, "Error deleting user", e);
                });
    }

}