package com.example.ridepal;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridepal.Adapter.JoinedAdapter;
import com.example.ridepal.Models.JoinedData;
import com.example.ridepal.Models.MyData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class JoinedRides extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView recyclerView;
    List<JoinedData> joinedDataList = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view=inflater.inflate(R.layout.fragment_joined_rides, container, false);

        String userId = MyData.userId;

        recyclerView = view.findViewById(R.id.joinedRecyclerview);

        CollectionReference ridesCollectionRef = db.collection("rides");


        ridesCollectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot rideDoc : task.getResult()) {
                        // Get a reference to the "pickups" subcollection for each "event" document
                        CollectionReference joinedUsersCollectionRef = rideDoc.getReference().collection("joinedUsers");
                        joinedUsersCollectionRef.whereEqualTo("joined_user", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> joinedUsersTask2) {
                                Log.d("JoinedUsers", "UserId: " + joinedUsersTask2.getResult().isEmpty());
                                if (joinedUsersTask2.isSuccessful() && !joinedUsersTask2.getResult().isEmpty()) {

                                    for (DocumentSnapshot joinedUserDoc : joinedUsersTask2.getResult()) {
                                        JoinedData joinedData = new JoinedData();
                                        joinedData.setDocumentId(rideDoc.getId());
                                        joinedData.setType("ride");
                                        joinedData.setName(rideDoc.getString("name"));
                                        joinedData.setLocationOrSource(rideDoc.getString("source"));
                                        joinedData.setPhoneNumberOrDestination(rideDoc.getString("destination"));
                                        joinedDataList.add(joinedData);
                                    }

                                } else {
                                    Log.d("JoinedUsers", "User doesn't exist for ride: " + rideDoc.getId());
                                }
                                JoinedAdapter adapter = new JoinedAdapter(joinedDataList, getContext());
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                            }
                        });
                    }
                } else {
                    Log.d("Firestore", "Error getting events documents: ", task.getException());
                }
            }
        });

        return view;

    }
}