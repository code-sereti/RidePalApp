package com.example.ridepal;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ridepal.Helper.FirebaseHelper;
import com.example.ridepal.Models.Event;
import com.example.ridepal.Models.EventPickupDetail;
import com.example.ridepal.Models.MyData;
import com.example.ridepal.Models.OneEventPickupDetail;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class CreateGroupRideFragment extends Fragment {
    private EditText eventName, eventLocation, phone, pickupLocation,availableSeats,dateTime,eventDateTime;
    private String formattedDateTime,name;
    public boolean pickupAdded = false;
//    private List<EventPickupDetail> pickupDetailsList = new ArrayList<>();
//    private List<OneEventPickupDetail> oneEventPickupDetails = new ArrayList<>();
    private Date selectedDateTime;
    private FirebaseHelper firebaseHelper;
    private FirebaseFirestore db= FirebaseFirestore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_group_ride, container, false);

        phone = view.findViewById(R.id.organizer_phone_number_edt);
        eventLocation = view.findViewById(R.id.event_location_edt);
        eventName = view.findViewById(R.id.event_name_edt);
        availableSeats = view.findViewById(R.id.available_seats_edt);
        pickupLocation = view.findViewById(R.id.pick_up_location_edt);
        dateTime = view.findViewById(R.id.pickup_time_Edt);
        eventDateTime=view.findViewById(R.id.event_time_Edt);
        Button createRideBtn = view.findViewById(R.id.create_ride_btn);
        Button addPickup = view.findViewById(R.id.add_btn);
        firebaseHelper = new FirebaseHelper();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(MyData.userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Get the value of the field named "fieldName"
                            Object myName = documentSnapshot.get("name");
                            if (myName != null) {
                                // Do something with the retrieved value
                                name = myName.toString();
                                Log.d("FIRESTORE_VALUE", "Retrieved value: " + myName.toString());
                            } else {
                                Log.d("FIRESTORE_VALUE", "Field 'fieldName' does not exist or is null");
                            }
                        } else {
                            Log.d("FIRESTORE_VALUE", "Document does not exist");
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

        eventDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePickerDialog(eventDateTime);
            }
        });

        dateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePickerDialog(dateTime);
            }
        });

        // Initialize pickupDetailsList at the beginning of your class
        List<EventPickupDetail> pickupDetailsList = new ArrayList<>();
        List<OneEventPickupDetail> oneEventPickupDetailList = new ArrayList<>();


// AddPickup button click listener
        addPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pickupLocationText = pickupLocation.getText().toString();
                int availableSeatsValue = Integer.parseInt(availableSeats.getText().toString());

                // Create a new PickupDetail object and add it to the list
                EventPickupDetail newEventPickupDetail = new EventPickupDetail(pickupLocationText, availableSeatsValue, String.valueOf(selectedDateTime));
                pickupDetailsList.add(newEventPickupDetail);

                pickupAdded = true;

                pickupLocation.setText("");
                availableSeats.setText("");
                dateTime.setText("");
                eventDateTime.setText(" ");
            }
        });

// CreateRide button click listener
        createRideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventNameText = eventName.getText().toString();
                String eventLocationText = eventLocation.getText().toString();
                String organizerPhoneNumberText = phone.getText().toString();
                String eventDateTimeText=eventDateTime.getText().toString();

                String rideType = "Group";
                String userId = MyData.userId;
                String fcmToken = MyData.token;

                // Create a new Event object
                Event newEvent = new Event(eventNameText, eventLocationText, organizerPhoneNumberText, rideType, userId, fcmToken,eventDateTimeText);

                // Upload event details to Firestore
                DocumentReference eventRef = db.collection("events").document();
                String eventId = eventRef.getId();

                // Create a map for event data
                Map<String, Object> eventData = new HashMap<>();
                eventData.put("eventName", newEvent.getEventName());
                eventData.put("eventLocation", newEvent.getEventLocation());
                eventData.put("organizerPhoneNumber", newEvent.getOrganizerPhoneNumber());
                eventData.put("rideType", newEvent.getRideType());
                eventData.put("userId", newEvent.getUserId());
                eventData.put("fcmToken", newEvent.getFcmToken());
                eventData.put("eventDateTime",newEvent.getEventDateTime());

                String pickupLocationText = pickupLocation.getText().toString();
                String availableSeatsText = availableSeats.getText().toString();
                String dateTimeText = dateTime.getText().toString();



// Check if pickupLocation, availableSeats, and dateTime are not blank
                if (!TextUtils.isEmpty(pickupLocationText) && !TextUtils.isEmpty(availableSeatsText) && !TextUtils.isEmpty(dateTimeText)&& !TextUtils.isEmpty(eventDateTimeText)) {
                    int availableSeatsValue = Integer.parseInt(availableSeatsText);

                    // Create a new PickupDetail object and add it to the list
                    EventPickupDetail newEventPickupDetail = new EventPickupDetail(pickupLocationText, availableSeatsValue, String.valueOf(selectedDateTime));
                    pickupDetailsList.add(newEventPickupDetail);

                    pickupAdded = true;
                    pickupLocation.setText("");
                    availableSeats.setText("");
                    dateTime.setText("");

                } else {
                    // Handle the case where any of the required fields is blank


                    Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                }

                eventRef.set(eventData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // DocumentSnapshot added successfully
                                Log.d("Firestore", "Event data added successfully with ID: " + eventId);

                                // Upload all pickup details for the event
                                CollectionReference pickupsRef = eventRef.collection("pickups");

//                                for (EventPickupDetail pickup : pickupDetailsList) {
//                                    pickupsRef.add(pickup);
//                                }
//                                // Clear pickupDetailsList after successful upload
//                                pickupDetailsList.clear();

                                if (pickupAdded) {
                                    for (EventPickupDetail pickup : pickupDetailsList) {
                                        pickupsRef.add(pickup);
                                    }
                                    Log.d(TAG, "Pickup Data" + pickupDetailsList);
                                    // Clear pickupDetailsList after successful upload
                                    pickupDetailsList.clear();
                                }
//                                else{
//                                    for (OneEventPickupDetail pickup : oneEventPickupDetailList ) {
//                                        pickupsRef.add(pickup);
//                                    }
//                                    // Clear pickupDetailsList after successful upload
//                                    oneEventPickupDetailList.clear();
//                                }

                                eventLocation.setText("");
                                eventName.setText("");
                                phone.setText("");
                                pickupLocation.setText("");
                                availableSeats.setText("");
                                dateTime.setText("");
                                eventDateTime.setText("");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure
                                Log.e("Firestore", "Error adding event data", e);
                            }
                        });

            }
        });

        return view;
    }
    private void showDateTimePickerDialog(EditText edt) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Show DatePickerDialog for date selection
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        c.set(year, monthOfYear, dayOfMonth);
                        showTimePickerDialog(c,edt);
                    }
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void showTimePickerDialog(final Calendar calendar,EditText edt) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Show TimePickerDialog for time selection
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        selectedDateTime = calendar.getTime();

                        // Format the selected date and time
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
                        formattedDateTime = dateFormat.format(selectedDateTime);

                        // Set the formatted date and time to the EditText
                        edt.setText(formattedDateTime);
                    }
                },
                hour, minute, false);

        timePickerDialog.show();
    }
}