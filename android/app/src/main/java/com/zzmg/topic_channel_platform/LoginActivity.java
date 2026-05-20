package com.zzmg.topic_channel_platform;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.zzmg.topic_channel_platform.api.UserApi;
import com.zzmg.topic_channel_platform.model.LoginRequest;
import com.zzmg.topic_channel_platform.model.LoginResponse;
import com.zzmg.topic_channel_platform.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etPhone, etPassword;
    private View tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        tvError = findViewById(R.id.tv_error);

        findViewById(R.id.btn_login).setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (phone.isEmpty() || password.isEmpty()) {
                showError("Please enter phone and password");
                return;
            }

            doLogin(phone, password);
        });
    }

    private void doLogin(String phone, String password) {
        UserApi userApi = RetrofitClient.getInstance().create(UserApi.class);
        userApi.login(new LoginRequest(phone, password)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse.isSuccess()) {
                        Toast.makeText(LoginActivity.this, "Welcome, " + loginResponse.getUserName(), Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        showError(loginResponse.getMessage());
                    }
                } else {
                    showError("Login failed");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        tvError.setVisibility(View.VISIBLE);
        ((android.widget.TextView) tvError).setText(message);
    }
}
