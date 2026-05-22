package com.zzmg.topic_channel_platform;

import android.content.Intent;
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
import com.zzmg.topic_channel_platform.api.AdminApi;
import com.zzmg.topic_channel_platform.model.AdminLoginRequest;
import com.zzmg.topic_channel_platform.model.AdminLoginResponse;
import com.zzmg.topic_channel_platform.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminLoginActivity extends AppCompatActivity {

    private TextInputLayout tilAdminId, tilPassword;
    private TextInputEditText etAdminId, etPassword;
    private View tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btn_back), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        tilAdminId = findViewById(R.id.til_admin_id);
        tilPassword = findViewById(R.id.til_password);
        etAdminId = findViewById(R.id.et_admin_id);
        etPassword = findViewById(R.id.et_password);
        tvError = findViewById(R.id.tv_error);

        etAdminId.addTextChangedListener(new ClearErrorWatcher(tilAdminId));
        etPassword.addTextChangedListener(new ClearErrorWatcher(tilPassword));

        findViewById(R.id.btn_login).setOnClickListener(v -> {
            tvError.setVisibility(View.GONE);
            if (validateForm()) doLogin();
        });
    }

    private boolean validateForm() {
        boolean valid = true;
        if (etAdminId.getText().toString().trim().isEmpty()) {
            tilAdminId.setError("请输入管理员账号");
            valid = false;
        }
        if (etPassword.getText().toString().trim().isEmpty()) {
            tilPassword.setError("请输入密码");
            valid = false;
        }
        return valid;
    }

    private void doLogin() {
        String adminId = etAdminId.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        AdminApi adminApi = RetrofitClient.getInstance().create(AdminApi.class);
        adminApi.login(new AdminLoginRequest(adminId, password))
                .enqueue(new Callback<AdminLoginResponse>() {
            @Override
            public void onResponse(Call<AdminLoginResponse> call, Response<AdminLoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AdminLoginResponse res = response.body();
                    if (res.isSuccess()) {
                        Toast.makeText(AdminLoginActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminLoginActivity.this, AdminDashboardActivity.class));
                        finish();
                    } else {
                        showError(res.getMessage());
                    }
                } else {
                    showError("登录失败");
                }
            }

            @Override
            public void onFailure(Call<AdminLoginResponse> call, Throwable t) {
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
