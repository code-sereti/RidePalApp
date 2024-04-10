package com.example.ridepal;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.MotionEffect;

import com.example.ridepal.Helper.ValidationHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    String fcmToken;
    EditText name,email,phone,password,confirmPassword,location,dob;
    RadioGroup radioButtonGroupGender;
    RadioButton radioButtonGroupGenderSelected;
    CheckBox terms;
    Button register;
//    CheckBox Info_terms;
    TextView login;
    boolean isAllFieldsChecked = true;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        name = (EditText) findViewById(R.id.name_reg_edt);
        email = (EditText) findViewById(R.id.email_reg_edt);
        phone = (EditText) findViewById(R.id.phone_reg_edt);
        password = (EditText) findViewById(R.id.password_reg_edt);
        confirmPassword = (EditText) findViewById(R.id.confirm_password_reg_edt);
        location=(EditText) findViewById(R.id.address_reg_edt);
        dob=(EditText)findViewById(R.id.dob_reg_edt);
        radioButtonGroupGender=(RadioGroup)findViewById(R.id.radio_group_register_gender);
        radioButtonGroupGender.clearCheck();
        register = (Button)findViewById(R.id.register_btn);
        login = (TextView) findViewById(R.id.login_txt);
//        Info_terms=findViewById(R.id.checkBox_terms);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ValidationHelper validationHelper = new ValidationHelper();
                boolean allowSave = true;

                int selectedGenderId=radioButtonGroupGender.getCheckedRadioButtonId();
                radioButtonGroupGenderSelected=findViewById(selectedGenderId);

                String nameString = name.getText().toString();
                String emailString = email.getText().toString();
                String phoneString = phone.getText().toString();
                String genderString = null;
                String dobString = dob.getText().toString();
                String locationString=location.getText().toString();
                String passwordString = password.getText().toString();
                String confirmPasswordString = confirmPassword.getText().toString();

                if (validationHelper.isNullOrEmpty(nameString)){
                    name.setError("Name cannot be empty");

                    allowSave = false;
                    return;
                }

                if (validationHelper.isNullOrEmpty(emailString)){
                    email.setError("Email cannot be empty");
                    allowSave = false;
                    return;
                }
                if (validationHelper.isNullOrEmpty(phoneString)){
                    phone.setError("Phone number cannot be empty");
                    allowSave = false;
                    return;
                }
                if (validationHelper.isNullOrEmpty(passwordString)){
                    password.setError("Password cannot be empty");
                    allowSave = false;
                    return;
                }

                if (validationHelper.isValidPhoneNumber(phoneString)){
                    phone.setError("Enter a valid phone number");
                    allowSave = false;
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()){
                    email.setError("Enter a valid email");
                    return;
                }
                if (validationHelper.isEqualPassword(passwordString,confirmPasswordString)){
                    confirmPassword.setError("Passwords do not match");
                    allowSave = false;
                    return;
                }else if (TextUtils.isEmpty(locationString)){
                    location.setError("Location cannot be empty");

                }else if (TextUtils.isEmpty(dobString)){
                    dob.setError("Date of birth cannot be empty");

                }else if (radioButtonGroupGender.getCheckedRadioButtonId()==-1){
                    radioButtonGroupGenderSelected.setError("Select your gender");
                    
                } else
                    genderString=radioButtonGroupGenderSelected.getText().toString();

                register(nameString,emailString,phoneString,passwordString,dobString,genderString,locationString);
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(taskToken -> {
                            if (taskToken.isSuccessful()) {
                                fcmToken = taskToken.getResult();
                            } else {
                                // Handle token retrieval error
                                Exception exception = taskToken.getException();
                                if (exception != null) {
                                    // Handle the exception
                                    Log.e(MotionEffect.TAG, "FCM token retrieval failed: " + exception.getMessage());
                                }
                            }
                        });

//                sendRideJoinRequestNotification();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }






    public void register(String name,String email,String phone,String password,String dob,String gender,String location){
        Log.d(TAG, "SignUp");
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {


                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Objects.requireNonNull(mAuth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Registration successfull,check your email for verification", Toast.LENGTH_SHORT).show();
                                        mAuth.getCurrentUser().sendEmailVerification();
                                        FirebaseUser currentUser = mAuth.getCurrentUser();
                                        String userId = currentUser.getUid();
                                        Log.d(TAG, "create user : Success");

                                        saveToFirestore(name,location,gender,phone,email,dob,userId);
                                    }
                                    else{
                                        Log.d(TAG, task.getException().toString());
                                        Toast.makeText(RegisterActivity.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
//                                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        else{
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Log.d(TAG, "User already registered");
                                Toast.makeText(RegisterActivity.this, "You are already Registered", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Log.d(TAG, "Auth failed" + task.getException());
                                Toast.makeText(RegisterActivity.this, "Authentication failed "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void saveToFirestore(String name,String location, String gender,String phone, String email,String dob,String userId) {
        Map<String, Object> userdetails = new HashMap<>();
        userdetails.put("userId", userId);
        userdetails.put("name", name);
        userdetails.put("phone_number", phone);
        userdetails.put("email", email);
        userdetails.put("location",location);
        userdetails.put("gender",gender);
        userdetails.put("dob",dob);
        userdetails.put("fcmToken", "fcmToken");

        db.collection("users").document(userId)
                .set(userdetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(RegisterActivity.this,"DocumentSnapshot successfully written!",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Toast.makeText(RegisterActivity.this,"Error writing document",Toast.LENGTH_SHORT).show();
                    }
                });

        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }


}