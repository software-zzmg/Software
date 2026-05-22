package com.zzmg.topic_channel_platform;

import android.os.Bundle;
import android.view.View;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.zzmg.topic_channel_platform.adapter.CommentAdapter;
import com.zzmg.topic_channel_platform.api.PostApi;
import com.zzmg.topic_channel_platform.model.ApiResponse;
import com.zzmg.topic_channel_platform.model.CollectStatusResponse;
import com.zzmg.topic_channel_platform.model.CommentCreateRequest;
import com.zzmg.topic_channel_platform.model.CommentItem;
import com.zzmg.topic_channel_platform.model.PostDetail;
import com.zzmg.topic_channel_platform.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvAuthor, tvForum, tvTime, tvContent;
    private TextView tvNoComments;
    private RecyclerView rvComments;
    private CommentAdapter commentAdapter;
    private TextInputLayout tilComment;
    private EditText etComment;
    private Button btnCollect;

    private long postId;
    private boolean isCollected;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvTitle = findViewById(R.id.tv_detail_title);
        tvAuthor = findViewById(R.id.tv_detail_author);
        tvForum = findViewById(R.id.tv_detail_forum);
        tvTime = findViewById(R.id.tv_detail_time);
        tvContent = findViewById(R.id.tv_detail_content);
        tvNoComments = findViewById(R.id.tv_no_comments);
        rvComments = findViewById(R.id.rv_comments);
        tilComment = findViewById(R.id.til_comment);
        etComment = findViewById(R.id.et_comment);
        btnCollect = findViewById(R.id.btn_collect);

        rvComments.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter();
        rvComments.setAdapter(commentAdapter);

        postId = getIntent().getLongExtra("postId", -1);
        if (postId == -1) {
            Toast.makeText(this, "Invalid post ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnCollect.setOnClickListener(v -> toggleCollect());

        etComment.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilComment.setError(null);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        findViewById(R.id.btn_submit).setOnClickListener(v -> submitComment());

        com.zzmg.topic_channel_platform.helper.BottomNavHelper.setup(this, "detail");

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadPostDetail(postId);
            loadComments(postId);
            loadCollectStatus(postId);
        });

        loadPostDetail(postId);
        loadComments(postId);
        loadCollectStatus(postId);
    }

    private void toggleCollect() {
        PostApi postApi = RetrofitClient.getInstance().create(PostApi.class);
        if (isCollected) {
            postApi.cancelCollect(postId).enqueue(new CollectCallback());
        } else {
            postApi.collect(postId).enqueue(new CollectCallback());
        }
    }

    private void loadCollectStatus(long postId) {
        PostApi postApi = RetrofitClient.getInstance().create(PostApi.class);
        postApi.getCollectStatus(postId).enqueue(new Callback<CollectStatusResponse>() {
            @Override
            public void onResponse(Call<CollectStatusResponse> call, Response<CollectStatusResponse> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.code() == 401) {
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    updateCollectUI(response.body().isCollected());
                }
            }

            @Override
            public void onFailure(Call<CollectStatusResponse> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void updateCollectUI(boolean collected) {
        isCollected = collected;
        btnCollect.setText(collected ? "Collected" : "Collect");
    }

    private class CollectCallback implements Callback<ApiResponse> {
        @Override
        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
            if (response.code() == 401) {
                Toast.makeText(PostDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                return;
            }
            if (response.isSuccessful() && response.body() != null) {
                ApiResponse res = response.body();
                Toast.makeText(PostDetailActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                if (res.isSuccess()) {
                    updateCollectUI(!isCollected);
                }
            }
        }

        @Override
        public void onFailure(Call<ApiResponse> call, Throwable t) {
            Toast.makeText(PostDetailActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void submitComment() {
        String content = etComment.getText().toString().trim();
        if (content.isEmpty()) {
            tilComment.setError("评论内容不能为空");
            return;
        }

        PostApi postApi = RetrofitClient.getInstance().create(PostApi.class);
        postApi.createComment(postId, new CommentCreateRequest(content))
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.code() == 401) {
                            Toast.makeText(PostDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse res = response.body();
                            if (res.isSuccess()) {
                                Toast.makeText(PostDetailActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                                etComment.setText("");
                            } else {
                                Toast.makeText(PostDetailActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PostDetailActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toast.makeText(PostDetailActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadPostDetail(long postId) {
        PostApi postApi = RetrofitClient.getInstance().create(PostApi.class);
        postApi.getPostDetail(postId).enqueue(new Callback<PostDetail>() {
            @Override
            public void onResponse(Call<PostDetail> call, Response<PostDetail> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    PostDetail post = response.body();
                    tvTitle.setText(post.getTitle());
                    tvAuthor.setText(post.getAuthorName());
                    tvForum.setText(post.getForumName());
                    tvTime.setText(post.getPublishTime());
                    tvContent.setText(post.getContent());
                } else {
                    Toast.makeText(PostDetailActivity.this, "Post not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<PostDetail> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(PostDetailActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadComments(long postId) {
        PostApi postApi = RetrofitClient.getInstance().create(PostApi.class);
        postApi.getComments(postId).enqueue(new Callback<List<CommentItem>>() {
            @Override
            public void onResponse(Call<List<CommentItem>> call, Response<List<CommentItem>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    commentAdapter.setComments(response.body());
                    boolean empty = response.body().isEmpty();
                    tvNoComments.setVisibility(empty ? View.VISIBLE : View.GONE);
                    rvComments.setVisibility(empty ? View.GONE : View.VISIBLE);
                } else {
                    tvNoComments.setVisibility(View.VISIBLE);
                    rvComments.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<CommentItem>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                tvNoComments.setText("Failed to load comments");
                tvNoComments.setVisibility(View.VISIBLE);
                rvComments.setVisibility(View.GONE);
            }
        });
    }
}
