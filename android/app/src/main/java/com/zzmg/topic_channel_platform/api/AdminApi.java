package com.zzmg.topic_channel_platform.api;

import com.zzmg.topic_channel_platform.model.AdminLoginRequest;
import com.zzmg.topic_channel_platform.model.AdminLoginResponse;
import com.zzmg.topic_channel_platform.model.ApiResponse;
import com.zzmg.topic_channel_platform.model.PendingCommentItem;
import com.zzmg.topic_channel_platform.model.PendingForumItem;
import com.zzmg.topic_channel_platform.model.PendingPostItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AdminApi {
    @POST("/api/admin/login")
    Call<AdminLoginResponse> login(@Body AdminLoginRequest request);

    @POST("/api/admin/logout")
    Call<ApiResponse> logout();

    @GET("/api/admin/forums/pending")
    Call<List<PendingForumItem>> getPendingForums();

    @POST("/api/admin/forums/{id}/approve")
    Call<ApiResponse> approveForum(@Path("id") Long id);

    @POST("/api/admin/forums/{id}/reject")
    Call<ApiResponse> rejectForum(@Path("id") Long id);

    @GET("/api/admin/posts/pending")
    Call<List<PendingPostItem>> getPendingPosts();

    @POST("/api/admin/posts/{id}/approve")
    Call<ApiResponse> approvePost(@Path("id") Long id);

    @POST("/api/admin/posts/{id}/reject")
    Call<ApiResponse> rejectPost(@Path("id") Long id);

    @GET("/api/admin/comments/pending")
    Call<List<PendingCommentItem>> getPendingComments();

    @POST("/api/admin/comments/{id}/approve")
    Call<ApiResponse> approveComment(@Path("id") Long id);

    @POST("/api/admin/comments/{id}/reject")
    Call<ApiResponse> rejectComment(@Path("id") Long id);
}
