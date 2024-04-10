package com.example.ridepal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AccountSettings extends AppCompatActivity {

    public TextView change_password,delete,back_arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);


        change_password=findViewById(R.id.txt_change_password);
        delete=findViewById(R.id.txt_delete_account);
        back_arrow=findViewById(R.id.back_home);

        // Back to home activity..
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountSettings.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        // User to change password...
        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountSettings.this, ChangePasswordActivity.class);
                startActivity(intent);
                Toast.makeText(AccountSettings.this, "Go change your password", Toast.LENGTH_SHORT).show();

            }
        });

        // User to delete account..
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountSettings.this, DeleteAccount.class);
                startActivity(intent);
            }
        });

    }
}