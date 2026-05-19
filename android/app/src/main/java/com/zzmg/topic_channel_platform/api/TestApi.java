package com.zzmg.topic_channel_platform.api;

import com.zzmg.topic_channel_platform.model.TestResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TestApi {
    @GET("/api/test")
    Call<TestResponse> test();
}
