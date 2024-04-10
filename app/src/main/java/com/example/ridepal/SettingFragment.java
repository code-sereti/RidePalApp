package com.example.ridepal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ridepal.Models.MyData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class SettingFragment extends Fragment {

    private FirebaseAuth authProfile;
    TextView Edit_info,Edit_car,Edit_contact;
    TextView username,greetings;
    static TextView Name,Phone,Email,Address,Gender,Dob,CarModel,CarMake,CarPlate,Contact_name,Contact_phone,Contact_relationship;
    static ImageView Profile_Image, Back,Notification;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        // Declare variables..... User Information

//        Back= view.findViewById(R.id.settings_back);
        Notification=view.findViewById(R.id.set_notification);
        Edit_info=view.findViewById(R.id.info_edit);
        Edit_contact=view.findViewById(R.id.info_edit_contact);
        Edit_car=view.findViewById(R.id.info_edit_car);
        Name=view.findViewById(R.id.user_nametxt);
        Phone=view.findViewById(R.id.user_numbertxt);
        Email=view.findViewById(R.id.user_emailtxt);
        Address=view.findViewById(R.id.user_address);
        Dob=view.findViewById(R.id.user_dob);
        Gender=view.findViewById(R.id.user_gender);
        Profile_Image=view.findViewById(R.id.image_profile);

        // Declare variables..... Car Information

        CarModel=view.findViewById(R.id.user_car_model);
        CarMake=view.findViewById(R.id.user_car_make);
        CarPlate=view.findViewById(R.id.user_car_plate);


        // Declare variables..... Emergency Contact Information
        Contact_name=view.findViewById(R.id.contact_name);
        Contact_phone=view.findViewById(R.id.contact_phone);
        Contact_relationship=view.findViewById(R.id.contact_relation);
        username = view.findViewById(R.id.username);
        greetings = view.findViewById(R.id.greetings);


        authProfile=FirebaseAuth.getInstance();

        // Get current user....
        FirebaseUser firebaseUser=authProfile.getCurrentUser();
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        String greetingMessage;
        if (hourOfDay >= 0 && hourOfDay < 12) {
            greetingMessage = "Good Morning";
        } else if (hourOfDay >= 12 && hourOfDay < 18) {
            greetingMessage = "Good Afternoon";
        } else {
            greetingMessage = "Good Evening";
        }

        try {
            username.setText(MyData.name);
        }catch (Exception e){
            username.setText(" ");
        }


        greetings.setText(greetingMessage);



        //Navigate to Emergency contact activity..
        Edit_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), EmergencyContacts.class));
            }
        });


        // Navigate to Cars activity...

        Edit_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), Cars.class));
            }
        });

        // Edit Personal Information...
        Edit_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), UpdateProfile.class));
            }
        });

        Notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireActivity(), Notifications.class));
            }
        });
        // Change profile Image..
        Profile_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), ImageUploadActivity.class));
            }
        });

        //Check whether current user is null or not...
        if (firebaseUser==null){
            Toast.makeText(getContext(), "Something went wrong..", Toast.LENGTH_SHORT).show();

        }else {


            try {
                getUserData(firebaseUser);
                getCarInfo(firebaseUser);
                getContactInfo(firebaseUser);

            }catch (Exception e){
                Log.e("FIRESTORE_VALUE", "Network Problems"+e);
            }


        }

        return view;
    }
    // Method to get user emergency contact

    private void getContactInfo(FirebaseUser firebaseUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(MyData.userId).collection("Contacts").document(MyData.userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Object name = documentSnapshot.get("contact_name");
                            Object phone = documentSnapshot.get("contact_phone");
//                            Object relationship = documentSnapshot.get("contact_relationship");

                            // Retrieve  data from firebase
                            Contact_name.setText(" "+name);
                            Contact_phone.setText("+254"+phone);
//                            Contact_relationship.setText(" "+relationship);

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

    // Method to get user car information
    private void getCarInfo(FirebaseUser firebaseUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(MyData.userId).collection("Cars").document(MyData.userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Object model = documentSnapshot.get("model");
                            Object make = documentSnapshot.get("make");
                            Object plate = documentSnapshot.get("plate");

                            // Retrieve  data from firebase
                            CarModel.setText(" "+model);
                            CarMake.setText(" "+make);
                            CarPlate.setText(" "+plate);

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

    // Method to get user information
    private void getUserData(FirebaseUser firebaseUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(MyData.userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Object username = documentSnapshot.get("name");
                            Object email = documentSnapshot.get("email");
                            Object phone = documentSnapshot.get("phone_number");
                            Object gender = documentSnapshot.get("gender");
                            Object location = documentSnapshot.get("location");
                            Object dob = documentSnapshot.get("dob");

                            // Retrieve  data from firebase
                            Name.setText(" "+username);
                            Phone.setText("+254"+phone);
                            Email.setText(" "+email);
                            Address.setText(" "+location);
                            Dob.setText(" "+dob);
                            Gender.setText(" "+gender);


                            //Set profile pic as uploaded
                            Uri uri=firebaseUser.getPhotoUrl();
                            Picasso.get().load(uri).into(Profile_Image);

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

}