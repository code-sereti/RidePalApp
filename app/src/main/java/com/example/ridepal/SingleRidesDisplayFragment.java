package com.example.ridepal;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridepal.Adapter.RideAdapter;
import com.example.ridepal.Models.Ride;
import com.google.android.material.search.SearchView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class SingleRidesDisplayFragment extends Fragment {

    private RecyclerView recyclerViewRides;
    private ImageView notification;
    private RideAdapter rideAdapter;
    private SearchView search;
    Toolbar toolbar;
    Menu menu;
    private List<Ride> rides;
    private FirebaseFirestore db;

    public SingleRidesDisplayFragment() {
        // Required empty public constructor
    }


    public static SingleRidesDisplayFragment newInstance(String param1, String param2) {
        SingleRidesDisplayFragment fragment = new SingleRidesDisplayFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rides, container, false);

        recyclerViewRides = view.findViewById(R.id.recyclerViewRides);
        notification = view.findViewById(R.id.notification);
//        search = view.findViewById(R.id.search);

        rides = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), Notifications.class));
            }
        });

        rideAdapter = new RideAdapter(getContext(),rides);
        recyclerViewRides.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        recyclerViewRides.setAdapter(rideAdapter);

        // Query Firestore for available rides and update the RecyclerView
        db.collection("rides")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        // Handle the error
                        return;
                    }

                    for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                        DocumentSnapshot document = documentChange.getDocument();
                        Ride ride = document.toObject(Ride.class);
                        ride.setId(document.getId());

                        String rideId = document.getId();
                        ride.setRideId(rideId);

                        switch (documentChange.getType()) {
                            case ADDED:
                                rides.add(0, ride);
                                break;
                            case MODIFIED:
                                int index = findRideIndex(ride);
                                if (index >= 0) {
                                    rides.set(index, ride);
                                }
                                break;
                            case REMOVED:
                                int removeIndex = findRideIndex(ride);
                                if (removeIndex >= 0) {
                                    rides.remove(removeIndex);
                                }
                                break;
                        }
                    }
                    rideAdapter.notifyDataSetChanged();
                });

        return view;
    }
    private int findRideIndex(Ride ride) {
        for (int i = 0; i < rides.size(); i++) {
            if (rides.get(i).getId().equals(ride.getId())) {
                return i;
            }
        }
        return -1;
    }
}