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

public class JoinedEventActivity extends AppCompatActivity {
    public TextView eventName;
    public TextView eventLocation;
    public TextView organizerPhoneNumber;
    public TextView bookedSeats;
    public TextView pickupTime;
    public TextView pickupLocation;
    public Button cancel;
    public ImageView back,whatsapp;
    static RecyclerView recyclerViewPassengers;
    String formattedTime;
    String selectedPickupLocation;
    static String eventId;
    static String pickupId;
    long bookedSeatsString,availableSeatsString;
    String userId;
    public String organizerPhoneNumberString,receiverToken,pickupLocationString;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    CreatedFragment createdFragment = new CreatedFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined_event);

        back = findViewById(R.id.joinedEventBack);
        eventName = findViewById(R.id.joinedEventNameDetail);
        eventLocation = findViewById(R.id.joinedEventLocationDetail);
        organizerPhoneNumber = findViewById(R.id.organizerPhoneDetail);
        bookedSeats = findViewById(R.id.bookedSeatsDetail);
        pickupTime = findViewById(R.id.joinedPickupTimeDetail);
        pickupLocation = findViewById(R.id.joinedPickupLocationDetail);
        whatsapp = findViewById(R.id.whatsappicon);
        cancel = findViewById(R.id.cancel_ride);

        eventId = getIntent().getStringExtra("documentId");
        pickupId = getIntent().getStringExtra("pickupDocumentId");
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Whatsapp.sendMessageToWhatsApp(organizerPhoneNumberString,"", JoinedEventActivity.this);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
                                                JoinedEventActivity.this,
                                                userId,
                                                receiverToken,
                                                "Cancelled ride",
                                                MyData.name + " has cancelled your event from pickup location "+ pickupLocationString
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

        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Document exists, you can retrieve data
                        String eventNameString = documentSnapshot.getString("eventName");
                        String eventLocationString = documentSnapshot.getString("eventLocation");
                        userId = documentSnapshot.getString("userId");

                        organizerPhoneNumberString = documentSnapshot.getString("organizerPhoneNumber");

                        eventName.setText("Event Name: " + eventNameString);
                        eventLocation.setText("Event Location: "+eventLocationString);
                        organizerPhoneNumber.setText("Organizer PhoneNumber: "+organizerPhoneNumberString);

                    } else {
                        // Document does not exist
                        Log.d(TAG, "No such document");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e(TAG, "Error fetching event details ", e);
                });
        db.collection("events")
                .document(eventId)
                .collection("pickups")
                .document(pickupId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        pickupLocationString = documentSnapshot.getString("pickupLocation");
                        availableSeatsString = documentSnapshot.getLong("availableSeats");
                        String pickupTimeString = documentSnapshot.getString("pickupTime");
//                        int BookedInt = documentSnapshot.getLong("availableSeats").intValue();

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
                        pickupLocation.setText(" "+pickupLocationString);
                        pickupTime.setText(" "+formattedTime);
//                        availableSeats.setText(" "+availableSeats);
                    } else {
                        // Document does not exist
                        Log.d(TAG, "No such document");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e(TAG, "Error fetching event details ", e);
                });

        db.collection("events")
                .document(eventId)
                .collection("pickups")
                .document(pickupId)
                .collection("joinedUsers")
                .document(MyData.userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Document exists, you can retrieve data
                        bookedSeatsString = documentSnapshot.getLong("bookedSeats");
                        bookedSeats.setText(""+bookedSeatsString);
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
        db.collection("events")
                .document(eventId)
                .collection("pickups")
                .document(pickupId)
                .update("availableSeats",seatsRemaining )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Handle success
                        Log.d("FIRESTORE_VALUE", "Seats values Updated successfully ");
                        deleteUser(JoinedEventActivity.this);
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
        db.collection("events")
                .document(eventId)
                .collection("pickups")
                .document(pickupId)
                .collection("joinedUsers")
                .document(MyData.userId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("FIRESTORE_VALUE", "Ride canceled Successfully");
                        Toast.makeText(context, "Ride canceled Successfully", Toast.LENGTH_LONG).show();
//                        JoinedFragment joinedFragment=new JoinedFragment();
//                        getSupportFragmentManager()
//                                .beginTransaction()
//                                .replace(R.id.fragment_container,joinedFragment)
//                                .commit();

                    }

                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e(TAG, "Error deleting user", e);
                });
    }


}