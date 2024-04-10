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
import com.example.ridepal.Models.Event;
import com.example.ridepal.Models.MyData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class CreatedEventsHistory extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_created_events_history, container, false);

        String userId = MyData.userId;

        RecyclerView recyclerView = view.findViewById(R.id.createdRecyclerview);
        List<CreatedData> createdDataList = new ArrayList<>();

// Reference to the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            CreatedData createdData = new CreatedData();
                            createdData.setDocumentId(document.getId());
                            createdData.setType("event");
                            createdData.setName(event.getEventName());
                            createdData.setLocationOrSource(event.getEventLocation());
                            createdData.setPhoneNumberOrDestination(event.getOrganizerPhoneNumber());
                            createdDataList.add(createdData);
                        }

                        // After retrieving events, query for rides created by the user

                    } else {
                        // Handle errors for events query
                        Log.e("FIRESTORE_DATA", "Error getting events: ", task.getException());
                    }
                    // Set up the RecyclerView with the combined items
                    CreatedAdapter adapter = new CreatedAdapter(createdDataList, getContext());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                });

        return view;
    }
}