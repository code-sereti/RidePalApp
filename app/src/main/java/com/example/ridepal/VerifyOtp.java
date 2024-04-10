package com.example.ridepal;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyOtp extends AppCompatActivity {

    private EditText inputOtp1,inputOtp2,inputOtp3,inputOtp4,inputOtp5,inputOtp6;
    private String verificationId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        TextView textMobile=findViewById(R.id.textMobile);
        textMobile.setText(String.format("+254-%s",getIntent().getStringExtra("mobile")));

        inputOtp1=findViewById(R.id.otp_code1);
        inputOtp2=findViewById(R.id.otp_code2);
        inputOtp3=findViewById(R.id.otp_code3);
        inputOtp4=findViewById(R.id.otp_code4);
        inputOtp5=findViewById(R.id.otp_code5);
        inputOtp6=findViewById(R.id.otp_code6);


        setUpOTPInputs();
        final ProgressBar progressBar=findViewById(R.id.progressBar);
        final   Button verifyOtp=findViewById(R.id.verifyOTP);

        verificationId= getIntent().getStringExtra("verificationId");
        verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputOtp1.getText().toString().trim().isEmpty()
                        || inputOtp2.getText().toString().trim().isEmpty()
                        || inputOtp3.getText().toString().trim().isEmpty()
                        || inputOtp4.getText().toString().trim().isEmpty()
                        || inputOtp5.getText().toString().trim().isEmpty()
                        || inputOtp6.getText().toString().trim().isEmpty()){
                    Toast.makeText(VerifyOtp.this, "Please enter a valid code", Toast.LENGTH_SHORT).show();
                    return;
                }

                String code=
               inputOtp1.getText().toString()+
                       inputOtp2.getText().toString()+
                       inputOtp3.getText().toString()+
                       inputOtp4.getText().toString()+
                       inputOtp5.getText().toString()+
                       inputOtp6.getText().toString();
                if (verificationId !=null){
                    progressBar.setVisibility(View.VISIBLE);
                    verifyOtp.setVisibility(View.INVISIBLE);
                    PhoneAuthCredential phoneAuthCredential= PhoneAuthProvider.getCredential(

                            verificationId,
                            code
                    );
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    verifyOtp.setVisibility(View.VISIBLE);
                                    if (task.isSuccessful()){
                                        Intent intent=new Intent(getApplicationContext(), VerificationActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }else {
                                        Toast.makeText(VerifyOtp.this, "Invalid verification code", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }

            }
        });
        findViewById(R.id.resend_otp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+254" + getIntent().getStringExtra("mobile"),
                        60,
                        TimeUnit.SECONDS,
                        VerifyOtp.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {

                                Toast.makeText(VerifyOtp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                verificationId=newVerificationId;
                                Toast.makeText(VerifyOtp.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        });

    }

    private void setUpOTPInputs() {



        inputOtp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()) {
                    inputOtp2.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputOtp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()) {
                    inputOtp3.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputOtp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()) {
                    inputOtp4.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputOtp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()) {
                    inputOtp5.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputOtp5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()) {
                    inputOtp6.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}