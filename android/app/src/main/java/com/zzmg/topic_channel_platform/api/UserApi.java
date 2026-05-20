package com.zzmg.topic_channel_platform.api;

import com.zzmg.topic_channel_platform.model.LoginRequest;
import com.zzmg.topic_channel_platform.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApi {
    @POST("/api/users/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}
