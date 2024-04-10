package com.example.ridepal.FCM;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAfRRdR2M:APA91bEynC-0VUGED3JEX0B8WTiD0nF92vVTtEpuNBFB5q_HRSynnFae7i7PyW1BsJZLVVdwKFm8xUBX575_xDxZ6pxfqPlmQCsv1rBlLlgBWmljjYt_fVHvd0iTqbClwsXahbYrbXRi"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}

