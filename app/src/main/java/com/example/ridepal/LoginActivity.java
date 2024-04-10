package com.example.ridepal;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ridepal.Helper.ValidationHelper;
import com.example.ridepal.Models.MyData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {
    public static String SHARED_PREFS = "shared-prefs";
    public static String USER_ID_KEY = "user_id";
    public static String TOKEN_KEY = "token_key";
    public String userid_key,name_key,userid,name;
    String fcmToken;
    SharedPreferences sharedPreferences;
    public Button login;
    public EditText email,password;
    public TextView forgotPassword,signup;

    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        userid_key=sharedPreferences.getString("USER_ID_KEY",null);
        name_key=sharedPreferences.getString("TOKEN_KEY",null);

        email = (EditText) findViewById(R.id.email_login_edt);
        password = (EditText) findViewById(R.id.password_login_edt);
        forgotPassword = (TextView) findViewById(R.id.forgot_password_edt);
        signup = (TextView) findViewById(R.id.login_create_account);
        login = (Button) findViewById(R.id.login_btn);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(register);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                ValidationHelper validationHelper = new ValidationHelper();
                boolean allowSave =true;

//                String passwordString = password.getText().toString();
//                String emailString = email.getText().toString();
                String passwordString = password.getText().toString();
                String emailString = email.getText().toString();

                if (validationHelper.isNullOrEmpty(passwordString)){
                    password.setError("Input Password");
                    return;
                }
                if (validationHelper.isNullOrEmpty(emailString)){
                    email.setError("Input Phone Number");
                    return;
                }


                login(emailString,passwordString);

            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecoverPasswordDialog();
            }
        });
    }



    public void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
//                if (mAuth.getCurrentUser().isEmailVerified()){
                    Toast.makeText(LoginActivity.this, "login successfully", Toast.LENGTH_SHORT).show();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    userid = currentUser.getUid();

                    // Get the FCM token after the user logs in
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


                    MyData data = new MyData();
                    data.token=fcmToken;
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
// Get the document reference for the user
                    DocumentReference docRef = db.collection("users").document(userid);

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
                                Log.d("FirestoreData", "Received name is " + MyData.name);
                            } else {
                                // Document does not exist
                                Log.e("FirestoreData", "No such document");
                            }
                        } else {
                            // Task failed with an exception
                            Log.d("FirestoreData", "Task failed: " + task.getException());
                        }
                    });


                    getUserId(userid);

//                Store The user's id and token to shared Preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(USER_ID_KEY,userid);
                    editor.putString(TOKEN_KEY,fcmToken);

                    editor.apply();

                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));


                    finish();
                }
//                else {
//                    Log.e(TAG, "Not verified");
//                    Toast.makeText(LoginActivity.this, "Your email is not verified. Verify to login " , Toast.LENGTH_SHORT).show();
//                }
            else {
                Toast.makeText(LoginActivity.this, "Log in Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getUserId(String UserId) {

        return name;
    }

    // Update FCM token in Firestore associated with the logged-in user
    private void updateFCMTokenInFirestore(String fcmToken) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userid = currentUser.getUid();

            // Update the FCM token in Firestore
            db.collection("users").document(userid)
                    .update("fcmToken", fcmToken)
                    .addOnSuccessListener(aVoid -> {
//                        Toast.makeText(LoginActivity.this, "Update Successful: ", Toast.LENGTH_SHORT).show();
                        MyData.token=fcmToken;
                        MyData.userId=userid;
                        // FCM token updated successfully
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Update failed: " + e.getMessage());
                        // Failed to update FCM token
                        // Handle the error
                    });
        }
    }

    ProgressDialog loadingBar;

    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");
        LinearLayout linearLayout=new LinearLayout(this);
        final EditText emailet= new EditText(this);

        // write the email using which you registered
        emailet.setText("Email");
        emailet.setMinEms(16);
        emailet.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        linearLayout.addView(emailet);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);

        // Click on Recover and a email will be sent to your registered email id
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email=emailet.getText().toString().trim();
                beginRecovery(email);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void beginRecovery(String email) {
        loadingBar=new ProgressDialog(this);
        loadingBar.setMessage("Sending Email....");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        // calling sendPasswordResetEmail
        // open your email and write the new
        // password and then you can login
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingBar.dismiss();
                if(task.isSuccessful())
                {
                    // if isSuccessful then done message will be shown
                    // and you can change the password
                    Toast.makeText(LoginActivity.this,"Done sent",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(LoginActivity.this,"Error Occurred",Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBar.dismiss();
                Toast.makeText(LoginActivity.this,"Error Failed",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(userid_key != null){
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        }
    }
}