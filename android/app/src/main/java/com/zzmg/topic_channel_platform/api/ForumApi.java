package com.zzmg.topic_channel_platform.api;

import com.zzmg.topic_channel_platform.model.ForumItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ForumApi {
    @GET("/api/forums/my")
    Call<List<ForumItem>> getMyForums();
}
