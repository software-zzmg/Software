package com.zzmg.topic_channel_platform.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zzmg.topic_channel_platform.R;
import com.zzmg.topic_channel_platform.model.ForumListItem;

import java.util.ArrayList;
import java.util.List;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ViewHolder> {

    private final List<ForumListItem> forums = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ForumListItem forum);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setForums(List<ForumListItem> forums) {
        this.forums.clear();
        if (forums != null) {
            this.forums.addAll(forums);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forum, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ForumListItem forum = forums.get(position);
        holder.tvName.setText(forum.getForumName());
        holder.tvDescription.setText(forum.getDescription());
        holder.tvCreator.setText("Created by " + forum.getCreatorName());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(forum);
            }
        });
    }

    @Override
    public int getItemCount() {
        return forums.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvCreator;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_forum_name);
            tvDescription = itemView.findViewById(R.id.tv_forum_desc);
            tvCreator = itemView.findViewById(R.id.tv_forum_creator);
        }
    }
}
