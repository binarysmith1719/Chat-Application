package com.codezilla.chatapp.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
            "Content-Type:application/json",
            "Authorization:key=AAAAicLGkSw:APA91bG7woong8DhKMQwywkcGVR-uTaO0vt67jBvCei6JDETDPWcCHV3ZvN-nOevT62x6Ds6zkyrJ1IAm7lF476gg3ZzVu9nuY7Y1msoJYba-Q6vk7xhIHxtEzk0Tn9mgZUbR8eMpLym"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}
