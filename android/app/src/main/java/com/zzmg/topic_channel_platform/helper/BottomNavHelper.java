package com.zzmg.topic_channel_platform.helper;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.zzmg.topic_channel_platform.MainActivity;
import com.zzmg.topic_channel_platform.MyCollectsActivity;
import com.zzmg.topic_channel_platform.R;

public class BottomNavHelper {

    private BottomNavHelper() {}

    public static void setup(Activity activity, String current) {
        activity.findViewById(R.id.btn_nav_home).setOnClickListener(v -> {
            if (!"home".equals(current)) {
                Intent intent = new Intent(activity, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
                activity.finish();
            }
        });

        activity.findViewById(R.id.btn_nav_collects).setOnClickListener(v -> {
            if (!"collects".equals(current)) {
                Intent intent = new Intent(activity, MyCollectsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
            }
        });

        activity.findViewById(R.id.btn_nav_me).setOnClickListener(v ->
                Toast.makeText(activity, "个人中心待实现", Toast.LENGTH_SHORT).show()
        );
    }
}
