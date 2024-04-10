package com.example.ridepal;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.os.Bundle;
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

public class UpdateProfile extends AppCompatActivity {

    EditText updateAddress,updatePhone;
    Button updateProfile;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        updateAddress=findViewById(R.id.editText_update_profile_address);
        updatePhone=findViewById(R.id.editText_update_profile_mobile);
        updateProfile=findViewById(R.id.button_update_profile);
        db=FirebaseFirestore.getInstance();

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Address = updateAddress.getText().toString();
                String Phone = updatePhone.getText().toString();
                //String userId = MyData.userId;

                Map<String, Object> updatesProfile = new HashMap<>();
                updatesProfile.put("location",Address);
                updatesProfile.put("phone_number",Phone);

                db.collection("users").document(MyData.userId)
                        .update(updatesProfile)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot  successfully updated");
                                Toast.makeText(UpdateProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                updateAddress.setText("");
                                updatePhone.setText("");

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document users", e);
                                Toast.makeText(UpdateProfile.this, "Error submitting information", Toast.LENGTH_SHORT).show();
                            }
                        });







            }
        });







    }
}