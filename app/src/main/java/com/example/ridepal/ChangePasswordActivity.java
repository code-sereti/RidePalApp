package com.example.ridepal;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
     FirebaseAuth authProfile;
     EditText current_password,new_password,confirm_new_password;
     Button check_identity,change_password;
     ProgressBar progressBar;
    String userPwdCurr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
//       getSupportActionBar().setTitle("Change Password");

        current_password=findViewById(R.id.editText_change_pwd_current);
        new_password=findViewById(R.id.editText_change_pwd_new);
        confirm_new_password=findViewById(R.id.editText_conf_pwd_new);
        check_identity=findViewById(R.id.button_change_pwd_authenticate);
        change_password=findViewById(R.id.button_change_pwd);
        progressBar=findViewById(R.id.progressBar);


        // Disable edittext for new password ,confirm password,and change password button clickable till user is authenticated
        new_password.setEnabled(false);
        confirm_new_password.setEnabled(false);
        change_password.setEnabled(false);



        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();
        if (firebaseUser.equals("")){
            Toast.makeText(this, "User not found..!", Toast.LENGTH_SHORT).show();
        }else {
            reAuntenticateUser(firebaseUser);
        }
    }
    private void reAuntenticateUser(FirebaseUser firebaseUser) {
        check_identity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwdCurr=current_password.getText().toString();
                if (TextUtils.isEmpty(userPwdCurr)){
                    Toast.makeText(ChangePasswordActivity.this, "Enter your current password", Toast.LENGTH_SHORT).show();
                    current_password.setError("Current password field cannot be empty");
                    current_password.requestFocus();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    //Re-Authenticating the user
                    AuthCredential credential= EmailAuthProvider.getCredential((firebaseUser.getEmail()),userPwdCurr);
                    firebaseUser.reauthenticate(credential)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {

                                    // Proceed with sensitive operations like changing password, etc.
                                    progressBar.setVisibility(View.GONE);

                                    // Disable edittext current password. Enable Edittext new password,confirm password, button change password..
                                    current_password.setEnabled(false);
                                    check_identity.setEnabled(false);
                                    new_password.setEnabled(true);
                                    confirm_new_password.setEnabled(true);
                                    change_password.setEnabled(true);

                                    Toast.makeText(ChangePasswordActivity.this, "You can change your password", Toast.LENGTH_SHORT).show();
                                    current_password.setText("");
                                    change_password.setBackgroundTintList(ContextCompat.getColorStateList(ChangePasswordActivity.this,R.color.dark_green));
                                    change_password.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            UpdatePassword(firebaseUser);
                                        }
                                    });
                                } else {
//                                    Log.e("Notifications", "Error updating notification status: " + e.getMessage());
                                    Exception e = task.getException();
                                    if (e != null) {
                                        Toast.makeText(ChangePasswordActivity.this, "Re-authentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                progressBar.setVisibility(View.GONE);
                            });
                }
            }
        });
    }
    private void UpdatePassword(FirebaseUser firebaseUser) {
        String userNewPass=new_password.getText().toString();
        String userConfPass=confirm_new_password.getText().toString();

        //Check if the user has entered the passwords
        if (TextUtils.isEmpty(userNewPass)){
            Toast.makeText(this, "New password field is empty", Toast.LENGTH_SHORT).show();
            new_password.setError("Field is empty");
            new_password.requestFocus();

        }else if (TextUtils.isEmpty(userConfPass)){
            Toast.makeText(this, "Confirm password field is empty", Toast.LENGTH_SHORT).show();
            confirm_new_password.setError("Field is empty");
            confirm_new_password.requestFocus();
        }else if (!userNewPass.matches(userConfPass)){
            Toast.makeText(this, "Passwords don't match. Try again", Toast.LENGTH_SHORT).show();
            confirm_new_password.setError("Passwords don't match");

        } else if (userNewPass.matches(userPwdCurr)){

            Toast.makeText(this, "Use a password you haven't used before", Toast.LENGTH_SHORT).show();
            confirm_new_password.setError("New password and old password cannot be the same");
            confirm_new_password.requestFocus();
        }else {
            progressBar.setVisibility(View.VISIBLE);
            firebaseUser.updatePassword(userNewPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ChangePasswordActivity.this, "Password change Successfully", Toast.LENGTH_SHORT).show();
                        new_password.setText("");
                        confirm_new_password.setText("");

                    }else {
                        try {
                            throw  task.getException();
                        }catch (Exception e){
                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }
}