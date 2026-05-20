package com.zzmg.topic_channel_platform.api;

import com.zzmg.topic_channel_platform.model.ApiResponse;
import com.zzmg.topic_channel_platform.model.CollectStatusResponse;
import com.zzmg.topic_channel_platform.model.CommentCreateRequest;
import com.zzmg.topic_channel_platform.model.CommentItem;
import com.zzmg.topic_channel_platform.model.PostDetail;
import com.zzmg.topic_channel_platform.model.PostListItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PostApi {
    @GET("/api/posts")
    Call<List<PostListItem>> getPosts();

    @GET("/api/posts/{id}")
    Call<PostDetail> getPostDetail(@Path("id") Long id);

    @GET("/api/posts/{postId}/comments")
    Call<List<CommentItem>> getComments(@Path("postId") Long postId);

    @POST("/api/posts/{postId}/comments")
    Call<ApiResponse> createComment(@Path("postId") Long postId, @Body CommentCreateRequest request);

    @GET("/api/posts/{postId}/collect-status")
    Call<CollectStatusResponse> getCollectStatus(@Path("postId") Long postId);

    @POST("/api/posts/{postId}/collect")
    Call<ApiResponse> collect(@Path("postId") Long postId);

    @POST("/api/posts/{postId}/collect/cancel")
    Call<ApiResponse> cancelCollect(@Path("postId") Long postId);
}
