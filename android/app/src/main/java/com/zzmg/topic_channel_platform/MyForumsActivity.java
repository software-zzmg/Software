package com.zzmg.topic_channel_platform;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zzmg.topic_channel_platform.adapter.ForumAdapter;
import com.zzmg.topic_channel_platform.api.UserApi;
import com.zzmg.topic_channel_platform.helper.BottomNavHelper;
import com.zzmg.topic_channel_platform.model.ForumListItem;
import com.zzmg.topic_channel_platform.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyForumsActivity extends AppCompatActivity {

    private ForumAdapter adapter;
    private TextView tvNoForums;
    private RecyclerView rvForums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_forums);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavHelper.setup(this, "my_forums");

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        tvNoForums = findViewById(R.id.tv_no_forums);
        rvForums = findViewById(R.id.rv_forums);
        rvForums.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ForumAdapter();
        rvForums.setAdapter(adapter);

        adapter.setOnItemClickListener(forum -> {
            Intent intent = new Intent(MyForumsActivity.this, ForumDetailActivity.class);
            intent.putExtra("forumId", forum.getId());
            startActivity(intent);
        });

        loadMyForums();
    }

    private void loadMyForums() {
        UserApi userApi = RetrofitClient.getInstance().create(UserApi.class);
        userApi.getMyForumsJoined().enqueue(new Callback<List<ForumListItem>>() {
            @Override
            public void onResponse(Call<List<ForumListItem>> call, Response<List<ForumListItem>> response) {
                if (response.code() == 401) {
                    Toast.makeText(MyForumsActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setForums(response.body());
                    boolean empty = response.body().isEmpty();
                    tvNoForums.setVisibility(empty ? View.VISIBLE : View.GONE);
                    rvForums.setVisibility(empty ? View.GONE : View.VISIBLE);
                } else {
                    tvNoForums.setVisibility(View.VISIBLE);
                    rvForums.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<ForumListItem>> call, Throwable t) {
                Toast.makeText(MyForumsActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
