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
import com.google.gson.Gson;
import com.zzmg.topic_channel_platform.api.UserApi;
import com.zzmg.topic_channel_platform.model.ApiResponse;
import com.zzmg.topic_channel_platform.model.ForgotPasswordRequest;
import com.zzmg.topic_channel_platform.model.SendCodeRequest;
import com.zzmg.topic_channel_platform.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputLayout tilPhone, tilEmail, tilCode, tilNewPassword, tilConfirmPassword;
    private TextInputEditText etPhone, etEmail, etCode, etNewPassword, etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tilPhone = findViewById(R.id.til_phone);
        tilEmail = findViewById(R.id.til_email);
        tilCode = findViewById(R.id.til_code);
        tilNewPassword = findViewById(R.id.til_new_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);

        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);
        etCode = findViewById(R.id.et_code);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

        etPhone.addTextChangedListener(new ClearErrorWatcher(tilPhone));
        etEmail.addTextChangedListener(new ClearErrorWatcher(tilEmail));
        etCode.addTextChangedListener(new ClearErrorWatcher(tilCode));
        etNewPassword.addTextChangedListener(new ClearErrorWatcher(tilNewPassword));
        etConfirmPassword.addTextChangedListener(new ClearErrorWatcher(tilConfirmPassword));

        findViewById(R.id.btn_send_code).setOnClickListener(v -> {
            if (!validateSendForm()) return;
            sendCode();
        });

        findViewById(R.id.btn_reset).setOnClickListener(v -> {
            if (validateForm()) {
                doResetPassword();
            }
        });
    }

    private boolean validateSendForm() {
        boolean valid = true;
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (phone.isEmpty()) {
            tilPhone.setError("请输入手机号");
            valid = false;
        } else if (!phone.matches("1\\d{10}")) {
            tilPhone.setError("手机号格式不正确，应为11位数字");
            valid = false;
        }

        if (email.isEmpty() || !email.contains("@")) {
            tilEmail.setError("邮箱格式错误");
            valid = false;
        }
        return valid;
    }

    private void sendCode() {
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        UserApi userApi = RetrofitClient.getInstance().create(UserApi.class);
        userApi.sendForgotPasswordCode(new SendCodeRequest(phone, email))
                .enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                ApiResponse res = parseResponse(response);
                if (res != null) {
                    if (res.isSuccess()) {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "验证码已发送", Toast.LENGTH_LONG).show();
                    } else {
                        showServerError(res.getMessage());
                    }
                } else {
                    Toast.makeText(ForgotPasswordActivity.this,
                            "发送验证码失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this,
                        "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String code = etCode.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (phone.isEmpty()) {
            tilPhone.setError("请输入手机号");
            valid = false;
        } else if (!phone.matches("1\\d{10}")) {
            tilPhone.setError("手机号格式不正确，应为11位数字");
            valid = false;
        }

        if (email.isEmpty() || !email.contains("@")) {
            tilEmail.setError("邮箱格式错误");
            valid = false;
        }

        if (code.isEmpty()) {
            tilCode.setError("请输入验证码");
            valid = false;
        }

        if (newPassword.isEmpty()) {
            tilNewPassword.setError("新密码不能为空");
            valid = false;
        } else if (newPassword.length() < 6) {
            tilNewPassword.setError("新密码长度至少6位");
            valid = false;
        }

        if (!newPassword.equals(confirmPassword)) {
            tilConfirmPassword.setError("两次密码不一致");
            valid = false;
        }

        return valid;
    }

    private void doResetPassword() {
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String code = etCode.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        UserApi userApi = RetrofitClient.getInstance().create(UserApi.class);
        ForgotPasswordRequest request = new ForgotPasswordRequest(
                phone, email, code, newPassword, confirmPassword);
        userApi.resetPassword(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                ApiResponse res = parseResponse(response);
                if (res != null) {
                    if (res.isSuccess()) {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "密码重置成功，请重新登录", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        showServerError(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this,
                        "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ApiResponse parseResponse(Response<ApiResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            return response.body();
        }
        if (response.errorBody() != null) {
            try {
                return new Gson().fromJson(response.errorBody().string(), ApiResponse.class);
            } catch (Exception ignored) {}
        }
        return null;
    }

    private void showServerError(String message) {
        if (message.contains("手机")) tilPhone.setError(message);
        else if (message.contains("邮箱")) tilEmail.setError(message);
        else if (message.contains("验证码")) tilCode.setError(message);
        else Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
