package com.example.ridepal;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ridepal.Models.MyData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EmergencyContacts extends AppCompatActivity {

    EditText contactName,contactPhone,contactRelation;
    Button submit;
    MyData data=new MyData();
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

        contactName=findViewById(R.id.editText_first_contact);
        contactPhone=findViewById(R.id.editText_first_contact_phone);
        submit=findViewById(R.id.button_submit_contact);
        db = FirebaseFirestore.getInstance(); // Initialize Firestore

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Name = contactName.getText().toString();
                String  Phone = contactPhone.getText().toString();
                String userId = MyData.userId;

                // Make sure user fills all details..

                if (TextUtils.isEmpty(Name)){
                    contactName.setError("Emergency  contact name cannot field be empty ");
                }else if (TextUtils.isEmpty(Phone)){
                    contactPhone.setError("Emergency  contact phone cannot  field be empty ");
                }else {

                    Map<String, Object> contacts= new HashMap<>();
                    contacts.put("userId",userId);
                    contacts.put("contact_name",Name);
                    contacts.put("contact_phone",Phone);



                    db.collection("users").document(MyData.userId).collection("Contacts").document(MyData.userId)
                            .set(contacts)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot for emergency successfully written!");
                                    Toast.makeText(EmergencyContacts.this, " Emergency contacts Information submitted successfully", Toast.LENGTH_SHORT).show();

                                    contactName.setText("");
                                    contactPhone.setText("");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document for contacts", e);
                                    Toast.makeText(EmergencyContacts.this, "Error submitting emergency", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });

    }
}