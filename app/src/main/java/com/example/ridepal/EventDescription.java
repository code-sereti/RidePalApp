package com.example.ridepal;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridepal.Adapter.EventPickupDescriptionAdapter;
import com.example.ridepal.FCM.APIService;
import com.example.ridepal.FCM.Client;
import com.example.ridepal.FCM.FCMSend;
import com.example.ridepal.Models.Event;
import com.example.ridepal.Models.EventPickupDetail;
import com.example.ridepal.Models.MyData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDescription extends AppCompatActivity {
    private List<EventPickupDetail> pickupDetailsList;
    private static EventPickupDetail eventPickupDetail;
    private static FirebaseFirestore db;
    public static String receiverToken,eventId,pickupId,selectedPickupLocation;
    public static int availableSeats,selectedSeats;
    public static NumberPicker numberPicker;
    public ImageView back_events;
    public static String eventName,EventPickupLocation;
    public static String userId;
    private APIService apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_description);

        db = FirebaseFirestore.getInstance();

        // Initialize views
        TextView eventNameDetail = findViewById(R.id.eventNameDetail);
        TextView eventLocationDetail = findViewById(R.id.eventLocationDetail);
        TextView organizerPhoneDetail = findViewById(R.id.organizerPhoneDetail);
        TextView rideTypeDetail = findViewById(R.id.rideTypeDetail);
        RecyclerView recyclerViewPickups = findViewById(R.id.recyclerViewPickups);
        Button joinEvent = findViewById(R.id.joinEventBtn);
        back_events=findViewById(R.id.back_events);

        back_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        eventPickupDetail = new EventPickupDetail();

        // Initialize RecyclerView
        pickupDetailsList = new ArrayList<>();
        EventPickupDescriptionAdapter eventPickupDescriptionAdapter = new EventPickupDescriptionAdapter(pickupDetailsList);
        recyclerViewPickups.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPickups.setAdapter(eventPickupDescriptionAdapter);

        // Fetch eventId from intent
        eventId = getIntent().getStringExtra("eventId");

        // Fetch event details from Firestore based on eventId

        FirebaseFirestore.getInstance().collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Map Firestore data to your model class or use directly
                        Event event = documentSnapshot.toObject(Event.class);

                        // Update UI with event details
                        updateUI(event,eventId);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
        joinEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPickupLocationPopup(EventDescription.this,eventId );
                }
            });
    }


    // Update UI with event details
    private void updateUI(Event event,String eventId) {
        TextView eventNameDetail = findViewById(R.id.eventNameDetail);
        TextView eventLocationDetail = findViewById(R.id.eventLocationDetail);
        TextView organizerPhoneDetail = findViewById(R.id.organizerPhoneDetail);
        TextView rideTypeDetail = findViewById(R.id.rideTypeDetail);

        // Set text in TextViews
        eventNameDetail.setText("Event Name: " + event.getEventName());
        eventLocationDetail.setText("Event Location: " + event.getEventLocation());
        organizerPhoneDetail.setText("Organizer's Phone: " + event.getOrganizerPhoneNumber());
        rideTypeDetail.setText("Ride Type: " + event.getRideType());
        userId = event.getUserId();

        eventName= event.getEventName();

        RecyclerView recyclerViewPickups = findViewById(R.id.recyclerViewPickups);

        // Fetch and display pickups data in the RecyclerView
        fetchAndDisplayPickups(eventId, recyclerViewPickups);
    }
    private void fetchAndDisplayPickups(String eventId, RecyclerView recyclerView) {

        FirebaseFirestore.getInstance().collection("events")
                .document(eventId)
                .collection("pickups")
                .whereGreaterThan("availableSeats",0)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Convert query result to list of EventPickupDetail objects
                    List<EventPickupDetail> pickupsList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String pickupId = document.getId();
                        EventPickupDetail pickup = document.toObject(EventPickupDetail.class);
                        pickup.setPickupId(pickupId);
                        pickupsList.add(pickup);
                    }

                    // Create and set adapter for RecyclerView
                    EventPickupDescriptionAdapter eventPickupDescriptionAdapter = new EventPickupDescriptionAdapter(pickupsList);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    recyclerView.setAdapter(eventPickupDescriptionAdapter);
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    private static void showPickupLocationPopup(Context context, String eventId) {
        // Fetch pickup details for the selected event
        db = FirebaseFirestore.getInstance();
        db.collection("events")
                .document(eventId)
                .collection("pickups")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> pickupLocations = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            EventPickupDetail pickupDetail = document.toObject(EventPickupDetail.class);
                            pickupLocations.add(pickupDetail.getPickupLocation());
                        }
                        // Display the pickup locations in a popup with a Spinner
                        showPopup(context, pickupLocations);
                    } else {
                        // Handle errors
                        Log.e("FIRESTORE_VALUE", "Error fetching Pickup Locations");
                    }
                });
    }

    private static void showPopup(Context context, List<String> pickupLocations) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Pickup Location");
        EventPickupDetail eventPickupDetail = new EventPickupDetail();

        // Convert List<String> to String[]
        String[] pickupLocationsArray = pickupLocations.toArray(new String[0]);

        // Set up the layout inflater for the custom view
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_pickup_location, null);
        builder.setView(view);

        numberPicker = view.findViewById(R.id.seats_select_edt);
        numberPicker.setMinValue(1);

        // Fetch available seats when a pickup location is selected
        Spinner pickupSpinner = view.findViewById(R.id.pickupSpinner);
        pickupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedPickupLocation = pickupLocationsArray[position];
                fetchAvailableSeats(eventId, selectedPickupLocation, numberPicker);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, pickupLocationsArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        pickupSpinner.setAdapter(adapter);

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle confirm button click
                selectedSeats = numberPicker.getValue();

                if (selectedSeats > availableSeats) {
                    // Handle the case where the user selects more seats than available
                    Toast.makeText(context, "Selected seats exceed available seats", Toast.LENGTH_SHORT).show();
                }

                String selectedPickupLocation = pickupSpinner.getSelectedItem().toString();

                // Call a method to handle further logic with the selected pickup location
                handleSelectedPickupLocation(context, selectedPickupLocation, selectedSeats);
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
    private static void handleSelectedPickupLocation(Context context,String selectedPickupLocation, int selectedSeats) {
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
                                        MyData.name + " has requested to join your "+eventName+" Event from pickup Location: "+selectedPickupLocation+ "Booked seats : " +selectedSeats
                                );
                                int seatsRemaining = availableSeats - selectedSeats;
                                updateSeats(seatsRemaining,pickupId);
                                saveBookingData(eventId,pickupId,MyData.userId,selectedSeats);
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

    private static void fetchAvailableSeats(String eventId, String pickupLocation, NumberPicker numberPicker) {
        db.collection("events")
                .document(eventId)
                .collection("pickups")
                .whereEqualTo("pickupLocation", pickupLocation)

                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            EventPickupDetail pickupDetail = document.toObject(EventPickupDetail.class);
                            availableSeats = pickupDetail.getAvailableSeats();
                            numberPicker.setMaxValue(availableSeats);
                            numberPicker.setValue(1);  // Set default value to 1
                        }
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        pickupId = document.getId();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e(TAG, "Error fetching available seats ", e);
                });
    }

    public static void updateSeats(int seatsRemaining,String pickupId){
        EventPickupDetail eventPickupDetail = new EventPickupDetail();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .document(eventId)
                .collection("pickups")// Replace with the actual document ID
                .document(pickupId)
                .update("availableSeats", seatsRemaining)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Handle success
                        Log.d("FIRESTORE_VALUE", "Seats has been updated from " + availableSeats+ " to "+selectedSeats);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                        Log.d("FIRESTORE_VALUE", "Failed to update seats"+ e);
                    }
                });
    }

    public static void saveBookingData(String eventId, String pickupId, String userId, int bookedSeats) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a reference to the joinedUsers subcollection
        CollectionReference joinedUsersRef = db.collection("events")
                .document(eventId)
                .collection("pickups")
//                .whereEqualTo("pickupLocation", selectedPickupLocation)
                .document(pickupId)
                .collection("joinedUsers");

        // Create a document for the user in the joinedUsers subcollection
        DocumentReference userDocRef = joinedUsersRef.document(userId);

        // Create a data map to be saved in the document
        Map<String, Object> userData = new HashMap<>();
        // userId
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

}