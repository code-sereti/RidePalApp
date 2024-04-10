package com.example.ridepal.Helper;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import java.net.URLEncoder;

public class Whatsapp {
    public static void sendMessageToWhatsApp(String phoneNumber, String message, Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW);

        try {
            String url = "https://api.whatsapp.com/send?phone=" + phoneNumber + "&text= We can discuss the ride details.. " + URLEncoder.encode(message, "UTF-8");
            intent.setPackage("com.whatsapp");
            intent.setData(Uri.parse(url));
            if (intent.resolveActivity(packageManager) != null) {
                context.startActivity(intent);
            } else {
                // WhatsApp not installed, prompt the user to install WhatsApp
                Toast.makeText(context, "WhatsApp not installed ", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception
        }
    }
}
