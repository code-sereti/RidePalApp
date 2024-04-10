package com.example.ridepal;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridepal.Adapter.CreatedAdapter;
import com.example.ridepal.Models.CreatedData;
import com.example.ridepal.Models.MyData;
import com.example.ridepal.Models.Ride;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CreatedRidesHistory extends Fragment {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_created_rides_history, container, false);

        String userId = MyData.userId;

        RecyclerView recyclerView = view.findViewById(R.id.createdRecyclerview);
        List<CreatedData> createdDataList = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("rides")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task1.getResult()) {
                            Ride ride = document.toObject(Ride.class);
                            CreatedData createdData = new CreatedData();
                            createdData.setDocumentId(document.getId());
                            createdData.setType("ride");
                            createdData.setName(ride.getName());
                            createdData.setLocationOrSource(ride.getSource());
                            createdData.setPhoneNumberOrDestination(ride.getDestination());
                            createdDataList.add(createdData);
                        }

                        // Set up the RecyclerView with the combined items
                        CreatedAdapter adapter = new CreatedAdapter(createdDataList, getContext());
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    } else {
                        // Handle errors for rides query
                        Log.e("FIRESTORE_DATA", "Error getting rides: ", task1.getException());
                    }
                });


        return  view;
    }
}