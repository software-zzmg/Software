package com.zzmg.topic_channel_platform.api;

import com.zzmg.topic_channel_platform.model.PostListItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PostApi {
    @GET("/api/posts")
    Call<List<PostListItem>> getPosts();
}
