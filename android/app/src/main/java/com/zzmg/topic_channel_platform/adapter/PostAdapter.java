package com.zzmg.topic_channel_platform.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zzmg.topic_channel_platform.PostDetailActivity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zzmg.topic_channel_platform.R;
import com.zzmg.topic_channel_platform.model.PostListItem;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private final List<PostListItem> posts = new ArrayList<>();

    public void setPosts(List<PostListItem> posts) {
        this.posts.clear();
        if (posts != null) {
            this.posts.addAll(posts);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostListItem post = posts.get(position);
        holder.tvTitle.setText(post.getTitle());
        holder.tvAuthor.setText(post.getAuthorName());
        holder.tvForum.setText(post.getForumName());
        holder.tvContent.setText(post.getContent());
        holder.tvTime.setText(post.getPublishTime());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PostDetailActivity.class);
            intent.putExtra("postId", post.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvForum, tvContent, tvTime;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_post_title);
            tvAuthor = itemView.findViewById(R.id.tv_post_author);
            tvForum = itemView.findViewById(R.id.tv_post_forum);
            tvContent = itemView.findViewById(R.id.tv_post_content);
            tvTime = itemView.findViewById(R.id.tv_post_time);
        }
    }
}
