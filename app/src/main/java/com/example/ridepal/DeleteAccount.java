package com.example.ridepal;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteAccount extends AppCompatActivity {
    EditText current_password;
    Button auntenticate,delete_account;
    FirebaseAuth authProfile;
     FirebaseUser firebaseUser;
    String userPwd;
    ProgressBar progressBar;
    private static final String TAG="DeleteAccount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        current_password=findViewById(R.id.editText_delete_user_pwd);
        auntenticate=findViewById(R.id.button_delete_user_authenticate);
        delete_account=findViewById(R.id.button_delete_user);
        progressBar=findViewById(R.id.progressBar);

        // Disable delete profile button until the user is authenticated .
        delete_account.setEnabled(false);
        authProfile=FirebaseAuth.getInstance();
        firebaseUser=authProfile.getCurrentUser();

        // Check whether the firebase user is null or not
        if (firebaseUser==null){
            Toast.makeText(this, "Something went wrong..User doesn't exist", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DeleteAccount.this, HomeActivity.class);
            startActivity(intent);

        }else {
            reAuntenticateUser(firebaseUser);
        }


    }

    private void reAuntenticateUser(FirebaseUser firebaseUser) {
        auntenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwd=current_password.getText().toString();
                if (TextUtils.isEmpty(userPwd)){
                    Toast.makeText(DeleteAccount.this, "Enter your current password", Toast.LENGTH_SHORT).show();
                    current_password.setError("Current password field cannot be empty");
                    current_password.requestFocus();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    //Re-Authenticating the user
                    AuthCredential credential= EmailAuthProvider.getCredential((firebaseUser.getEmail()),userPwd);
                    firebaseUser.reauthenticate(credential)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {

                                    // Proceed with sensitive operations like changing password, etc.
                                    progressBar.setVisibility(View.GONE);

                                    // Disable edittext current password. Enable button delete password.
                                    current_password.setEnabled(false);
                                    auntenticate.setEnabled(false);
                                    delete_account.setEnabled(true);

                                    Toast.makeText(DeleteAccount.this, "Authentication compete", Toast.LENGTH_SHORT).show();
                                    delete_account.setBackgroundTintList(ContextCompat.getColorStateList(DeleteAccount.this,R.color.dark_green));
                                    delete_account.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showAlertDialog();
                                        }
                                    });
                                } else {
//                                    Log.e("Notifications", "Error updating notification status: " + e.getMessage());
                                    Exception e = task.getException();
                                    if (e != null) {
                                        Toast.makeText(DeleteAccount.this, "Re-authentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                progressBar.setVisibility(View.GONE);
                            });
                }
            }
        });
    }

    private void showAlertDialog() {
        // Set the builder Alert...
        AlertDialog.Builder builder=    new AlertDialog.Builder(DeleteAccount.this);
        builder.setTitle("Delete User..?");
        builder.setMessage("A you sure about deleting your account..?");

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUser(firebaseUser);

            }
        });
        // If user presses cancel...Return to home Activity..
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(DeleteAccount.this, "Account deletion canceled..", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DeleteAccount.this, HomeActivity.class);
                startActivity(intent);
                finish();

            }
        });

        // Create the AlertDialog...

        AlertDialog alertDialog=builder.create();
        // Change the button color of continue ..
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });

        //  Show the AlertDialog...
        alertDialog.show();
    }

    // Delete User method.....
    private void deleteUser(FirebaseUser firebaseUser) {
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    deleteUserData();
                    authProfile.signOut();
                    Toast.makeText(DeleteAccount.this, "User account as been deleted.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DeleteAccount.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    try {
                        throw task.getException();
                    }catch (Exception e){
                        Toast.makeText(DeleteAccount.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);

            }
        });


    }

    private void deleteUserData() {
        // Deleting user profile...
        FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
        StorageReference storageReference=firebaseStorage.getReferenceFromUrl(firebaseUser.getPhotoUrl().toString());
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "OnSuccess: Profile photo deleted. ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.getMessage());
                Toast.makeText(DeleteAccount.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        // Delete user data using dataReference
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "User data successfully deleted!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.getMessage());
                Toast.makeText(DeleteAccount.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}