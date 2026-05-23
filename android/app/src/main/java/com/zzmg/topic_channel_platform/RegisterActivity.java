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
import com.zzmg.topic_channel_platform.api.UserApi;
import com.zzmg.topic_channel_platform.model.ApiResponse;
import com.zzmg.topic_channel_platform.model.RegisterRequest;
import com.zzmg.topic_channel_platform.model.SendCodeRequest;
import com.zzmg.topic_channel_platform.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilPhone, tilEmail, tilCode, tilUsername, tilPassword, tilConfirmPassword;
    private TextInputEditText etPhone, etEmail, etCode, etUsername, etPassword, etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tilPhone = findViewById(R.id.til_phone);
        tilEmail = findViewById(R.id.til_email);
        tilCode = findViewById(R.id.til_code);
        tilUsername = findViewById(R.id.til_username);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);

        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);
        etCode = findViewById(R.id.et_code);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

        etPhone.addTextChangedListener(new ClearErrorWatcher(tilPhone));
        etEmail.addTextChangedListener(new ClearErrorWatcher(tilEmail));
        etCode.addTextChangedListener(new ClearErrorWatcher(tilCode));
        etUsername.addTextChangedListener(new ClearErrorWatcher(tilUsername));
        etPassword.addTextChangedListener(new ClearErrorWatcher(tilPassword));
        etConfirmPassword.addTextChangedListener(new ClearErrorWatcher(tilConfirmPassword));

        findViewById(R.id.btn_send_code).setOnClickListener(v -> {
            if (!validateSendForm()) return;
            sendCode();
        });

        findViewById(R.id.btn_register).setOnClickListener(v -> {
            if (validateForm()) {
                doRegister();
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
        String email = etEmail.getText().toString().trim();
        UserApi userApi = RetrofitClient.getInstance().create(UserApi.class);
        userApi.sendRegisterCode(new SendCodeRequest(null, email)).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(RegisterActivity.this,
                                "验证码已发送", Toast.LENGTH_LONG).show();
                    } else {
                        tilEmail.setError(apiResponse.getMessage());
                    }
                } else {
                    tilEmail.setError("发送验证码失败");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                tilEmail.setError("网络错误: " + t.getMessage());
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String code = etCode.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
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

        if (username.isEmpty()) {
            tilUsername.setError("用户名不能为空");
            valid = false;
        }

        if (password.isEmpty()) {
            tilPassword.setError("密码不能为空");
            valid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("密码长度至少为6位");
            valid = false;
        }

        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("两次密码不一致");
            valid = false;
        }

        return valid;
    }

    private void doRegister() {
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String code = etCode.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        UserApi userApi = RetrofitClient.getInstance().create(UserApi.class);
        RegisterRequest request = new RegisterRequest(phone, email, username, password,
                confirmPassword, code);
        userApi.register(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(RegisterActivity.this,
                                "注册成功，请登录", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        showError(apiResponse.getMessage());
                    }
                } else {
                    showError("注册失败");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                showError("网络错误: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        if (message.contains("手机")) tilPhone.setError(message);
        else if (message.contains("邮箱")) tilEmail.setError(message);
        else if (message.contains("验证码")) tilCode.setError(message);
        else if (message.contains("用户")) tilUsername.setError(message);
        else if (message.contains("密码")) tilPassword.setError(message);
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
