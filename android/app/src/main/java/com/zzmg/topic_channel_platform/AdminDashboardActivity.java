package com.zzmg.topic_channel_platform;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.zzmg.topic_channel_platform.api.AdminApi;
import com.zzmg.topic_channel_platform.model.ApiResponse;
import com.zzmg.topic_channel_platform.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btn_back), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        findViewById(R.id.card_forums).setOnClickListener(v ->
                startActivity(new Intent(this, AdminPendingForumsActivity.class)));

        findViewById(R.id.card_posts).setOnClickListener(v ->
                startActivity(new Intent(this, AdminPendingPostsActivity.class)));

        findViewById(R.id.card_comments).setOnClickListener(v ->
                startActivity(new Intent(this, AdminPendingCommentsActivity.class)));

        findViewById(R.id.btn_logout).setOnClickListener(v -> doLogout());
    }

    private void doLogout() {
        AdminApi adminApi = RetrofitClient.getInstance().create(AdminApi.class);
        adminApi.logout().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Toast.makeText(AdminDashboardActivity.this, "已退出管理登录", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                finish();
            }
        });
    }
}
