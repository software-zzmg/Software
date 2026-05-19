package com.zzmg.topic_channel_platform;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zzmg.topic_channel_platform.adapter.PostAdapter;
import com.zzmg.topic_channel_platform.api.PostApi;
import com.zzmg.topic_channel_platform.model.PostListItem;
import com.zzmg.topic_channel_platform.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private PostAdapter adapter;

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

        RecyclerView rvPosts = findViewById(R.id.rv_posts);
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter();
        rvPosts.setAdapter(adapter);

        loadPosts();
    }

    private void loadPosts() {
        PostApi postApi = RetrofitClient.getInstance().create(PostApi.class);
        postApi.getPosts().enqueue(new Callback<List<PostListItem>>() {
            @Override
            public void onResponse(Call<List<PostListItem>> call, Response<List<PostListItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setPosts(response.body());
                } else {
                    Toast.makeText(MainActivity.this, "Error: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<PostListItem>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
