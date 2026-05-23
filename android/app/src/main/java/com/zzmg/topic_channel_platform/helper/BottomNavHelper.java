package com.zzmg.topic_channel_platform.helper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.zzmg.topic_channel_platform.CreatePostActivity;
import com.zzmg.topic_channel_platform.ForumListActivity;
import com.zzmg.topic_channel_platform.MainActivity;
import com.zzmg.topic_channel_platform.MeActivity;
import com.zzmg.topic_channel_platform.MyCollectsActivity;
import com.zzmg.topic_channel_platform.R;

public class BottomNavHelper {

    private BottomNavHelper() {}

    public static void setup(Activity activity, String current) {
        int primary = ContextCompat.getColor(activity, R.color.primary);
        int inactive = ContextCompat.getColor(activity, R.color.nav_inactive);

        // Forums
        {
            ImageView iv = activity.findViewById(R.id.iv_nav_forums);
            TextView tv = activity.findViewById(R.id.tv_nav_forums);
            boolean active = "forums".equals(current) || "forum_detail".equals(current);
            if (active) {
                iv.setColorFilter(primary, PorterDuff.Mode.SRC_IN);
                tv.setTextColor(primary);
            }
            activity.findViewById(R.id.nav_forums).setOnClickListener(v -> {
                if (!active) {
                    Intent intent = new Intent(activity, ForumListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                }
            });
        }

        // Home
        {
            ImageView iv = activity.findViewById(R.id.iv_nav_home);
            TextView tv = activity.findViewById(R.id.tv_nav_home);
            boolean active = "home".equals(current);
            if (active) {
                iv.setColorFilter(primary, PorterDuff.Mode.SRC_IN);
                tv.setTextColor(primary);
            }
            activity.findViewById(R.id.nav_home).setOnClickListener(v -> {
                if (!active) {
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                    activity.finish();
                }
            });
        }

        // Create
        activity.findViewById(R.id.btn_nav_create).setOnClickListener(v -> {
            if (!"create".equals(current)) {
                activity.startActivity(new Intent(activity, CreatePostActivity.class));
            }
        });

        // Collects
        {
            ImageView iv = activity.findViewById(R.id.iv_nav_collects);
            TextView tv = activity.findViewById(R.id.tv_nav_collects);
            boolean active = "collects".equals(current);
            if (active) {
                iv.setColorFilter(primary, PorterDuff.Mode.SRC_IN);
                tv.setTextColor(primary);
            }
            activity.findViewById(R.id.nav_collects).setOnClickListener(v -> {
                if (!active) {
                    Intent intent = new Intent(activity, MyCollectsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                }
            });
        }

        // Me
        {
            ImageView iv = activity.findViewById(R.id.iv_nav_me);
            TextView tv = activity.findViewById(R.id.tv_nav_me);
            boolean active = "me".equals(current);
            if (active) {
                iv.setColorFilter(primary, PorterDuff.Mode.SRC_IN);
                tv.setTextColor(primary);
            }
            activity.findViewById(R.id.nav_me).setOnClickListener(v -> {
                if (!active) {
                    Intent intent = new Intent(activity, MeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                }
            });
        }
    }
}
