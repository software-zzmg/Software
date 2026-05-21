package com.zzmg.topic_channel_platform;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.zzmg.topic_channel_platform.api.UserApi;
import com.zzmg.topic_channel_platform.model.ApiResponse;
import com.zzmg.topic_channel_platform.model.UpdateProfileRequest;
import com.zzmg.topic_channel_platform.model.UserProfile;
import com.zzmg.topic_channel_platform.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etUserName, etRealName, etGender, etBirthday, etIdNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUserName = findViewById(R.id.et_user_name);
        etRealName = findViewById(R.id.et_real_name);
        etGender = findViewById(R.id.et_gender);
        etBirthday = findViewById(R.id.et_birthday);
        etIdNumber = findViewById(R.id.et_id_number);

        findViewById(R.id.btn_save).setOnClickListener(v -> saveProfile());

        loadProfile();
    }

    private void loadProfile() {
        UserApi userApi = RetrofitClient.getInstance().create(UserApi.class);
        userApi.getMe().enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.code() == 401) {
                    Toast.makeText(EditProfileActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile p = response.body();
                    etUserName.setText(p.getUserName());
                    etRealName.setText(p.getRealName());
                    etGender.setText(p.getGender());
                    etBirthday.setText(p.getBirthday());
                    etIdNumber.setText(p.getIdNumber());
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        UpdateProfileRequest request = new UpdateProfileRequest(
                etUserName.getText().toString().trim(),
                etRealName.getText().toString().trim(),
                etGender.getText().toString().trim(),
                etBirthday.getText().toString().trim(),
                etIdNumber.getText().toString().trim()
        );

        UserApi userApi = RetrofitClient.getInstance().create(UserApi.class);
        userApi.updateMe(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse res = response.body();
                    if (res.isSuccess()) {
                        Toast.makeText(EditProfileActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(EditProfileActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
