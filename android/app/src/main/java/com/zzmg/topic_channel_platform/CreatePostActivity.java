package com.zzmg.topic_channel_platform;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.zzmg.topic_channel_platform.api.ForumApi;
import com.zzmg.topic_channel_platform.api.PostApi;
import com.zzmg.topic_channel_platform.helper.BottomNavHelper;
import com.zzmg.topic_channel_platform.model.ApiResponse;
import com.zzmg.topic_channel_platform.model.ForumItem;
import com.zzmg.topic_channel_platform.model.PostCreateRequest;
import com.zzmg.topic_channel_platform.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostActivity extends AppCompatActivity {

    private static final int REQUEST_LOGIN = 1;

    private TextView tvNoForums;
    private View llForm;
    private Spinner spinnerForum;
    private EditText etTitle, etContent;
    private List<ForumItem> forums = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavHelper.setup(this, "create");

        tvNoForums = findViewById(R.id.tv_no_forums);
        llForm = findViewById(R.id.ll_form);
        spinnerForum = findViewById(R.id.spinner_forum);
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);

        findViewById(R.id.btn_submit).setOnClickListener(v -> submitPost());

        loadMyForums();
    }

    private void loadMyForums() {
        ForumApi forumApi = RetrofitClient.getInstance().create(ForumApi.class);
        forumApi.getMyForums().enqueue(new Callback<List<ForumItem>>() {
            @Override
            public void onResponse(Call<List<ForumItem>> call, Response<List<ForumItem>> response) {
                if (response.code() == 401) {
                    Toast.makeText(CreatePostActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    forums = response.body();
                    if (forums.isEmpty()) {
                        tvNoForums.setVisibility(View.VISIBLE);
                    } else {
                        ArrayAdapter<ForumItem> adapter = new ArrayAdapter<>(
                                CreatePostActivity.this,
                                android.R.layout.simple_spinner_item,
                                forums);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerForum.setAdapter(adapter);
                        llForm.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ForumItem>> call, Throwable t) {
                Toast.makeText(CreatePostActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitPost() {
        if (forums.isEmpty()) return;

        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ForumItem selectedForum = forums.get(spinnerForum.getSelectedItemPosition());
        PostApi postApi = RetrofitClient.getInstance().create(PostApi.class);
        postApi.createPost(new PostCreateRequest(selectedForum.getId(), title, content))
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.code() == 401) {
                            Toast.makeText(CreatePostActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse res = response.body();
                            Toast.makeText(CreatePostActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                            if (res.isSuccess()) {
                                finish();
                            }
                        } else {
                            Toast.makeText(CreatePostActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toast.makeText(CreatePostActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
