package com.zzmg.topic_channel_platform.api;

import com.zzmg.topic_channel_platform.model.PostDetail;
import com.zzmg.topic_channel_platform.model.PostListItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PostApi {
    @GET("/api/posts")
    Call<List<PostListItem>> getPosts();

    @GET("/api/posts/{id}")
    Call<PostDetail> getPostDetail(@Path("id") Long id);
}
