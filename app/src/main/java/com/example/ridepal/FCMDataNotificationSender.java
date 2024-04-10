package com.example.ridepal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FCMDataNotificationSender {

    public static final String API_URL = "https://fcm.googleapis.com/v1/projects/nexashare/messages:send";
    public static final String YOUR_SERVER_KEY = "key=AAAAwmk5dLU:APA91bFAdNKT43O3ZhQM3z4h_mFatG7qNDIy5PjFn08FYHtTNbrvBtdUCFeO2fvTh2R3RXK8egqjOinf0LvO4RMx-RSn8fAvVNNj9e3PhXO8H5XFHeHWCZnzfQF77OQ4S5hy-csDb5Ic"; // Your Firebase server key

    public static void sendNotification(String deviceToken, String title, String message) {
        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        JSONObject messageJson = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject target = new JSONObject();

        try {
            data.put("title", title);
            data.put("message", message);

            messageJson.put("data", data);
            messageJson.put("token", deviceToken);

            target.put("message", messageJson);

            json.put("message", target);

            RequestBody requestBody = RequestBody.create(json.toString(), MediaType.parse("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + YOUR_SERVER_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        System.out.println("Notification sent successfully: " + responseData);
                    } else {
                        System.out.println("Failed to send notification: " + response.body().string());
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String deviceToken = "SPECIFIC_DEVICE_TOKEN"; // Replace with the FCM token of the target device
        String title = "Title of the Data Notification";
        String message = "Message of the Data Notification";

        sendNotification(deviceToken, title, message);
    }
}
