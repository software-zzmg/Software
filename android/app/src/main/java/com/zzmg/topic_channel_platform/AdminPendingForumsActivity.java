package com.zzmg.topic_channel_platform;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.zzmg.topic_channel_platform.api.AdminApi;
import com.zzmg.topic_channel_platform.model.ApiResponse;
import com.zzmg.topic_channel_platform.model.PendingForumItem;
import com.zzmg.topic_channel_platform.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPendingForumsActivity extends AppCompatActivity {

    private RecyclerView rvItems;
    private TextView tvEmpty;
    private ForumAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_pending_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btn_back), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        ((TextView) findViewById(R.id.tv_title)).setText("待审核频道");

        tvEmpty = findViewById(R.id.tv_empty);
        rvItems = findViewById(R.id.rv_items);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ForumAdapter();
        rvItems.setAdapter(adapter);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this::loadData);

        loadData();
    }

    private void loadData() {
        AdminApi adminApi = RetrofitClient.getInstance().create(AdminApi.class);
        adminApi.getPendingForums().enqueue(new Callback<List<PendingForumItem>>() {
            @Override
            public void onResponse(Call<List<PendingForumItem>> call, Response<List<PendingForumItem>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.code() == 401) {
                    Toast.makeText(AdminPendingForumsActivity.this, "请先进行管理员登录", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AdminPendingForumsActivity.this, AdminLoginActivity.class));
                    finish();
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setItems(response.body());
                    tvEmpty.setVisibility(response.body().isEmpty() ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<PendingForumItem>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(AdminPendingForumsActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.VH> {
        private final List<PendingForumItem> items = new ArrayList<>();

        void setItems(List<PendingForumItem> items) {
            this.items.clear();
            if (items != null) this.items.addAll(items);
            notifyDataSetChanged();
        }

        void removeItem(int position) {
            items.remove(position);
            notifyItemRemoved(position);
            tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_admin_pending, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            PendingForumItem item = items.get(position);
            h.tvTitle.setText(item.getForumName());
            h.tvBody.setText(item.getDescription());
            h.tvMeta.setText("创建者: " + item.getCreatorName());

            h.btnApprove.setOnClickListener(v -> audit(item.getId(), true, h.getAdapterPosition()));
            h.btnReject.setOnClickListener(v -> audit(item.getId(), false, h.getAdapterPosition()));
        }

        @Override
        public int getItemCount() { return items.size(); }

        private void audit(Long id, boolean approved, int position) {
            AdminApi api = RetrofitClient.getInstance().create(AdminApi.class);
            Call<ApiResponse> call = approved ? api.approveForum(id) : api.rejectForum(id);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.code() == 401) {
                        Toast.makeText(AdminPendingForumsActivity.this, "请先进行管理员登录", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(AdminPendingForumsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        removeItem(position);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(AdminPendingForumsActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        class VH extends RecyclerView.ViewHolder {
            TextView tvTitle, tvBody, tvMeta;
            View btnApprove, btnReject;
            VH(View v) {
                super(v);
                tvTitle = v.findViewById(R.id.tv_item_title);
                tvBody = v.findViewById(R.id.tv_item_body);
                tvMeta = v.findViewById(R.id.tv_item_meta);
                btnApprove = v.findViewById(R.id.btn_approve);
                btnReject = v.findViewById(R.id.btn_reject);
            }
        }
    }
}
