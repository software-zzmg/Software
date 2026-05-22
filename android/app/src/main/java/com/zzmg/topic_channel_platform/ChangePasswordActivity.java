package com.zzmg.topic_channel_platform;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.zzmg.topic_channel_platform.api.UserApi;
import com.zzmg.topic_channel_platform.model.ApiResponse;
import com.zzmg.topic_channel_platform.model.ChangePasswordRequest;
import com.zzmg.topic_channel_platform.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputLayout tilOld, tilNew, tilConfirm;
    private EditText etOldPassword, etNewPassword, etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tilOld = findViewById(R.id.til_old_password);
        tilNew = findViewById(R.id.til_new_password);
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        tilConfirm = findViewById(R.id.til_confirm_password);
        etOldPassword = findViewById(R.id.et_old_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

        etOldPassword.addTextChangedListener(new ClearErrorWatcher(tilOld));
        etNewPassword.addTextChangedListener(new ClearErrorWatcher(tilNew));
        etConfirmPassword.addTextChangedListener(new ClearErrorWatcher(tilConfirm));

        findViewById(R.id.btn_submit).setOnClickListener(v -> {
            if (validateForm()) changePassword();
        });
    }

    private boolean validateForm() {
        boolean valid = true;
        String oldPwd = etOldPassword.getText().toString().trim();
        String newPwd = etNewPassword.getText().toString().trim();
        String confirmPwd = etConfirmPassword.getText().toString().trim();

        if (oldPwd.isEmpty()) {
            tilOld.setError("请输入原密码");
            valid = false;
        }
        if (newPwd.isEmpty()) {
            tilNew.setError("请输入新密码");
            valid = false;
        }
        if (confirmPwd.isEmpty()) {
            tilConfirm.setError("请确认新密码");
            valid = false;
        } else if (!confirmPwd.equals(newPwd)) {
            tilConfirm.setError("两次密码输入不一致");
            valid = false;
        }

        return valid;
    }

    private void changePassword() {
        String oldPwd = etOldPassword.getText().toString().trim();
        String newPwd = etNewPassword.getText().toString().trim();
        String confirmPwd = etConfirmPassword.getText().toString().trim();

        ChangePasswordRequest request = new ChangePasswordRequest(oldPwd, newPwd, confirmPwd);
        UserApi userApi = RetrofitClient.getInstance().create(UserApi.class);
        userApi.changePassword(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 401) {
                    Toast.makeText(ChangePasswordActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                ApiResponse res = null;
                if (response.isSuccessful() && response.body() != null) {
                    res = response.body();
                } else if (response.errorBody() != null) {
                    try {
                        res = new Gson().fromJson(response.errorBody().string(), ApiResponse.class);
                    } catch (Exception ignored) {}
                }
                if (res != null) {
                    Toast.makeText(ChangePasswordActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                    if (res.isSuccess()) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        tilOld.setError(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(ChangePasswordActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
