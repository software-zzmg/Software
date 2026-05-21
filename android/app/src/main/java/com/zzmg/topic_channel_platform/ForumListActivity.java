package com.zzmg.topic_channel_platform;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zzmg.topic_channel_platform.adapter.ForumAdapter;
import com.zzmg.topic_channel_platform.api.ForumApi;
import com.zzmg.topic_channel_platform.helper.BottomNavHelper;
import com.zzmg.topic_channel_platform.model.ForumListItem;
import com.zzmg.topic_channel_platform.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForumListActivity extends AppCompatActivity {

    private ForumAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forum_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavHelper.setup(this, "forums");

        RecyclerView rvForums = findViewById(R.id.rv_forums);
        rvForums.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ForumAdapter();
        rvForums.setAdapter(adapter);

        adapter.setOnItemClickListener(forum -> {
            Intent intent = new Intent(ForumListActivity.this, ForumDetailActivity.class);
            intent.putExtra("forumId", forum.getId());
            startActivity(intent);
        });

        loadForums();
    }

    private void loadForums() {
        ForumApi forumApi = RetrofitClient.getInstance().create(ForumApi.class);
        forumApi.getForums().enqueue(new Callback<List<ForumListItem>>() {
            @Override
            public void onResponse(Call<List<ForumListItem>> call, Response<List<ForumListItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setForums(response.body());
                } else {
                    Toast.makeText(ForumListActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ForumListItem>> call, Throwable t) {
                Toast.makeText(ForumListActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
