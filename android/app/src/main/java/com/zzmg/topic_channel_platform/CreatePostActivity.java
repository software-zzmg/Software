package com.zzmg.topic_channel_platform;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.google.android.material.textfield.TextInputLayout;
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

    private TextView tvNoForums;
    private View llForm;
    private Spinner spinnerForum;
    private TextInputLayout tilTitle, tilContent;
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

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        tvNoForums = findViewById(R.id.tv_no_forums);
        llForm = findViewById(R.id.ll_form);
        spinnerForum = findViewById(R.id.spinner_forum);
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        tilTitle = findViewById(R.id.til_title);
        tilContent = findViewById(R.id.til_content);

        etTitle.addTextChangedListener(new ClearErrorWatcher(tilTitle));
        etContent.addTextChangedListener(new ClearErrorWatcher(tilContent));

        findViewById(R.id.btn_submit).setOnClickListener(v -> {
            if (validateForm()) submitPost();
        });

        loadMyForums();
    }

    private boolean validateForm() {
        boolean valid = true;
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (title.isEmpty()) {
            tilTitle.setError("标题不能为空");
            valid = false;
        } else if (title.length() > 100) {
            tilTitle.setError("标题不能超过100字");
            valid = false;
        }

        if (content.isEmpty()) {
            tilContent.setError("正文不能为空");
            valid = false;
        }

        return valid;
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
                Toast.makeText(CreatePostActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitPost() {
        if (forums.isEmpty()) return;

        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();
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
                            if (res.isSuccess()) {
                                Toast.makeText(CreatePostActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                tilContent.setError(res.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toast.makeText(CreatePostActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static class ClearErrorWatcher implements TextWatcher {
        private final TextInputLayout layout;
        ClearErrorWatcher(TextInputLayout layout) { this.layout = layout; }
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            layout.setError(null);
        }
        @Override public void afterTextChanged(Editable s) {}
    }
}
