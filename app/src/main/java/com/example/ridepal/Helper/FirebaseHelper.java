package com.example.ridepal.Helper;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseHelper {

    private FirebaseFirestore db;

    public FirebaseHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public void addRide(String driverName, String source, String destination, String dateTime) {

    }
}
