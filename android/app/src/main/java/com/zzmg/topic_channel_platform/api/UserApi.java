package com.zzmg.topic_channel_platform.api;

import com.zzmg.topic_channel_platform.model.ApiResponse;
import com.zzmg.topic_channel_platform.model.ChangePasswordRequest;
import com.zzmg.topic_channel_platform.model.ForumListItem;
import com.zzmg.topic_channel_platform.model.LoginRequest;
import com.zzmg.topic_channel_platform.model.LoginResponse;
import com.zzmg.topic_channel_platform.model.PostListItem;
import com.zzmg.topic_channel_platform.model.RegisterRequest;
import com.zzmg.topic_channel_platform.model.SendCodeRequest;
import com.zzmg.topic_channel_platform.model.UpdateProfileRequest;
import com.zzmg.topic_channel_platform.model.UserProfile;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface UserApi {
    @POST("/api/users/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("/api/users/me")
    Call<UserProfile> getMe();

    @PUT("/api/users/me")
    Call<ApiResponse> updateMe(@Body UpdateProfileRequest request);

    @POST("/api/users/me/password")
    Call<ApiResponse> changePassword(@Body ChangePasswordRequest request);

    @GET("/api/users/me/forums")
    Call<List<ForumListItem>> getMyForumsJoined();

    @POST("/api/users/me/delete")
    Call<ApiResponse> deleteMe();

    @POST("/api/users/logout")
    Call<ApiResponse> logout();

    @GET("/api/users/me/collects")
    Call<List<PostListItem>> getMyCollects();

    @POST("/api/users/register/send-code")
    Call<ApiResponse> sendRegisterCode(@Body SendCodeRequest request);

    @POST("/api/users/register")
    Call<ApiResponse> register(@Body RegisterRequest request);
}
