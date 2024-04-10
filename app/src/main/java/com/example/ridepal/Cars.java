package com.example.ridepal;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

public class Cars extends AppCompatActivity {

    EditText model,make,plate;
    Button Submit;
    CheckBox terms;

    MyData data=new MyData();
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars);

        model=findViewById(R.id.name_car_model_edt);
        make=findViewById(R.id.car_make_edt);
        plate=findViewById(R.id.car_plate_edt);
        Submit=findViewById(R.id.submit_btn);
        terms=findViewById(R.id.checkBox_car_terms);


        db = FirebaseFirestore.getInstance(); // Initialize Firestore
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!terms.isChecked()) {
                    // If not checked, show an alert dialog
                    showTermsErrorDialog();
                } else {
                    // Proceed with the next steps
                    String Model = model.getText().toString();
                    String Make = make.getText().toString();
                    String Plate=plate.getText().toString();
                    String userId = MyData.userId;


                    Map<String, Object> carData = new HashMap<>();
                    carData.put("userId",userId);
                    carData.put("model",Model);
                    carData.put("make",Make);
                    carData.put("plate",Plate);

                    db.collection("users").document(MyData.userId).collection("Cars").document(MyData.userId)
                            .set(carData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot for create rides successfully written!");
                                    Toast.makeText(Cars.this, "Car Information submitted successfully", Toast.LENGTH_SHORT).show();
                                    model.setText("");
                                    make.setText("");
                                    plate.setText("");

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document for create rides", e);
                                    Toast.makeText(Cars.this, "Error submitting car information", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }
        });
    }

    private void showTermsErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Please agree with the terms and conditions to continue.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}