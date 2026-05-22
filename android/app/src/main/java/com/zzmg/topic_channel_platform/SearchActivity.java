package com.zzmg.topic_channel_platform;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.zzmg.topic_channel_platform.api.SearchApi;
import com.zzmg.topic_channel_platform.helper.BottomNavHelper;
import com.zzmg.topic_channel_platform.model.ForumListItem;
import com.zzmg.topic_channel_platform.model.PostListItem;
import com.zzmg.topic_channel_platform.model.SearchResult;
import com.zzmg.topic_channel_platform.model.UserSearchItem;
import com.zzmg.topic_channel_platform.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private TextInputEditText etKeyword;
    private TextView tvPostsHeader, tvForumsHeader, tvUsersHeader, tvNoResults;
    private LinearLayout llPosts, llForums, llUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavHelper.setup(this, "search");

        etKeyword = findViewById(R.id.et_keyword);
        tvPostsHeader = findViewById(R.id.tv_posts_header);
        tvForumsHeader = findViewById(R.id.tv_forums_header);
        tvUsersHeader = findViewById(R.id.tv_users_header);
        tvNoResults = findViewById(R.id.tv_no_results);
        llPosts = findViewById(R.id.ll_posts);
        llForums = findViewById(R.id.ll_forums);
        llUsers = findViewById(R.id.ll_users);

        etKeyword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch();
                return true;
            }
            return false;
        });

        findViewById(R.id.btn_search).setOnClickListener(v -> doSearch());
    }

    private void doSearch() {
        String keyword = etKeyword.getText().toString().trim();
        if (keyword.isEmpty()) {
            return;
        }
        SearchApi searchApi = RetrofitClient.getInstance().create(SearchApi.class);
        searchApi.search(keyword).enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayResults(response.body());
                } else {
                    Toast.makeText(SearchActivity.this, "搜索失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayResults(SearchResult result) {
        llPosts.removeAllViews();
        llForums.removeAllViews();
        llUsers.removeAllViews();

        List<PostListItem> posts = result.getPosts();
        List<ForumListItem> forums = result.getForums();
        List<UserSearchItem> users = result.getUsers();

        if (posts != null && !posts.isEmpty()) {
            tvPostsHeader.setVisibility(View.VISIBLE);
            for (PostListItem post : posts) {
                MaterialButton btn = createResultButton(post.getTitle());
                btn.setOnClickListener(v -> {
                    Intent intent = new Intent(SearchActivity.this, PostDetailActivity.class);
                    intent.putExtra("postId", post.getId());
                    startActivity(intent);
                });
                llPosts.addView(btn);
            }
        } else {
            tvPostsHeader.setVisibility(View.GONE);
        }

        if (forums != null && !forums.isEmpty()) {
            tvForumsHeader.setVisibility(View.VISIBLE);
            for (ForumListItem forum : forums) {
                MaterialButton btn = createResultButton(forum.getForumName());
                btn.setOnClickListener(v -> {
                    Intent intent = new Intent(SearchActivity.this, ForumDetailActivity.class);
                    intent.putExtra("forumId", forum.getId());
                    startActivity(intent);
                });
                llForums.addView(btn);
            }
        } else {
            tvForumsHeader.setVisibility(View.GONE);
        }

        if (users != null && !users.isEmpty()) {
            tvUsersHeader.setVisibility(View.VISIBLE);
            for (UserSearchItem user : users) {
                String label = user.getUserName() + " (" + user.getUserId() + ")";
                MaterialButton btn = createResultButton(label);
                btn.setEnabled(false);
                llUsers.addView(btn);
            }
        } else {
            tvUsersHeader.setVisibility(View.GONE);
        }

        boolean hasResults = !isEmpty(posts) || !isEmpty(forums) || !isEmpty(users);
        tvNoResults.setVisibility(hasResults ? View.GONE : View.VISIBLE);
    }

    private MaterialButton createResultButton(String text) {
        MaterialButton btn = new MaterialButton(this);
        btn.setText(text);
        btn.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = 4;
        btn.setLayoutParams(params);
        return btn;
    }

    private boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }
}
