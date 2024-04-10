
package com.example.ridepal;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.ridepal.Models.MyData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class CreateRideFragment extends Fragment{
    public static String SHARED_PREFS = "shared-prefs";
    String userid,name;
    private EditText destination, source, phone, carType,seats,dateTime,payment;
    private Button createRideBtn;
    private String formattedDateTime;
    private Date selectedDateTime;
    private FirebaseHelper firebaseHelper;
    MyData data=new MyData();
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=  inflater.inflate(R.layout.fragment_create_ride, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
//        userid = sharedPreferences.getString(USER_ID_KEY,null);
//        name = sharedPreferences.getString(NAME_KEY,null);

//        Retrieve current user name
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

        Toast.makeText(getContext(),"Token is "+ MyData.token,Toast.LENGTH_SHORT);
        Toast.makeText(getContext(),"User Id is "+ MyData.userId,Toast.LENGTH_SHORT);
        phone = view.findViewById(R.id.phone_create_edt);
        source = view.findViewById(R.id.source_edt);
        destination = view.findViewById(R.id.destination_edt);
        seats = view.findViewById(R.id.seats_edt);
        carType = view.findViewById(R.id.car_type_edt);
        dateTime = view.findViewById(R.id.dateTime_Edt);
        payment=view.findViewById(R.id.amount_edt);

        createRideBtn = view.findViewById(R.id.create_ride_btn);
        firebaseHelper = new FirebaseHelper();

        dateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePickerDialog();
            }
        });

        createRideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Source = source.getText().toString();
                String Destination = destination.getText().toString();
                String Phone = phone.getText().toString();
                String CarType = carType.getText().toString();
                int Seats = Integer.parseInt(seats.getText().toString());
                int Payment=Integer.parseInt(payment.getText().toString());
                String rideType = "Single";
                String userId = MyData.userId;
                String fcmToken = MyData.token;


                Map<String, Object> rideData = new HashMap<>();
                rideData.put("userId",userId);
                rideData.put("name",name);
                rideData.put("rideType", rideType);
                rideData.put("source", Source);
                rideData.put("destination", Destination);
                rideData.put("phone_number", Phone);
                rideData.put("date_and_time", String.valueOf(selectedDateTime));
                rideData.put("cartType", CarType);
                rideData.put("payment",Payment);
                rideData.put("seats", Seats);
                rideData.put("token", fcmToken);


                db.collection("users").document(userId).collection("Cars").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Document exists, now check if fields have values
                            String model = document.getString("model");
                            String make = document.getString("make");
                            String plate = document.getString("plate");

                            if (model != null && !model.isEmpty() && make != null && !make.isEmpty() && plate != null && !plate.isEmpty()) {
                                // All fields have values
                                db.collection("rides").document()
                                        .set(rideData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot for create rides successfully written!");
                                                Toast.makeText(getContext(),"Ride Created successfully",Toast.LENGTH_LONG).show();
                                                source.setText("");
                                                destination.setText("");
                                                phone.setText("");
                                                carType.setText("");
                                                payment.setText(" ");
                                                dateTime.setText("");
                                                seats.setText("");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "Error writing document for create rides", e);
                                                Toast.makeText(getContext(),"Error Creating Rides,check your Internet",Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            } else {
                                // Some fields are missing or empty
                                Toast.makeText(getContext(),"Make sure all card details are updated in profile before creating a ride ",Toast.LENGTH_LONG).show();
                                Log.d("RIDE_CREATION", "Make sure all card details are updated in profile before creating a ride");

                            }
                        } else {
                            // Document does not exist

                            Toast.makeText(getContext(),"Add card details in profile before creating a ride",Toast.LENGTH_LONG).show();
                            Log.d("RIDE_CREATION", "Add card details in profile before creating a ride");
                        }
                    } else {
                        Log.d("RIDE_CREATION", "Error getting document: ", task.getException());
                    }
                }
                });
                FCMDataNotificationSender fcmDataNotificationSender = new FCMDataNotificationSender();

                String title = "Create";
                String message = "Ride has been created";

                FCMDataNotificationSender.sendNotification(data.token,title, message);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    private void showDateTimePickerDialog() {
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
                        showTimePickerDialog(c);
                    }
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void showTimePickerDialog(final Calendar calendar) {
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
//                        dateTimeEdt.setText(formattedDateTime);
                    }
                },
                hour, minute, false);

        timePickerDialog.show();
    }
}