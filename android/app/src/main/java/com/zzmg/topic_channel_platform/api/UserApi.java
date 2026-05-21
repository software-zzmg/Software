package com.zzmg.topic_channel_platform.api;

import com.zzmg.topic_channel_platform.model.ApiResponse;
import com.zzmg.topic_channel_platform.model.LoginRequest;
import com.zzmg.topic_channel_platform.model.LoginResponse;
import com.zzmg.topic_channel_platform.model.PostListItem;
import com.zzmg.topic_channel_platform.model.UserProfile;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserApi {
    @POST("/api/users/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("/api/users/me")
    Call<UserProfile> getMe();

    @POST("/api/users/logout")
    Call<ApiResponse> logout();

    @GET("/api/users/me/collects")
    Call<List<PostListItem>> getMyCollects();
}
