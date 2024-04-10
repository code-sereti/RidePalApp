package com.example.ridepal;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridepal.Adapter.PassengerAdapter;
import com.example.ridepal.Models.Passenger;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventPassengersActivity extends AppCompatActivity {
    public TextView eventName;
    public TextView eventLocation;
    public TextView organizerPhoneNumber;
    public TextView availableSeats;
    public TextView pickupTime;
    public ImageView back;
    Spinner pickupLocation;
    static RecyclerView recyclerViewPassengers;
    String formattedTime;
    String selectedPickupLocation,eventId,pickupId;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    CreatedFragment createdFragment = new CreatedFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_passengers);

        back = findViewById(R.id.createdBack);
        eventName = findViewById(R.id.eventNameDetail);
        eventLocation = findViewById(R.id.eventLocationDetail);
        organizerPhoneNumber = findViewById(R.id.organizerPhoneDetail);
        availableSeats = findViewById(R.id.availableSeatsDetail);
        pickupTime = findViewById(R.id.pickupTimeDetail);
        pickupLocation = findViewById(R.id.pickupLocationDetailSpinner);
        recyclerViewPassengers = findViewById(R.id.recyclerViewPassengers);

        eventId = getIntent().getStringExtra("documentId");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
                        String organizerPhoneNumberString = documentSnapshot.getString("organizerPhoneNumber");

//                        updateEventDetails();
                        eventName.setText("Event Name: " + eventNameString);
                        eventLocation.setText("Event Location: "+eventLocationString);
                        organizerPhoneNumber.setText("Organizer PhoneNumber: "+organizerPhoneNumberString);

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

        db.collection("events")
                .document(eventId)
                .collection("pickups")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<String> pickupLocationsList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String pickupLocation = document.getString("pickupLocation");
                            pickupLocationsList.add(pickupLocation);
                        }
                        // Update the Spinner with the fetched pickup locations
                        updatePickupLocationsSpinner(pickupLocationsList);
                    } else {
                        // Handle case where no pickup documents match the query
                        Log.d(TAG, "No pickups found for eventId: " + eventId);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e(TAG, "Error fetching pickup locations ", e);
                });

        // Set up an item selected listener for the Spinner
        pickupLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Fetch passengers when a pickup location is selected
                selectedPickupLocation = pickupLocation.getSelectedItem().toString();
                fetchPassengers(eventId, selectedPickupLocation);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle case where nothing is selected (if needed)
            }
        });

    }
    private void updatePickupLocationsSpinner(List<String> pickupLocationsList) {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pickupLocationsList);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        pickupLocation.setAdapter(adapter);
    }
    private void fetchPassengers(String eventId, String pickupLocation) {
        List<Passenger> passengersList = new ArrayList<>();

        db.collection("events")
                .document(eventId)
                .collection("pickups")
                .whereEqualTo("pickupLocation", pickupLocation)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            // Assuming you have a subcollection named "joinedUsers" under each pickup
                            CollectionReference joinedUsersRef = document.getReference().collection("joinedUsers");

                            String pickupTime = document.getString("pickupTime");
                            int availableSeats = document.getLong("availableSeats").intValue();
                            pickupId = document.getId();


                            SimpleDateFormat firestoreDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.US);

                            try {
                                // Parse the Firestore date string into a Date object
                                Date date = firestoreDateFormat.parse(pickupTime);

                                // Define the desired format for the output
                                SimpleDateFormat desiredDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

                                // Format the Date object into the desired format
                                formattedTime = desiredDateFormat.format(date);

                                // Now 'formattedTime' contains the time in the desired format

                            } catch (ParseException e) {
                                e.printStackTrace();
                                // Handle the parsing exception
                            }

                            // Update UI with additional event information
                            updateEventDetails(formattedTime, availableSeats);

                            // Fetch passengers
                            joinedUsersRef.get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot userDocument : task.getResult()) {
                                        Passenger passenger = userDocument.toObject(Passenger.class);
                                        String passengerId = userDocument.getId();
                                        // Set the document ID to the Passenger object
                                        passenger.setDocumentId(eventId);
                                        passenger.setUserId(passengerId);
                                        passenger.setPickupId(pickupId);
                                        passenger.setType("event");
                                        passengersList.add(passenger);

                                    }
                                    // Display the list of passengers in a RecyclerView
                                    displayPassengers(passengersList);
                                } else {
                                    // Handle errors for joinedUsers query
                                    Log.e(TAG, "Error getting joinedUsers: ", task.getException());
                                }
                            });
                        }
                    } else {
                        // Handle case where no pickup documents match the query
                        Log.d(TAG, "No pickups found for location: " + pickupLocation);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e(TAG, "Error fetching passengers ", e);
                });
    }

    private void updateEventDetails(String pickupTime, int availableSeats) {
        // Update the UI with the additional event information
        this.pickupTime.setText(" "+pickupTime);
        this.availableSeats.setText(" "+availableSeats);
        // You might also want to update other UI elements based on your needs
    }


    private void displayPassengers(List<Passenger> passengersList) {

        // For example, assuming you have a PassengerAdapter
        PassengerAdapter adapter = new PassengerAdapter(passengersList,EventPassengersActivity.this);
        recyclerViewPassengers.setLayoutManager(new LinearLayoutManager(recyclerViewPassengers.getContext()));
        recyclerViewPassengers.setAdapter(adapter);
    }
    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy - HH:mm");
        return dateFormat.format(date);
    }
}