package com.zzmg.topic_channel_platform.helper;

import android.app.Activity;
import android.content.Intent;

import com.zzmg.topic_channel_platform.CreatePostActivity;
import com.zzmg.topic_channel_platform.ForumListActivity;
import com.zzmg.topic_channel_platform.MainActivity;
import com.zzmg.topic_channel_platform.MeActivity;
import com.zzmg.topic_channel_platform.R;

public class BottomNavHelper {

    private BottomNavHelper() {}

    public static void setup(Activity activity, String current) {
        activity.findViewById(R.id.btn_nav_create).setOnClickListener(v -> {
            if (!"create".equals(current)) {
                activity.startActivity(new Intent(activity, CreatePostActivity.class));
            }
        });

        activity.findViewById(R.id.btn_nav_home).setOnClickListener(v -> {
            if (!"home".equals(current)) {
                Intent intent = new Intent(activity, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
                activity.finish();
            }
        });

        activity.findViewById(R.id.btn_nav_forums).setOnClickListener(v -> {
            if (!"forums".equals(current) && !"forum_detail".equals(current)) {
                Intent intent = new Intent(activity, ForumListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
            }
        });

        activity.findViewById(R.id.btn_nav_me).setOnClickListener(v -> {
            if (!"me".equals(current)) {
                Intent intent = new Intent(activity, MeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
            }
        });
    }
}
