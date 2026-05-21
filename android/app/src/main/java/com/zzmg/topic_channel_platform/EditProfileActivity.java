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
import com.zzmg.topic_channel_platform.api.UserApi;
import com.zzmg.topic_channel_platform.model.ApiResponse;
import com.zzmg.topic_channel_platform.model.UpdateProfileRequest;
import com.zzmg.topic_channel_platform.model.UserProfile;
import com.zzmg.topic_channel_platform.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputLayout tilUserName, tilBirthday, tilIdNumber;
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

        tilUserName = findViewById(R.id.til_user_name);
        tilBirthday = findViewById(R.id.til_birthday);
        tilIdNumber = findViewById(R.id.til_id_number);
        etUserName = findViewById(R.id.et_user_name);
        etRealName = findViewById(R.id.et_real_name);
        etGender = findViewById(R.id.et_gender);
        etBirthday = findViewById(R.id.et_birthday);
        etIdNumber = findViewById(R.id.et_id_number);

        etUserName.addTextChangedListener(new ClearErrorWatcher(tilUserName));
        etBirthday.addTextChangedListener(new ClearErrorWatcher(tilBirthday));
        etIdNumber.addTextChangedListener(new ClearErrorWatcher(tilIdNumber));

        findViewById(R.id.btn_save).setOnClickListener(v -> {
            if (validateForm()) saveProfile();
        });

        loadProfile();
    }

    private boolean validateForm() {
        boolean valid = true;
        String userName = etUserName.getText().toString().trim();
        String birthday = etBirthday.getText().toString().trim();
        String idNumber = etIdNumber.getText().toString().trim();

        if (userName.isEmpty()) {
            tilUserName.setError("用户名不能为空");
            valid = false;
        } else if (userName.length() > 50) {
            tilUserName.setError("用户名不能超过50字");
            valid = false;
        }

        if (!birthday.isEmpty()) {
            if (!birthday.matches("\\d{4}-\\d{2}-\\d{2}")) {
                tilBirthday.setError("生日格式不正确，应为 yyyy-MM-dd");
                valid = false;
            } else {
                String[] parts = birthday.split("-");
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2]);
                if (month < 1 || month > 12) {
                    tilBirthday.setError("月份必须在 1-12 之间");
                    valid = false;
                } else {
                    int maxDay;
                    switch (month) {
                        case 2:
                            int year = Integer.parseInt(parts[0]);
                            boolean leap = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
                            maxDay = leap ? 29 : 28;
                            break;
                        case 4: case 6: case 9: case 11: maxDay = 30; break;
                        default: maxDay = 31;
                    }
                    if (day < 1 || day > maxDay) {
                        tilBirthday.setError("日期不正确，该月只有" + maxDay + "天");
                        valid = false;
                    }
                }
            }
        }

        if (!idNumber.isEmpty() && !idNumber.matches("^(\\d{17}[\\dXx])?$")) {
            tilIdNumber.setError("身份证号格式不正确");
            valid = false;
        }

        return valid;
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
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
