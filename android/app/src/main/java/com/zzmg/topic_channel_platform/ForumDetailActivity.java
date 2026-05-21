package com.zzmg.topic_channel_platform;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.zzmg.topic_channel_platform.api.ForumApi;
import com.zzmg.topic_channel_platform.helper.BottomNavHelper;
import com.zzmg.topic_channel_platform.model.ApiResponse;
import com.zzmg.topic_channel_platform.model.ForumDetail;
import com.zzmg.topic_channel_platform.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForumDetailActivity extends AppCompatActivity {

    private TextView tvForumName, tvCreator, tvDescription;
    private Button btnJoinLeave;

    private long forumId;
    private boolean joined;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forum_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavHelper.setup(this, "forum_detail");

        tvForumName = findViewById(R.id.tv_forum_name);
        tvCreator = findViewById(R.id.tv_forum_creator);
        tvDescription = findViewById(R.id.tv_forum_description);
        btnJoinLeave = findViewById(R.id.btn_join_leave);

        forumId = getIntent().getLongExtra("forumId", -1);
        if (forumId == -1) {
            Toast.makeText(this, "Invalid forum ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnJoinLeave.setOnClickListener(v -> toggleJoin());
        loadForumDetail();
    }

    private void loadForumDetail() {
        ForumApi forumApi = RetrofitClient.getInstance().create(ForumApi.class);
        forumApi.getForumDetail(forumId).enqueue(new Callback<ForumDetail>() {
            @Override
            public void onResponse(Call<ForumDetail> call, Response<ForumDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ForumDetail forum = response.body();
                    tvForumName.setText(forum.getForumName());
                    tvCreator.setText("Created by " + forum.getCreatorName());
                    tvDescription.setText(forum.getDescription());
                    updateJoinButton(forum.isJoined());
                } else {
                    Toast.makeText(ForumDetailActivity.this, "Forum not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ForumDetail> call, Throwable t) {
                Toast.makeText(ForumDetailActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleJoin() {
        ForumApi forumApi = RetrofitClient.getInstance().create(ForumApi.class);
        Callback<ApiResponse> callback = new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 401) {
                    Toast.makeText(ForumDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse res = response.body();
                    Toast.makeText(ForumDetailActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                    if (res.isSuccess()) {
                        updateJoinButton(!joined);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(ForumDetailActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        if (joined) {
            forumApi.leave(forumId).enqueue(callback);
        } else {
            forumApi.join(forumId).enqueue(callback);
        }
    }

    private void updateJoinButton(boolean isJoined) {
        joined = isJoined;
        btnJoinLeave.setText(isJoined ? "Leave" : "Join");
    }
}
