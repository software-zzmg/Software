package com.zzmg.topic_channel_platform.api;

import com.zzmg.topic_channel_platform.model.ApiResponse;
import com.zzmg.topic_channel_platform.model.ForumCreateRequest;
import com.zzmg.topic_channel_platform.model.ForumDetail;
import com.zzmg.topic_channel_platform.model.ForumItem;
import com.zzmg.topic_channel_platform.model.ForumListItem;
import com.zzmg.topic_channel_platform.model.PostListItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ForumApi {
    @GET("/api/forums/my")
    Call<List<ForumItem>> getMyForums();

    @GET("/api/forums")
    Call<List<ForumListItem>> getForums();

    @GET("/api/forums/{id}")
    Call<ForumDetail> getForumDetail(@Path("id") Long id);

    @GET("/api/forums/{forumId}/posts")
    Call<List<PostListItem>> getForumPosts(@Path("forumId") Long forumId);

    @POST("/api/forums/{id}/join")
    Call<ApiResponse> join(@Path("id") Long id);

    @POST("/api/forums/{id}/leave")
    Call<ApiResponse> leave(@Path("id") Long id);

    @POST("/api/forums")
    Call<ApiResponse> createForum(@Body ForumCreateRequest request);

    @DELETE("/api/forums/{id}")
    Call<ApiResponse> deleteForum(@Path("id") Long id);

    @PUT("/api/forums/{id}")
    Call<ApiResponse> editForum(@Path("id") Long id, @Body ForumCreateRequest request);
}
