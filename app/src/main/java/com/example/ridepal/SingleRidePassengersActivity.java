package com.example.ridepal;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridepal.Adapter.PassengerAdapter;
import com.example.ridepal.Models.Passenger;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SingleRidePassengersActivity extends AppCompatActivity {
    public TextView driverName;
    public TextView pickupLocation;
    public TextView dropoffLocation;
    public TextView driverPhoneNumber;
    public TextView availableSeats;
    public TextView pickupTime;
    public ImageView back;
    static RecyclerView recyclerViewPassengers;
    String formattedTime;
    String selectedPickupLocation,rideId;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    CreatedFragment createdFragment = new CreatedFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_ride_passengers);

        back = findViewById(R.id.createdBack);
        driverName = findViewById(R.id.driverNameDetail);
        pickupLocation = findViewById(R.id.pickupLocationDetail);
        dropoffLocation = findViewById(R.id.dropoffLocationDetail);
        driverPhoneNumber = findViewById(R.id.driverPhoneDetail);
        availableSeats = findViewById(R.id.availableSeatsDetail);
        pickupTime = findViewById(R.id.pickupTimeDetail);
        recyclerViewPassengers = findViewById(R.id.recyclerViewSingleRidePassengers);

        rideId = getIntent().getStringExtra("documentId");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              onBackPressed();

            }
        });

        db.collection("rides")
                .document(rideId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Document exists, you can retrieve data
                        String driverNameString = documentSnapshot.getString("name");
                        String pickupLocationString = documentSnapshot.getString("source");
                        String dropoffLocationString = documentSnapshot.getString("destination");
                        String driverPhoneNumberString = documentSnapshot.getString("phone_number");
                        int availableSeatsInt = Math.toIntExact(documentSnapshot.getLong("seats"));
                        String pickupTimeString = documentSnapshot.getString("date_and_time");


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
                        availableSeats.setText("Available Seats: "+availableSeatsInt);
                        pickupTime.setText("Pickup Time: "+formattedTime);

                        fetchPassengers(rideId);

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
    }

    private void fetchPassengers(String rideId) {
        List<Passenger> passengersList = new ArrayList<>();

        db.collection("rides")
                .document(rideId)
                .collection("joinedUsers")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot userDocument : queryDocumentSnapshots) {
                            Passenger passenger = userDocument.toObject(Passenger.class);
                            passenger.setDocumentId(rideId);
                            passenger.setUserId(userDocument.getId());
                            passenger.setType("single");
                            passengersList.add(passenger);
                        }
                        // Display the list of passengers in a RecyclerView
                        displayPassengers(passengersList);
                    } else {
                        // Handle the case where no joined users are found
                        Log.e(TAG, "No joined users found for eventId: " + rideId);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e(TAG, "Error fetching joined users ", e);
                });
    }

    private void displayPassengers(List<Passenger> passengersList) {

        // For example, assuming you have a PassengerAdapter
        PassengerAdapter adapter = new PassengerAdapter(passengersList,SingleRidePassengersActivity.this);
        recyclerViewPassengers.setLayoutManager(new LinearLayoutManager(recyclerViewPassengers.getContext()));
        recyclerViewPassengers.setAdapter(adapter);
    }
}