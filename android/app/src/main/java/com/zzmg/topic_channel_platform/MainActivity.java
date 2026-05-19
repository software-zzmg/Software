package com.zzmg.topic_channel_platform;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.zzmg.topic_channel_platform.api.TestApi;
import com.zzmg.topic_channel_platform.model.TestResponse;
import com.zzmg.topic_channel_platform.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvMessage = findViewById(R.id.tv_message);

        TestApi testApi = RetrofitClient.getInstance().create(TestApi.class);
        testApi.test().enqueue(new Callback<TestResponse>() {
            @Override
            public void onResponse(Call<TestResponse> call, Response<TestResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvMessage.setText(response.body().getMessage());
                } else {
                    tvMessage.setText("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TestResponse> call, Throwable t) {
                tvMessage.setText("Failed: " + t.getMessage());
            }
        });
    }
}
