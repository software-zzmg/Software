package com.zzmg.topic_channel_platform.api;

import com.zzmg.topic_channel_platform.model.SearchResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchApi {
    @GET("/api/search")
    Call<SearchResult> search(@Query("keyword") String keyword);
}
