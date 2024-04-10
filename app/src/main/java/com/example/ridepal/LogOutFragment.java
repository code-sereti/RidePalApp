package com.example.ridepal;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;


public class LogOutFragment extends Fragment {

    Button Confirm,Cancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_log_out, container, false);
        Confirm= view.findViewById(R.id.logout_btn_confirm);
        Cancel=view.findViewById(R.id.logout_btn_cancel);

        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(requireActivity(), "Sign out successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(requireActivity(), StartActivity.class));

            }
        });
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), HomeActivity.class));
            }
        });

        return view;
    }
}