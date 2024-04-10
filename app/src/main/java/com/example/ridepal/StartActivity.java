package com.example.ridepal;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ridepal.Models.MyData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class StartActivity extends AppCompatActivity {
    public Button register;
    public TextView login;
    public String userid,name,fcmToken;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseUser currentUser = mAuth.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        register = (Button)findViewById(R.id.get_started_btn);
        login = (TextView)findViewById(R.id.start_login);

        if (currentUser != null) {
            // User is signed in, continue to the app
            userid = currentUser.getUid();
            // Get the document reference for the user
            DocumentReference docRef = db.collection("users").document(userid);
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(taskToken -> {
                        if (taskToken.isSuccessful()) {
                            fcmToken = taskToken.getResult();
                            updateFCMTokenInFirestore(fcmToken);
                        } else {
                            // Handle token retrieval error
                            Exception exception = taskToken.getException();
                            if (exception != null) {
                                // Handle the exception
                                Log.e(TAG, "FCM token retrieval failed: " + exception.getMessage());
                            }
                        }
                    });
// Fetch the name of the current user
            docRef.get().addOnCompleteListener(tank -> {
                if (tank.isSuccessful()) {
                    DocumentSnapshot document = tank.getResult();
                    if (document.exists()) {
                        // DocumentSnapshot exists, retrieve data
                        name = document.getString("name");
                        // Use retrieved data (username, age, etc.)
                        // Example: Log the retrieved data
                        MyData.name = name;
                        MyData.userId = userid;
                        Log.d("FirestoreData", "Received name is " + MyData.name);
                    } else {
                        // Document does not exist
                        Log.e("FirestoreData", "No such document");
                    }
                } else {
                    // Task failed with an exception
                    Log.d("FirestoreData", "Task failed: " + tank.getException());
                }
            });
            Intent register = new Intent(StartActivity.this,HomeActivity.class);
            startActivity(register);
        } else {
            // User is not signed in, redirect to the login screen
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent register = new Intent(StartActivity.this,RegisterActivity.class);
                    startActivity(register);
                }
            });
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent login = new Intent(StartActivity.this,LoginActivity.class);
                    startActivity(login);
                }
            });
        }
    }

    private void updateFCMTokenInFirestore(String fcmToken) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userid = currentUser.getUid();

            // Update the FCM token in Firestore
            db.collection("users").document(userid)
                    .update("fcmToken", fcmToken)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(StartActivity.this, "Update Successful: ", Toast.LENGTH_SHORT).show();
                        MyData.token=fcmToken;
                        // FCM token updated successfully
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Update failed: " + e.getMessage());
                        // Failed to update FCM token
                        // Handle the error
                    });
        }
    }
}