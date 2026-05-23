package com.zzmg.topic_channel_platform;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.zzmg.topic_channel_platform.api.ForumApi;
import com.zzmg.topic_channel_platform.model.ApiResponse;
import com.zzmg.topic_channel_platform.model.ForumCreateRequest;
import com.zzmg.topic_channel_platform.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateForumActivity extends AppCompatActivity {

    private TextInputLayout tilForumName, tilDescription;
    private TextInputEditText etForumName, etDescription;
    private View tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_forum);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tilForumName = findViewById(R.id.til_forum_name);
        tilDescription = findViewById(R.id.til_description);
        etForumName = findViewById(R.id.et_forum_name);
        etDescription = findViewById(R.id.et_description);
        tvError = findViewById(R.id.tv_error);

        etForumName.addTextChangedListener(new ClearErrorWatcher(tilForumName));
        etDescription.addTextChangedListener(new ClearErrorWatcher(tilDescription));

        findViewById(R.id.btn_submit).setOnClickListener(v -> {
            tvError.setVisibility(View.GONE);
            if (validateForm()) {
                doCreateForum();
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;
        String forumName = etForumName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (forumName.isEmpty()) {
            tilForumName.setError("频道名不能为空");
            valid = false;
        } else if (forumName.length() > 50) {
            tilForumName.setError("频道名长度不能超过50");
            valid = false;
        }

        if (description.isEmpty()) {
            tilDescription.setError("频道简介不能为空");
            valid = false;
        } else if (description.length() > 500) {
            tilDescription.setError("频道简介长度不能超过500");
            valid = false;
        }

        return valid;
    }

    private void doCreateForum() {
        String forumName = etForumName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        ForumApi forumApi = RetrofitClient.getInstance().create(ForumApi.class);
        forumApi.createForum(new ForumCreateRequest(forumName, description))
                .enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 401) {
                    showError("请先登录");
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(CreateForumActivity.this,
                                apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        showError(apiResponse.getMessage());
                    }
                } else {
                    showError("创建失败");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                showError("网络错误: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        tvError.setVisibility(View.VISIBLE);
        ((android.widget.TextView) tvError).setText(message);
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
