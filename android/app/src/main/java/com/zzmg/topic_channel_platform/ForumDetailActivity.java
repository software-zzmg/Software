package com.zzmg.topic_channel_platform;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zzmg.topic_channel_platform.adapter.PostAdapter;
import com.zzmg.topic_channel_platform.api.ForumApi;
import com.zzmg.topic_channel_platform.helper.BottomNavHelper;
import com.zzmg.topic_channel_platform.model.ApiResponse;
import com.zzmg.topic_channel_platform.model.ForumCreateRequest;
import com.zzmg.topic_channel_platform.model.ForumDetail;
import com.zzmg.topic_channel_platform.model.PostListItem;
import com.zzmg.topic_channel_platform.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForumDetailActivity extends AppCompatActivity {

    private TextView tvForumName, tvCreator, tvDescription;
    private Button btnJoinLeave, btnEditForum;
    private TextView tvNoPosts;
    private RecyclerView rvPosts;
    private PostAdapter postAdapter;

    private long forumId;
    private boolean joined;
    private boolean isCreator;

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
        btnEditForum = findViewById(R.id.btn_edit_forum);
        tvNoPosts = findViewById(R.id.tv_no_posts);
        rvPosts = findViewById(R.id.rv_posts);

        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter();
        rvPosts.setAdapter(postAdapter);

        forumId = getIntent().getLongExtra("forumId", -1);
        if (forumId == -1) {
            Toast.makeText(this, "Invalid forum ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnJoinLeave.setOnClickListener(v -> handleAction());
        btnEditForum.setOnClickListener(v -> showEditDialog());
        loadForumDetail();
        loadForumPosts();
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
                    updateActionButton(forum.isCreator(), forum.isJoined());
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

    private void loadForumPosts() {
        ForumApi forumApi = RetrofitClient.getInstance().create(ForumApi.class);
        forumApi.getForumPosts(forumId).enqueue(new Callback<List<PostListItem>>() {
            @Override
            public void onResponse(Call<List<PostListItem>> call, Response<List<PostListItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    postAdapter.setPosts(response.body());
                    boolean empty = response.body().isEmpty();
                    tvNoPosts.setVisibility(empty ? View.VISIBLE : View.GONE);
                    rvPosts.setVisibility(empty ? View.GONE : View.VISIBLE);
                } else {
                    tvNoPosts.setVisibility(View.VISIBLE);
                    rvPosts.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<PostListItem>> call, Throwable t) {
                tvNoPosts.setText("Failed to load posts");
                tvNoPosts.setVisibility(View.VISIBLE);
                rvPosts.setVisibility(View.GONE);
            }
        });
    }

    private void updateActionButton(boolean creator, boolean member) {
        isCreator = creator;
        joined = member;
        if (creator) {
            btnEditForum.setVisibility(View.VISIBLE);
            btnJoinLeave.setText("Dismiss Channel");
        } else {
            btnEditForum.setVisibility(View.GONE);
            if (member) {
                btnJoinLeave.setText("Leave");
            } else {
                btnJoinLeave.setText("Join");
            }
        }
    }

    private void showEditDialog() {
        android.widget.EditText etName = new android.widget.EditText(this);
        etName.setText(tvForumName.getText());
        android.widget.EditText etDesc = new android.widget.EditText(this);
        etDesc.setText(tvDescription.getText());
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 16, 48, 0);
        layout.addView(etName);
        layout.addView(etDesc);
        ((LinearLayout.LayoutParams) etDesc.getLayoutParams()).topMargin = 16;

        new AlertDialog.Builder(this)
                .setTitle("Edit Channel")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String desc = etDesc.getText().toString().trim();
                    if (name.isEmpty() || desc.isEmpty()) {
                        Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ForumApi forumApi = RetrofitClient.getInstance().create(ForumApi.class);
                    forumApi.editForum(forumId, new ForumCreateRequest(name, desc))
                            .enqueue(new Callback<ApiResponse>() {
                                @Override
                                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                    if (response.isSuccessful() && response.body() != null
                                            && response.body().isSuccess()) {
                                        Toast.makeText(ForumDetailActivity.this,
                                                response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        tvForumName.setText(name);
                                        tvDescription.setText(desc);
                                    } else if (response.code() == 403) {
                                        Toast.makeText(ForumDetailActivity.this,
                                                "Only the creator can edit", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ForumDetailActivity.this,
                                                "Edit failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onFailure(Call<ApiResponse> call, Throwable t) {
                                    Toast.makeText(ForumDetailActivity.this,
                                            "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void handleAction() {
        if (isCreator) {
            new AlertDialog.Builder(this)
                    .setTitle("Dismiss Channel")
                    .setMessage("Dismiss this channel? This cannot be undone.")
                    .setPositiveButton("Dismiss", (dialog, which) -> deleteForum())
                    .setNegativeButton("Cancel", null)
                    .show();
        } else if (joined) {
            leave();
        } else {
            join();
        }
    }

    private void join() {
        ForumApi forumApi = RetrofitClient.getInstance().create(ForumApi.class);
        forumApi.join(forumId).enqueue(new ActionCallback("join"));
    }

    private void leave() {
        ForumApi forumApi = RetrofitClient.getInstance().create(ForumApi.class);
        forumApi.leave(forumId).enqueue(new ActionCallback("leave"));
    }

    private void deleteForum() {
        ForumApi forumApi = RetrofitClient.getInstance().create(ForumApi.class);
        forumApi.deleteForum(forumId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 401) {
                    Toast.makeText(ForumDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.code() == 403) {
                    Toast.makeText(ForumDetailActivity.this, "Only the creator can dismiss", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse res = response.body();
                    Toast.makeText(ForumDetailActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                    if (res.isSuccess()) {
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(ForumDetailActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class ActionCallback implements Callback<ApiResponse> {
        private final String action;

        ActionCallback(String action) { this.action = action; }

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
                    if ("leave".equals(action)) {
                        updateActionButton(false, false);
                    } else {
                        updateActionButton(false, true);
                    }
                }
            }
        }

        @Override
        public void onFailure(Call<ApiResponse> call, Throwable t) {
            Toast.makeText(ForumDetailActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
