package com.zzmg.topicchannelplatform.util;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component("timeFormatter")
public class TimeFormatter {

    public String format(LocalDateTime time) {
        if (time == null) {
            return "";
        }
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(time, now);
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (minutes < 60) {
            return Math.max(1, minutes) + "分钟前";
        }
        if (hours < 24) {
            return hours + "小时前";
        }
        if (days == 1) {
            return "昨天";
        }
        if (days == 2) {
            return "前天";
        }
        if (days <= 3) {
            return days + "天前";
        }
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public String formatDate(LocalDateTime time) {
        if (time == null) {
            return "";
        }
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
