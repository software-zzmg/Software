package com.zzmg.topic_channel_platform;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zzmg.topic_channel_platform.adapter.CommentAdapter;
import com.zzmg.topic_channel_platform.api.PostApi;
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

        rvComments.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter();
        rvComments.setAdapter(commentAdapter);

        long postId = getIntent().getLongExtra("postId", -1);
        if (postId == -1) {
            Toast.makeText(this, "Invalid post ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadPostDetail(postId);
        loadComments(postId);
    }

    private void loadPostDetail(long postId) {
        PostApi postApi = RetrofitClient.getInstance().create(PostApi.class);
        postApi.getPostDetail(postId).enqueue(new Callback<PostDetail>() {
            @Override
            public void onResponse(Call<PostDetail> call, Response<PostDetail> response) {
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
                Toast.makeText(PostDetailActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadComments(long postId) {
        PostApi postApi = RetrofitClient.getInstance().create(PostApi.class);
        postApi.getComments(postId).enqueue(new Callback<List<CommentItem>>() {
            @Override
            public void onResponse(Call<List<CommentItem>> call, Response<List<CommentItem>> response) {
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
                tvNoComments.setText("Failed to load comments");
                tvNoComments.setVisibility(View.VISIBLE);
                rvComments.setVisibility(View.GONE);
            }
        });
    }
}
