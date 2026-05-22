package com.zzmg.topic_channel_platform;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.zzmg.topic_channel_platform.api.UserApi;
import com.zzmg.topic_channel_platform.helper.BottomNavHelper;
import com.zzmg.topic_channel_platform.model.ApiResponse;
import com.zzmg.topic_channel_platform.model.UserProfile;
import com.zzmg.topic_channel_platform.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeActivity extends AppCompatActivity {

    private static final int REQUEST_LOGIN = 1;
    private static final int REQUEST_EDIT_PROFILE = 2;
    private static final int REQUEST_CHANGE_PASSWORD = 3;

    private View llLoggedIn, llNotLoggedIn;
    private TextView tvUserName, tvPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_me);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavHelper.setup(this, "me");

        llLoggedIn = findViewById(R.id.ll_logged_in);
        llNotLoggedIn = findViewById(R.id.ll_not_logged_in);
        tvUserName = findViewById(R.id.tv_user_name);
        tvPhoneNumber = findViewById(R.id.tv_phone_number);

        findViewById(R.id.btn_edit_profile).setOnClickListener(v ->
                startActivityForResult(new Intent(MeActivity.this, EditProfileActivity.class), REQUEST_EDIT_PROFILE));

        findViewById(R.id.btn_change_password).setOnClickListener(v ->
                startActivityForResult(new Intent(MeActivity.this, ChangePasswordActivity.class), REQUEST_CHANGE_PASSWORD));

        findViewById(R.id.btn_my_forums).setOnClickListener(v ->
                startActivity(new Intent(MeActivity.this, MyForumsActivity.class)));

        findViewById(R.id.btn_my_collects).setOnClickListener(v ->
                startActivity(new Intent(MeActivity.this, MyCollectsActivity.class)));

        findViewById(R.id.btn_delete_account).setOnClickListener(v -> confirmDelete());

        findViewById(R.id.btn_logout).setOnClickListener(v -> doLogout());
        findViewById(R.id.btn_login).setOnClickListener(v -> {
            Intent intent = new Intent(MeActivity.this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_LOGIN);
        });

        loadProfile();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadProfile();
        }
    }

    private void loadProfile() {
        UserApi userApi = RetrofitClient.getInstance().create(UserApi.class);
        userApi.getMe().enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile profile = response.body();
                    tvUserName.setText(profile.getUserName());
                    tvPhoneNumber.setText(profile.getPhoneNumber());
                    llLoggedIn.setVisibility(View.VISIBLE);
                    llNotLoggedIn.setVisibility(View.GONE);
                } else if (response.code() == 401) {
                    llLoggedIn.setVisibility(View.GONE);
                    llNotLoggedIn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Toast.makeText(MeActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("注销账号")
                .setMessage("此操作将永久删除您的账号，确定继续？")
                .setPositiveButton("确定", (dialog, which) -> doDelete())
                .setNegativeButton("取消", null)
                .show();
    }

    private void doDelete() {
        UserApi userApi = RetrofitClient.getInstance().create(UserApi.class);
        userApi.deleteMe().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RetrofitClient.clearCookies();
                    llLoggedIn.setVisibility(View.GONE);
                    llNotLoggedIn.setVisibility(View.VISIBLE);
                    Toast.makeText(MeActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(MeActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doLogout() {
        UserApi userApi = RetrofitClient.getInstance().create(UserApi.class);
        userApi.logout().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                RetrofitClient.clearCookies();
                llLoggedIn.setVisibility(View.GONE);
                llNotLoggedIn.setVisibility(View.VISIBLE);
                Toast.makeText(MeActivity.this, "已退出登录", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(MeActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
